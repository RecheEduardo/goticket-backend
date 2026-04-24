package tech.goticket.backendapi.venue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.shared.exception.ForbiddenActionException;
import tech.goticket.backendapi.shared.exception.PatchProgressingException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.user.User;
import tech.goticket.backendapi.user.UserService;
import tech.goticket.backendapi.venue.dto.VenueListDTO;
import tech.goticket.backendapi.venue.dto.VenueMinDTO;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class VenueService {
    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    public Optional<Venue> findByCNPJ(String cnpj) { return venueRepository.findByCNPJ(cnpj); }

    public Optional<Venue> findById(Long venueId) { return venueRepository.findById(venueId); }

    @Transactional
    public void saveVenue(Venue newVenue) { venueRepository.save(newVenue);}

    @Transactional
    public VenueListDTO findAll(PageRequest pageRequest) {
        var venues = venueRepository.findAll(pageRequest)
                .map(VenueMinDTO::new);

        return new VenueListDTO(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                venues.getTotalPages(),
                venues.getTotalElements(),
                venues.toList()
        );
    }

    @Transactional
    public Venue updateVenue(Long venueId, JsonNode patchNode, UUID userId) {
        Venue existingVenue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado."));

        validateUserPermission(existingVenue, userId);

        try {
            objectMapper.readerForUpdating(existingVenue).readValue(patchNode);
            existingVenue.setLastUpdateDate(Instant.now());

            return venueRepository.save(existingVenue);
        } catch (Exception e) {
            throw new PatchProgressingException("Erro ao atualizar espaço.");
        }
    }

    @Transactional
    public void deleteVenue(Long venueId, UUID userId) {
        Venue existingVenue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado."));

        validateUserPermission(existingVenue, userId);

        venueRepository.delete(existingVenue);
    }

    // Auxiliar para lógica de permissão
    private void validateUserPermission(Venue venue, UUID userId) {
        User requestUser = userService.findById(userId)
                .orElseThrow(() -> new ForbiddenActionException("Um erro ocorreu na sessão atual, faça login novamente."));

        boolean isAdmin = requestUser.getRole().getName().equals(Role.Values.ADMIN.name());
        boolean isVenueOwner = venue.getOrganizer() != null
                && requestUser.getUserID().equals(venue.getOrganizer().getUserID());

        if (!isAdmin && !isVenueOwner) {
            throw new ForbiddenActionException("Usuário não tem permissão para executar esta ação.");
        }
    }
}
