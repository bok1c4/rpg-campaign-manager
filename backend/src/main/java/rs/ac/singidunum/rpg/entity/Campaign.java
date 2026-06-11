package rs.ac.singidunum.rpg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 120)
    private String setting;

    /** Vlasnik kampanje (Game Master). ManyToOne -> OneToMany sa strane korisnika. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_master_id")
    private User gameMaster;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameCharacter> characters = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameSession> sessions = new ArrayList<>();

    /** Party roster — čista ManyToMany veza kampanja <-> igrači. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "campaign_players",
            joinColumns = @JoinColumn(name = "campaign_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> players = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
