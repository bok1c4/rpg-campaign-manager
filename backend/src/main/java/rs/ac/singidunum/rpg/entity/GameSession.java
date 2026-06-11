package rs.ac.singidunum.rpg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "game_sessions")
@Getter
@Setter
@NoArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(name = "session_number")
    private int sessionNumber;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String summary;

    @Column(name = "played_on")
    private LocalDate playedOn;
}
