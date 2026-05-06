package tech.goticket.backendapi.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.client.dto.ClientListDTO;
import tech.goticket.backendapi.client.dto.ClientMinDTO;
import tech.goticket.backendapi.user.UserService;
import tech.goticket.backendapi.shared.exception.PatchProgressingException;
import tech.goticket.backendapi.shared.exception.user.EmailAlreadyExistsException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    private final UserService userService;

    private final ObjectMapper objectMapper;

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

    @Transactional
    public Client updateClient(UUID uuid, JsonNode patchNode) {
        Client existingClient = clientRepository.findByUserId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente não encontrado"));

        try {
            JsonNode existingClientNode = objectMapper.valueToTree(existingClient);

            JsonNode patchedNode = objectMapper.readerForUpdating(existingClientNode)
                    .readValue(patchNode);

            Client updatedClient = objectMapper.treeToValue(patchedNode, Client.class);
            updatedClient.setPassword(existingClient.getPassword());

            if(!updatedClient.getUserId().equals(existingClient.getUserId())) {
                updatedClient.setUserId(existingClient.getUserId());
            }

            if(!updatedClient.getEmail().equals(existingClient.getEmail())) {
                userService.findByEmail(updatedClient.getEmail())
                        .ifPresent(
                                user -> { throw new EmailAlreadyExistsException("E-mail já cadastrado na plataforma."); });
            }

            return clientRepository.save(updatedClient);

        } catch (Exception e) {
            throw new PatchProgressingException("Erro ao atualizar cliente.");
        }
    }
}
