package dev.ivan.reviewverso_back.lists;

import dev.ivan.reviewverso_back.lists.dtos.ListRequestDTO;
import dev.ivan.reviewverso_back.lists.dtos.ListResponseDTO;
import dev.ivan.reviewverso_back.lists.service.ListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "${api-endpoint}/lists")
@RequiredArgsConstructor
public class ListController {

    private final ListService listService;

    @PostMapping
    public ResponseEntity<ListResponseDTO> createList(@RequestBody ListRequestDTO dto) {
        ListResponseDTO created = listService.createEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ListResponseDTO>> getAllLists() {
        return ResponseEntity.ok(listService.getEntities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListResponseDTO> getListById(@PathVariable Long id) {
        return ResponseEntity.ok(listService.getByID(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ListResponseDTO>> getListsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(listService.getListsByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListResponseDTO> updateList(
            @PathVariable Long id,
            @RequestBody ListRequestDTO dto) {
        return ResponseEntity.ok(listService.updateEntity(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        listService.deleteEntity(id);
        return ResponseEntity.noContent().build();
    }
}
