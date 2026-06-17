package tech.goticket.backendapi.loadtest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.goticket.backendapi.order.service.OrderPaymentService;

@RestController
@RequestMapping("/loadtest")
@Profile("loadtest")
@RequiredArgsConstructor
public class LoadTestController {
    private final OrderPaymentService orderPaymentService;

    @PostMapping("/confirm/{orderId}")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<Void> confirm(@PathVariable Long orderId) {
        orderPaymentService.markPaidForLoadTest(orderId);
        return ResponseEntity.noContent().build();
    }
}
