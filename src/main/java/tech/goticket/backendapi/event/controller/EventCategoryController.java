package tech.goticket.backendapi.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.goticket.backendapi.event.dto.EventCategoryDTO;
import tech.goticket.backendapi.event.service.EventCategoryService;

import java.util.List;

@RestController
@RequestMapping("/event-categories")
@RequiredArgsConstructor
public class EventCategoryController {

    private final EventCategoryService eventCategoryService;

    @GetMapping
    public ResponseEntity<List<EventCategoryDTO>> getAllEventCategories() {
        var eventCategories = eventCategoryService.findAllCategories();
        return ResponseEntity.ok(eventCategories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<EventCategoryDTO> getEventCategoryById(@PathVariable Long categoryId) {
        EventCategoryDTO category = eventCategoryService.findCategoryById(categoryId);

        return ResponseEntity.ok(category);
    }
}
