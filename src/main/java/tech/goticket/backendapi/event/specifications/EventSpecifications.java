package tech.goticket.backendapi.event.specifications;

import org.springframework.data.jpa.domain.Specification;
import tech.goticket.backendapi.event.Event;

public class EventSpecifications {
    public static Specification<Event> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Event> hasCategory(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("category").get("categoryId"), categoryId);
        };
    }

    public static Specification<Event> hasStatus(Long statusId) {
        return (root, query, cb) -> {
            if (statusId == null) return null;
            return cb.equal(root.get("status").get("statusId"), statusId);
        };
    }

    public static Specification<Event> hasVenueState(String venueState) {
        return (root, query, cb) -> {
            if (venueState == null || venueState.isBlank()) return null;
            return cb.like(cb.lower(root.get("venue").get("state")), "%" + venueState.toLowerCase() + "%");
        };
    }

    public static Specification<Event> hasVenueCity(String venueCity) {
        return (root, query, cb) -> {
            if (venueCity == null || venueCity.isBlank()) return null;
            return cb.like(cb.lower(root.get("venue").get("city")), "%" + venueCity.toLowerCase() + "%");
        };
    }
}
