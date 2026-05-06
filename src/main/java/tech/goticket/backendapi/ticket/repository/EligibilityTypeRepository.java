package tech.goticket.backendapi.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.ticket.enums.EligibilityType;

@Repository
public interface EligibilityTypeRepository extends JpaRepository<EligibilityType, Long> {
    EligibilityType findByName(String name);
}