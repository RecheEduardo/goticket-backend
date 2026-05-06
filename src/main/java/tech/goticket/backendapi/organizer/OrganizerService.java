package tech.goticket.backendapi.organizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.organizer.dto.OrganizerListDTO;
import tech.goticket.backendapi.organizer.dto.OrganizerMinDTO;
import tech.goticket.backendapi.user.UserService;
import tech.goticket.backendapi.shared.exception.PatchProgressingException;
import tech.goticket.backendapi.shared.exception.user.EmailAlreadyExistsException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final OrganizerRepository organizerRepository;

    private final ObjectMapper objectMapper;

    private final UserService userService;

    @Transactional
    public void saveOrganizer(Organizer organizer) { organizerRepository.save(organizer); }

    public Optional<Organizer> findByCNPJ(String CNPJ) {
        return this.organizerRepository.findByCNPJ(CNPJ);
    }

    public Optional<Organizer> findById(UUID organizerId) { return this.organizerRepository.findByUserId(organizerId); }

    @Transactional
    public OrganizerListDTO findAll(PageRequest pageRequest) {
        var organizers = organizerRepository.findAll(pageRequest)
                .map(OrganizerMinDTO::new);

        return new OrganizerListDTO(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                organizers.getTotalPages(),
                organizers.getTotalElements(),
                organizers.toList()
        );
    }

    @Transactional
    public Organizer updateOrganizer(UUID organizerId, JsonNode patchNode) {
        Organizer existingOrganizer = organizerRepository.findByUserId(organizerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organizador não encontrado"));

        try {
            JsonNode existingOrganizerNode = objectMapper.valueToTree(existingOrganizer);

            JsonNode patchedNode = objectMapper.readerForUpdating(existingOrganizerNode)
                    .readValue(patchNode);

            Organizer updatedOrganizer = objectMapper.treeToValue(patchedNode, Organizer.class);
            updatedOrganizer.setPassword(existingOrganizer.getPassword());

            if(!updatedOrganizer.getUserId().equals(existingOrganizer.getUserId())) {
                updatedOrganizer.setUserId(existingOrganizer.getUserId());
            }

            if(!updatedOrganizer.getEmail().equals(existingOrganizer.getEmail())) {
                userService.findByEmail(updatedOrganizer.getEmail())
                        .ifPresent(user -> { throw new EmailAlreadyExistsException("E-mail já cadastrado na plataforma."); });
            }

            return organizerRepository.save(updatedOrganizer);

        } catch (Exception e) {
            throw new PatchProgressingException("Erro ao atualizar organizador.");
        }
    }
}
