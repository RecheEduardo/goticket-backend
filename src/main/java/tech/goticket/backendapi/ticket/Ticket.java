package tech.goticket.backendapi.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.client.Client;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_tickets")
@Getter
@Setter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ticket_id", columnDefinition = "uuid")
    private UUID ticketID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_allotment_id", nullable = false)
    private BatchAllotment allotment;

    @Column(name = "paid_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal paidPrice;

    @Column(name = "fees_paid", precision = 10, scale = 2)
    private BigDecimal feesPaid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Client buyer;

    @Column(name = "holder_name", nullable = false)
    private String holderName;

    @Column(name = "holder_document", nullable = false)
    private String holderDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_type_id")
    private EligibilityType eligibilityType;

    @Column(name = "eligibility_document_number", length = 50)
    private String eligibilityDocumentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private TicketStatus status;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "used_date")
    private Instant usedDate;

    @Column(name = "qr_token", unique = true, nullable = false, length = 64)
    private String qrToken;
}