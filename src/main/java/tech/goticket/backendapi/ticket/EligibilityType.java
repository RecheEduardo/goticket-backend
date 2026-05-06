package tech.goticket.backendapi.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_eligibility_types")
@Getter
@Setter
public class EligibilityType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eligibility_type_id")
    private Long eligibilityTypeId;

    private String name;

    public enum Values {
        STUDENT(1L),
        ELDERLY(2L),
        DISABILITY(3L),
        LOW_INCOME_YOUTH(4L),
        TEACHER(5L),;

        long eligibilityTypeId;

        Values(long eligibilityTypeId){ this.eligibilityTypeId = eligibilityTypeId; }

        public long getEligibilityTypeId() { return eligibilityTypeId; }
    }
}
