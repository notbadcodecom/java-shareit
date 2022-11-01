package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingAdvancedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final String userHeader = "X-Sharer-User-Id";
    private UserDto booker;
    private UserDto owner;
    private ItemDto item;
    private final LocalDateTime now = LocalDateTime.now().withNano(0);

    @BeforeEach
    void setUp() throws Exception {
        String uniqEmail = (int)(Math.random() * (99999) + 1) + "@mail.com";
        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Booker Name\", " +
                                "\"email\": \"booker" + uniqEmail + "\"}"))
                .andReturn();
        booker = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Owner Name\", " +
                                 "\"email\": \"owner" + uniqEmail + "\"}"))
                .andReturn();
        owner = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        mvcResult = mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, owner.getId())
                .content("{\"name\": \"Portal Gun\", " +
                        "\"description\": \"Gadget that allows to travel\", " +
                        "\"available\": true}"))
                .andReturn();
        item = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ItemDto.class);
    }

    @Test
    @DisplayName("POST create booking at /bookings")
    void create() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId())
                        .content("{\"itemId\": " + item.getId() + ", " +
                                "\"bookerId\": " + booker.getId() + ", " +
                                "\"start\": \"" + now.plusMinutes(1) + "\", " +
                                "\"end\": \"" + now.plusDays(10) + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("GET booking of booker by all state /bookings")
    void getBookingsByStateOfBooker() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"start\": \"" + now.plusMinutes(1) + "\", " +
                        "\"end\": \"" + now.plusDays(10) + "\"}"));
        mockMvc.perform(get("/bookings?state=all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking of booker by current state /bookings")
    void getBookingsOfBookerByStateCurrent() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings?state=current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"start\": \"" + LocalDateTime.now().plusSeconds(1) + "\", " +
                        "\"end\": \"" + LocalDateTime.now().plusDays(1) + "\"}"));
        TimeUnit.SECONDS.sleep(2);
        mockMvc.perform(get("/bookings?state=current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking of booker by past state /bookings")
    void getBookingsOfBookerByStatePast() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings?state=past")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"start\": \"" + LocalDateTime.now().plusSeconds(1) + "\", " +
                        "\"end\": \"" + LocalDateTime.now().plusSeconds(2) + "\"}"));
        TimeUnit.SECONDS.sleep(3);
        mockMvc.perform(get("/bookings?state=past")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking of booker by future state /bookings")
    void getBookingsOfBookerByStateFuture() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings?state=future")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"start\": \"" + now.plusDays(1) + "\", " +
                        "\"end\": \"" + now.plusDays(2) + "\"}"));
        mockMvc.perform(get("/bookings?state=future")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking of booker by unsupported state /bookings")
    void getBookingsOfBookerByStateUnsupported() throws Exception {
        mockMvc.perform(get("/bookings?state=wtf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"error\":\"Unknown state: wtf\"}"));
    }

    @Test
    @DisplayName("GET booking of booker by default state /bookings")
    void getBookingsOfBookerByStateDefault() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings?state=waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"status\": \"WAITING\", " +
                        "\"start\": \"" + LocalDateTime.now().plusSeconds(1) + "\", " +
                        "\"end\": \"" + LocalDateTime.now().plusDays(2) + "\"}"));
        mockMvc.perform(get("/bookings?state=waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking of owner by all state /bookings/owner")
    void getBookingsByStateOfOwner() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"start\": \"" + now.plusMinutes(1) + "\", " +
                        "\"end\": \"" + now.plusDays(10) + "\"}"));
        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking of owner by current state /bookings")
    void getBookingsOfOwnerByStateCurrent() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings/owner?state=current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings/")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"start\": \"" + LocalDateTime.now().plusSeconds(1) + "\", " +
                        "\"end\": \"" + LocalDateTime.now().plusDays(1) + "\"}"));
        TimeUnit.SECONDS.sleep(2);
        mockMvc.perform(get("/bookings/owner?state=current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking of owner by past state /bookings")
    void getBookingsOfOwnerByStatePast() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings/owner?state=past")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"start\": \"" + LocalDateTime.now().plusSeconds(1) + "\", " +
                        "\"end\": \"" + LocalDateTime.now().plusSeconds(2) + "\"}"));
        TimeUnit.SECONDS.sleep(3);
        mockMvc.perform(get("/bookings/owner?state=past")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking of owner by future state /bookings")
    void getBookingsOfOwnerByStateFuture() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings/owner?state=future")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"start\": \"" + now.plusDays(1) + "\", " +
                        "\"end\": \"" + now.plusDays(2) + "\"}"));
        mockMvc.perform(get("/bookings/owner?state=future")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking of owner by unsupported state /bookings")
    void getBookingsOfOwnerByStateUnsupported() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=wtf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"error\":\"Unknown state: wtf\"}"));
    }

    @Test
    @DisplayName("GET booking of owner by default state /bookings")
    void getBookingsOfOwnerByStateDefault() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bookings/owner?state=waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andReturn();
        List<BookingDto> before = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<BookingDto>>(){});
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(userHeader, booker.getId())
                .content("{\"itemId\": " + item.getId() + ", " +
                        "\"bookerId\": " + booker.getId() + ", " +
                        "\"status\": \"WAITING\", " +
                        "\"start\": \"" + LocalDateTime.now().plusSeconds(1) + "\", " +
                        "\"end\": \"" + LocalDateTime.now().plusDays(2) + "\"}"));
        mockMvc.perform(get("/bookings/owner?state=waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andExpect(jsonPath("$.length()").value(before.size() + 1));
    }

    @Test
    @DisplayName("GET booking when user not owner and not booker /bookings")
    void getBookingNotOwnerNotBooker_throw404Error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"User Name\", " +
                                "\"email\": \"user@mail.com\"}"))
                .andReturn();
        UserDto user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        mvcResult = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, booker.getId())
                        .content("{\"itemId\": " + item.getId() + ", " +
                                "\"bookerId\": " + booker.getId() + ", " +
                                "\"start\": \"" + now.plusSeconds(2) + "\", " +
                                "\"end\": \"" + now.plusDays(10) + "\"}"))
                .andReturn();
        BookingAdvancedDto booking = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), BookingAdvancedDto.class);
        mockMvc.perform(get("/bookings/" + booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, user.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found booking #" + booking.getId()));
    }

    @Test
    @DisplayName("PUT booking at /bookings")
    void putMethodAtBooking_throw405Error() throws Exception {
        mockMvc.perform(put("/bookings/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error").value("request method 'put' not supported"));
    }

    @Test
    @DisplayName("POST wrong json booking at /bookings")
    void postWrongDataAtBooking_throw500Error() throws Exception {
        mockMvc.perform(post("/bookings/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, owner.getId()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("internal server error"));
    }
}