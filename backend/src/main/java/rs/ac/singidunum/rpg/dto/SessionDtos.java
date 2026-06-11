package rs.ac.singidunum.rpg.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class SessionDtos {

    public record CreateRequest(
            @Min(0) int sessionNumber,
            @NotBlank String title,
            String summary,
            LocalDate playedOn) {
    }

    public record UpdateRequest(
            @Min(0) int sessionNumber,
            @NotBlank String title,
            String summary,
            LocalDate playedOn) {
    }

    public record Response(
            Long id,
            Long campaignId,
            int sessionNumber,
            String title,
            String summary,
            LocalDate playedOn) {
    }
}
