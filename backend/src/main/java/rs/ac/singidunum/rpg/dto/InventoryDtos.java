package rs.ac.singidunum.rpg.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryDtos {

    public record AddItemRequest(
            @NotNull Long itemId,
            @Min(1) Integer quantity,
            Boolean equipped) {
    }

    public record UpdateEntryRequest(
            @Min(1) Integer quantity,
            Boolean equipped) {
    }

    public record Response(
            Long id,
            ItemDtos.Response item,
            int quantity,
            boolean equipped) {
    }
}
