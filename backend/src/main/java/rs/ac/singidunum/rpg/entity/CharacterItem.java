package rs.ac.singidunum.rpg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Spojni entitet inventara (ManyToMany lik <-> predmet) sa dodatnim atributima
 * quantity i equipped.
 */
@Entity
@Table(name = "character_items",
        uniqueConstraints = @UniqueConstraint(name = "uq_character_item",
                columnNames = {"character_id", "item_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CharacterItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "character_id")
    private GameCharacter character;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity = 1;

    private boolean equipped = false;
}
