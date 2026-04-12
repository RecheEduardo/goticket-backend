package tech.goticket.backendapi.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.organizer.Organizer;

import java.time.Instant;

@Entity
@Table(name = "tb_venues")
@Getter
@Setter
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id", nullable = false)
    private Long venueID;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "identity_document", nullable = false)
    private String identityDocument;

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
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;
}
