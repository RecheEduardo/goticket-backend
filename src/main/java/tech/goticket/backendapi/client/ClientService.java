package tech.goticket.backendapi.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.user.UserService;
import tech.goticket.backendapi.shared.exception.PatchProgressingException;
import tech.goticket.backendapi.shared.exception.user.EmailAlreadyExistsException;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    public Optional<Client> findByIdentityDocument(String identityDocument) {
        return this.clientRepository.findByIdentityDocument(identityDocument);
    }

    public Optional<Client> findById(UUID clientId) {
        return this.clientRepository.findByUserID(clientId);
    }

    @Transactional
    public void saveClient(Client client) { clientRepository.save(client); }

    @Transactional
    public Client updateClient(UUID uuid, JsonNode patchNode) {
        Client existingClient = clientRepository.findByUserID(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente não encontrado"));

        try {
            JsonNode existingClientNode = objectMapper.valueToTree(existingClient);

            JsonNode patchedNode = objectMapper.readerForUpdating(existingClientNode)
                    .readValue(patchNode);

            Client updatedClient = objectMapper.treeToValue(patchedNode, Client.class);
            updatedClient.setPassword(existingClient.getPassword());

            if(!updatedClient.getUserID().equals(existingClient.getUserID())) {
                updatedClient.setUserID(existingClient.getUserID());
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
