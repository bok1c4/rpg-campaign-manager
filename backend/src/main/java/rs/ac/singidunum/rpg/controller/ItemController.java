package rs.ac.singidunum.rpg.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.singidunum.rpg.dto.ItemDtos;
import rs.ac.singidunum.rpg.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDtos.Response> list() {
        return itemService.list();
    }

    @GetMapping("/{id}")
    public ItemDtos.Response get(@PathVariable Long id) {
        return itemService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('GAME_MASTER')")
    public ResponseEntity<ItemDtos.Response> create(@Valid @RequestBody ItemDtos.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GAME_MASTER')")
    public ItemDtos.Response update(@PathVariable Long id, @Valid @RequestBody ItemDtos.UpdateRequest req) {
        return itemService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GAME_MASTER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
