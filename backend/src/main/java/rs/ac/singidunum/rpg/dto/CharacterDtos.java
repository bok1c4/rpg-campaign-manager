package rs.ac.singidunum.rpg.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CharacterDtos {

    public record CreateRequest(
            @NotBlank String name,
            String race,
            String characterClass,
            @Min(1) Integer level,
            @Min(0) Integer hitPoints,
            String backstory,
            @NotNull Long campaignId) {
    }

    public record UpdateRequest(
            @NotBlank String name,
            String race,
            String characterClass,
            @Min(1) Integer level,
            @Min(0) Integer hitPoints,
            String backstory) {
    }

    public record Response(
            Long id,
            String name,
            String race,
            String characterClass,
            int level,
            int hitPoints,
            String backstory,
            Long campaignId,
            String campaignTitle,
            UserDtos.Response player,
            int itemCount) {
    }
}
