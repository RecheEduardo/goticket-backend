package tech.goticket.backendapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tb_clients")
@Check(constraints = "sex IN (1,2)")
@Getter
@Setter
public class Client extends User {

    public Client() { super(); }

    public Client(
            String email,
            String password,
            Role role,
            UserStatus userStatus,
            String fullName,
            Integer sex,
            String identityDocument,
            LocalDate birthDate,
            Instant registerDate,
            Instant lastUpdateDate
    ) {
        super(email, password, role, userStatus);
        this.fullName = fullName;
        this.sex = sex;
        this.identityDocument = identityDocument;
        this.birthDate = birthDate;
        this.registerDate = registerDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "sex", nullable = false)
    private Integer sex;

    @Column(name = "identity_document", unique = true, nullable = false)
    private String identityDocument;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
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
