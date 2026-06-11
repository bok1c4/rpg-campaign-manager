package rs.ac.singidunum.rpg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Lik (PC). Naziv klase je GameCharacter da se ne sudara sa java.lang.Character;
 * tabela je "characters".
 */
@Entity
@Table(name = "characters")
@Getter
@Setter
@NoArgsConstructor
public class GameCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String race;

    @Column(name = "char_class")
    private String characterClass;

    private int level = 1;

    @Column(name = "hit_points")
    private int hitPoints = 10;

    @Column(length = 2000)
    private String backstory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    /** Vlasnik lika (igrač). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id")
    private User player;

    /** Inventar — ManyToMany sa predmetima realizovan kao entitet (nosi quantity/equipped). */
    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterItem> inventory = new ArrayList<>();
}
