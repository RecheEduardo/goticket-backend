package tech.goticket.backendapi.organizer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
    Optional<Organizer> findByCNPJ(String CNPJ);

    Optional<Organizer> findByUserID(UUID userID);
}

