package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final LocalDateTime now = LocalDateTime.now().withNano(0);

    @Test
    @DisplayName("POST create booking when end less start at /bookings")
    void createEndLessThanStartValidation() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content("{\"itemId\": 1, " +
                                "\"bookerId\": 1, " +
                                "\"start\": \"" + now.plusDays(20) + "\", " +
                                "\"end\": \"" + now.plusDays(10) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Validation error']")
                        .value("end cannot be after the start"));
    }

    @Test
    @DisplayName("POST create booking when end and start at past /bookings")
    void createEndAndStartInPastValidation() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content("{\"itemId\": 1, " +
                                "\"bookerId\": 1, " +
                                "\"start\": \"" + now.minusDays(20) + "\", " +
                                "\"end\": \"" + now.minusDays(10) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.start").value("start cannot be in the past"))
                .andExpect(jsonPath("$.end").value("end cannot be in the past"));
    }
}
