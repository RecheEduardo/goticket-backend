package tech.goticket.backendapi.event.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.event.EventCategory;
import tech.goticket.backendapi.event.dto.EventCategoryDTO;
import tech.goticket.backendapi.event.repository.EventCategoryRepository;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class EventCategoryService {
    @Autowired
    private EventCategoryRepository eventCategoryRepository;

    @Transactional
    public EventCategoryDTO findCategoryById(Long categoryId) {
        EventCategory category = eventCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        return new EventCategoryDTO(category.getCategoryId(),
                                    category.getName(),
                                    category.getSlug());
    }

    @Transactional
    public List<EventCategoryDTO> findAllCategories() {
        return eventCategoryRepository.findAll()
                .stream()
                .map(ec -> new EventCategoryDTO(ec.getCategoryId(), ec.getName(), ec.getSlug()))
                .toList();
    }
}
