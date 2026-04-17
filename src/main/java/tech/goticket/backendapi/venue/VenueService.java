package tech.goticket.backendapi.venue;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VenueService {
    @Autowired
    private VenueRepository venueRepository;

    public Optional<Venue> findByCNPJ(String cnpj) { return venueRepository.findByCNPJ(cnpj); }

    public Optional<Venue> findById(Long venueId) { return venueRepository.findById(venueId); }

    @Transactional
    public void saveVenue(Venue newVenue) { venueRepository.save(newVenue);}
}
