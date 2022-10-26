package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i " +
            "where i.available = true " +
            "and (upper(i.name) like concat('%',upper(:text),'%') " +
            "    or upper(i.description) like concat('%',upper(:text),'%'))")
    List<Item> findAvailableByNameOrDescription(@Param("text") String text, Pageable pageable);

    List<Item> findByOwner_IdOrderByIdAsc(Long id, Pageable pageable);
}
