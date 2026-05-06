package tech.goticket.backendapi.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.shared.exception.ForbiddenActionException;
import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.user.User;
import tech.goticket.backendapi.user.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventAuthorizationService {

    private final UserService userService;

    public void requireOwnerOrAdmin(Event event, UUID userId, String exceptionMessage) {
        if (userId == null) {
            throw new ForbiddenActionException(exceptionMessage);
        }

        User requestUser = userService.findById(userId)
                .orElseThrow(() -> new ForbiddenActionException(
                        "Um erro ocorreu na sessão atual, faça login novamente."
                ));

        boolean isAdmin = requestUser.getRole().getName().equals(Role.Values.ADMIN.name());
        boolean isEventOwner = requestUser.getUserId().equals(event.getOrganizer().getUserId());

        if (!isAdmin && !isEventOwner) {
            throw new ForbiddenActionException(exceptionMessage);
        }
    }
}