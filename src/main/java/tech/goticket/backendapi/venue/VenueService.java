package tech.goticket.backendapi.venue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.event.repository.EventSectorRepository;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ForbiddenActionException;
import tech.goticket.backendapi.shared.model.status.Status;
import tech.goticket.backendapi.shared.exception.PatchProgressingException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.user.User;
import tech.goticket.backendapi.user.UserService;
import tech.goticket.backendapi.venue.dto.UpsertVenueSectorDTO;
import tech.goticket.backendapi.venue.dto.VenueListDTO;
import tech.goticket.backendapi.venue.dto.VenueMinDTO;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    private final UserService userService;

    private final ObjectMapper objectMapper;

    private final VenueSectorRepository venueSectorRepository;

    private final EventSectorRepository eventSectorRepository;

    @Transactional
    public Optional<Venue> findByCNPJ(String cnpj) { return venueRepository.findByCNPJ(cnpj); }

    @Transactional
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
            String previousStatusName = existingVenue.getStatus() != null
                    ? existingVenue.getStatus().getName()
                    : null;

            objectMapper.readerForUpdating(existingVenue).readValue(patchNode);
            existingVenue.setLastUpdateDate(Instant.now());

            String newStatusName = existingVenue.getStatus() != null
                    ? existingVenue.getStatus().getName()
                    : null;

            boolean transitionedToActive = Status.Values.ACTIVE.name().equals(newStatusName)
                    && !Status.Values.ACTIVE.name().equals(previousStatusName);

            if (transitionedToActive) {
                existingVenue.setApprovalDate(Instant.now());
            }

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

    @Transactional
    public List<VenueSector> listVenueSectors(Long venueId) {
        return venueSectorRepository.findAllByVenue_VenueIdOrderBySectorIdAsc(venueId);
    }

    @Transactional
    public List<VenueSector> replaceVenueSectors(Long venueId, List<UpsertVenueSectorDTO> sectors, UUID userId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado."));

        validateUserPermission(venue, userId);

        if (sectors == null) {
            throw new InvalidArgumentException("A lista de setores é obrigatória.");
        }

        List<VenueSector> existingSectors = venueSectorRepository.findAllByVenue_VenueIdOrderBySectorIdAsc(venueId);
        Set<Long> payloadSectorIds = new HashSet<>();

        List<VenueSector> savedSectors = sectors.stream().map(input -> {
            VenueSector venueSector;
            if (input.sectorId() != null) {
                venueSector = venueSectorRepository.findBySectorIdAndVenue_VenueId(input.sectorId(), venueId)
                        .orElseThrow(() -> new InvalidArgumentException("Setor informado não pertence a este espaço."));
                payloadSectorIds.add(input.sectorId());
            } else {
                venueSector = new VenueSector();
                venueSector.setRegisterDate(Instant.now());
                venueSector.setVenue(venue);
            }

            venueSector.setName(input.name().trim());
            venueSector.setDescription(input.description().trim());
            venueSector.setMaxCapacity(input.maxCapacity());
            venueSector.setMapElementId(input.mapElementId());
            venueSector.setLastUpdateDate(Instant.now());
            return venueSector;
        }).toList();

        existingSectors.stream()
                .filter(existing -> !payloadSectorIds.contains(existing.getSectorId()))
                .forEach(existing -> {
                    long references = eventSectorRepository.countByVenueSector_SectorId(existing.getSectorId());
                    if (references > 0) {
                        throw new InvalidArgumentException(
                                "Não é possível remover o setor '" + existing.getName() + "' porque ele está vinculado a evento(s)."
                        );
                    }
                    venueSectorRepository.delete(existing);
                });

        return venueSectorRepository.saveAll(savedSectors);
    }

    @Transactional
    public Venue updateVenueMapKey(Long venueId, String sectorMapS3Key, UUID userId) {
        Venue existingVenue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado."));

        validateUserPermission(existingVenue, userId);

        existingVenue.setSectorMapS3Key(sectorMapS3Key);
        existingVenue.setLastUpdateDate(Instant.now());
        return venueRepository.save(existingVenue);
    }

    private void validateUserPermission(Venue venue, UUID userId) {
        User requestUser = userService.findById(userId)
                .orElseThrow(() -> new ForbiddenActionException("Um erro ocorreu na sessão atual, faça login novamente."));

        boolean isAdmin = requestUser.getRole().getName().equals(Role.Values.ADMIN.name());
        boolean isVenueOwner = venue.getOrganizer() != null
                && requestUser.getUserId().equals(venue.getOrganizer().getUserId());

        if (!isAdmin && !isVenueOwner) {
            throw new ForbiddenActionException("Usuário não tem permissão para executar esta ação.");
        }
    }
}
