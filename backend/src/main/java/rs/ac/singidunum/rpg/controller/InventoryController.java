package rs.ac.singidunum.rpg.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.singidunum.rpg.dto.InventoryDtos;
import rs.ac.singidunum.rpg.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/characters/{characterId}/items")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<InventoryDtos.Response> list(@PathVariable Long characterId) {
        return inventoryService.list(characterId);
    }

    @PostMapping
    public ResponseEntity<InventoryDtos.Response> add(@PathVariable Long characterId,
                                                      @Valid @RequestBody InventoryDtos.AddItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.add(characterId, req));
    }

    @PutMapping("/{entryId}")
    public InventoryDtos.Response update(@PathVariable Long characterId, @PathVariable Long entryId,
                                         @Valid @RequestBody InventoryDtos.UpdateEntryRequest req) {
        return inventoryService.update(characterId, entryId, req);
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> remove(@PathVariable Long characterId, @PathVariable Long entryId) {
        inventoryService.remove(characterId, entryId);
        return ResponseEntity.noContent().build();
    }
}
