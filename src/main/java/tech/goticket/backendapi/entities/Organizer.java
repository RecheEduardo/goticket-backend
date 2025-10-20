package tech.goticket.backendapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_organizers")
@Getter
@Setter
public class Organizer extends User{

    @Column(name = "organizer_name", nullable = false)
    private String organizerName;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(nullable = false)
    private String cnpj;

    @Column(name =  "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date")
    private Instant lastUpdateDate;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "street_address_number")
    private String streetAddressNumber;

    private String neighborhood;

    private String city;

    private String state;

    private String country;

    @Column(name = "zip_code")
    private String zipCode;

}