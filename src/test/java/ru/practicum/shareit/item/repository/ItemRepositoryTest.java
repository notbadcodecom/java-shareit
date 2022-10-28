package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.FromSizeRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("Find available items by text")
    void findAvailableByNameOrDescription() {
        // Assign
        int itemCount = 10;
        for (int i = 0; i < itemCount; i++) {
            Item item = Item.builder()
                    .name((i % 3 == 0) ? "multiple of three" : "Item #" + i)
                    .description((i % 2 == 0) ? "multiple of two" : "Item #" + i)
                    .available(i != 9)
                    .build();
            itemRepository.save(item);
        }
        Pageable pageable = FromSizeRequest.of(0, itemCount);

        // Act
        String text = "thREe";
        List<Item> itemsOfThree = itemRepository.findAvailableByNameOrDescription(text, pageable);
        text = "Two";
        List<Item> itemsOfTwo = itemRepository.findAvailableByNameOrDescription(text, pageable);
        text = "multiple";
        List<Item> itemsOfMultiple = itemRepository.findAvailableByNameOrDescription(text, pageable);

        // Asserts
        assertThat(itemsOfThree.size()).isEqualTo(3);
        assertThat(itemsOfTwo.size()).isEqualTo(5);
        assertThat(itemsOfMultiple.size()).isEqualTo(6);
    }
}
