package tech.goticket.backendapi.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.client.dto.ClientListDTO;
import tech.goticket.backendapi.client.dto.ClientMinDTO;
import tech.goticket.backendapi.client.dto.ClientProfileDTO;
import tech.goticket.backendapi.client.dto.UpdatePasswordDTO;
import tech.goticket.backendapi.shared.exception.ForbiddenActionException;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.PatchProgressingException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.shared.exception.user.EmailAlreadyExistsException;
import tech.goticket.backendapi.user.UserService;

import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private static final Set<String> CLIENT_PATCH_WHITELIST = Set.of(
            "fullName", "sex", "streetAddress", "streetAddressNumber",
            "neighborhood", "city", "state", "country", "zipCode"
    );

    private final ClientRepository clientRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<Client> findByIdentityDocument(String identityDocument) {
        return this.clientRepository.findByIdentityDocument(identityDocument);
    }

    public Optional<Client> findById(UUID clientId) {
        return this.clientRepository.findByUserId(clientId);
    }

    @Transactional
    public ClientListDTO findAll(PageRequest pageRequest) {
        var clients = clientRepository.findAll(pageRequest)
                .map(ClientMinDTO::new);

        return new ClientListDTO(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                clients.getTotalPages(),
                clients.getTotalElements(),
                clients.toList()
        );
    }

    @Transactional
    public void saveClient(Client client) { clientRepository.save(client); }

    public ClientProfileDTO getProfile(UUID clientId) {
        Client client = clientRepository.findByUserId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
        return new ClientProfileDTO(client);
    }

    @Transactional
    public void updatePassword(UUID clientId, UpdatePasswordDTO dto) {
        Client client = clientRepository.findByUserId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        if (!passwordEncoder.matches(dto.currentPassword(), client.getPassword())) {
            throw new InvalidArgumentException("Senha atual incorreta.");
        }
        if (passwordEncoder.matches(dto.newPassword(), client.getPassword())) {
            throw new InvalidArgumentException("A nova senha deve ser diferente da atual.");
        }

        client.setPassword(passwordEncoder.encode(dto.newPassword()));
        client.setLastUpdateDate(Instant.now());
        clientRepository.save(client);
    }

    @Transactional
    public Client updateClient(UUID uuid, JsonNode patchNode, boolean isAdmin) {
        Client existingClient = clientRepository.findByUserId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente não encontrado"));

        if (!isAdmin) {
            Iterator<String> fieldNames = patchNode.fieldNames();
            while (fieldNames.hasNext()) {
                String field = fieldNames.next();
                if (!CLIENT_PATCH_WHITELIST.contains(field)) {
                    throw new ForbiddenActionException("Campo '" + field + "' não pode ser alterado pelo cliente.");
                }
            }
        }

        try {
            JsonNode existingClientNode = objectMapper.valueToTree(existingClient);
            JsonNode patchedNode = objectMapper.readerForUpdating(existingClientNode)
                    .readValue(patchNode);

            Client updatedClient = objectMapper.treeToValue(patchedNode, Client.class);
            updatedClient.setPassword(existingClient.getPassword());
            updatedClient.setLastUpdateDate(Instant.now());

            if (!updatedClient.getUserId().equals(existingClient.getUserId())) {
                updatedClient.setUserId(existingClient.getUserId());
            }

            if (!updatedClient.getEmail().equals(existingClient.getEmail())) {
                userService.findByEmail(updatedClient.getEmail())
                        .ifPresent(user -> {
                            throw new EmailAlreadyExistsException("E-mail já cadastrado na plataforma.");
                        });
            }

            return clientRepository.save(updatedClient);

        } catch (ForbiddenActionException | EmailAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new PatchProgressingException("Erro ao atualizar cliente.");
        }
    }
}
