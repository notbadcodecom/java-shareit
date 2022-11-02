package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "item_requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "description", nullable = false)
    String description;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    User requester;

    @OneToMany(
            mappedBy = "itemRequest",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    List<Item> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    LocalDateTime created;
}