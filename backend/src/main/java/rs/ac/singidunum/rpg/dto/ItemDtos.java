package rs.ac.singidunum.rpg.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rs.ac.singidunum.rpg.entity.ItemType;
import rs.ac.singidunum.rpg.entity.Rarity;

public class ItemDtos {

    public record CreateRequest(
            @NotBlank String name,
            @NotNull ItemType itemType,
            @NotNull Rarity rarity,
            @Min(0) int goldValue,
            String description) {
    }

    public record UpdateRequest(
            @NotBlank String name,
            @NotNull ItemType itemType,
            @NotNull Rarity rarity,
            @Min(0) int goldValue,
            String description) {
    }

    public record Response(
            Long id,
            String name,
            ItemType itemType,
            Rarity rarity,
            int goldValue,
            String description) {
    }
}
