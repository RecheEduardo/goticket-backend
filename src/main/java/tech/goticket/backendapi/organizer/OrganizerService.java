package tech.goticket.backendapi.organizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.organizer.dto.OrganizerListDTO;
import tech.goticket.backendapi.organizer.dto.OrganizerMinDTO;
import tech.goticket.backendapi.user.UserService;
import tech.goticket.backendapi.shared.exception.PatchProgressingException;
import tech.goticket.backendapi.shared.exception.user.EmailAlreadyExistsException;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizerService {

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveOrganizer(Organizer organizer) { organizerRepository.save(organizer); }

    public Optional<Organizer> findByCNPJ(String CNPJ) {
        return this.organizerRepository.findByCNPJ(CNPJ);
    }

    public Optional<Organizer> findById(UUID organizerId) { return this.organizerRepository.findByUserID(organizerId); }

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
        Organizer existingOrganizer = organizerRepository.findByUserID(organizerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organizador não encontrado"));

        try {
            JsonNode existingOrganizerNode = objectMapper.valueToTree(existingOrganizer);

            JsonNode patchedNode = objectMapper.readerForUpdating(existingOrganizerNode)
                    .readValue(patchNode);

            Organizer updatedOrganizer = objectMapper.treeToValue(patchedNode, Organizer.class);
            updatedOrganizer.setPassword(existingOrganizer.getPassword());

            if(!updatedOrganizer.getUserID().equals(existingOrganizer.getUserID())) {
                updatedOrganizer.setUserID(existingOrganizer.getUserID());
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
