package rs.ac.singidunum.rpg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", length = 20)
    private ItemType itemType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Rarity rarity;

    @Column(name = "gold_value")
    private int goldValue;

    @Column(length = 1000)
    private String description;
}
