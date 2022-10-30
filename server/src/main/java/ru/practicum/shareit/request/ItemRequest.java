package ru.practicum.shareit.request;

import lombok.*;
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
@Table(name = "item_requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;

    @OneToMany(
            mappedBy = "itemRequest",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<Item> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    private LocalDateTime created;
}