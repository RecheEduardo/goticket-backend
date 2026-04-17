package tech.goticket.backendapi.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.organizer.Organizer;
import tech.goticket.backendapi.shared.model.status.Status;

import java.time.Instant;
import java.util.Locale;

@Entity
@Table(name = "tb_venues")
@Getter
@Setter
public class Venue {

    public Venue() {}

    public Venue(
            String name,
            String legalName,
            String CNPJ,
            String description,
            String streetAddress,
            String streetAddressNumber,
            String neighborhood,
            String city,
            String state,
            String country,
            Instant registerDate,
            Instant lastUpdateDate,
            Status status,
            Organizer organizer
    ) {
        this.name = name;
        this.legalName = legalName;
        this.CNPJ = CNPJ;
        this.description = description;
        this.streetAddress = streetAddress;
        this.streetAddressNumber = streetAddressNumber;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.country = country;
        this.registerDate = registerDate;
        this.lastUpdateDate = lastUpdateDate;
        this.status = status;
        this.organizer = organizer;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id", nullable = false)
    private Long venueID;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "cnpj", nullable = false)
    private String CNPJ;

    @Column
    private String description;

    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Column(name = "street_address_number", nullable = false)
    private String streetAddressNumber;

    @Column(nullable = false)
    private String neighborhood;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "approval_date", nullable = true)
    private Instant approvalDate;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;
}
