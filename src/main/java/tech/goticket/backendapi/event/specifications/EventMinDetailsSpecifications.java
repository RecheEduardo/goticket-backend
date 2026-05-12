package tech.goticket.backendapi.event.specifications;

import org.springframework.data.jpa.domain.Specification;
import tech.goticket.backendapi.event.view.EventMinDetailsView;

public class EventMinDetailsSpecifications {
    public static Specification<EventMinDetailsView> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<EventMinDetailsView> hasCategory(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("categoryId"), categoryId);
        };
    }

    public static Specification<EventMinDetailsView> hasStartingPrice(Double startingPrice) {
        return (root, query, cb) -> {
            if (startingPrice == null) return null;
            return cb.greaterThanOrEqualTo(root.get("startingPrice"), startingPrice);
        };
    }

    public static Specification<EventMinDetailsView> hasVenueState(String venueState) {
        return (root, query, cb) -> {
            if (venueState == null || venueState.isBlank()) return null;
            return cb.like(cb.lower(root.get("venueState")), "%" + venueState.toLowerCase() + "%");
        };
    }

    public static Specification<EventMinDetailsView> hasVenueCity(String venueCity) {
        return (root, query, cb) -> {
            if (venueCity == null || venueCity.isBlank()) return null;
            return cb.like(cb.lower(root.get("venueState")), "%" + venueCity.toLowerCase() + "%");
        };
    }


}
