package tech.goticket.backendapi.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Entity
@Table(name = "tb_event_categories")
@Getter
@Setter
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @PrePersist
    @PreUpdate
    private void autoSlug() {
        this.slug = makeSlug(this.name);
    }

    public String makeSlug(String input) {
        if (input == null) return null;

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");

        return slug.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }
}
