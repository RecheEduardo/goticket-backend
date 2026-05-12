package tech.goticket.backendapi.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.Ticket;
import tech.goticket.backendapi.ticket.enums.EligibilityType;
import tech.goticket.backendapi.ticket.enums.TicketType;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_order_items", indexes = {
        @Index(name = "ix_order_items_order", columnList = "order_id"),
        @Index(name = "ix_order_items_allotment", columnList = "batch_allotment_id")
})
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "holder_name", nullable = false, length = 200)
    private String holderName;

    @Column(name = "holder_document", nullable = false, length = 20)
    private String holderDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_allotment_id", nullable = false)
    private BatchAllotment batchAllotment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_type_id")
    private EligibilityType eligibilityType;

    @Column(name = "eligibility_document_number", length = 50)
    private String eligibilityDocumentNumber;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "fee_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal feeAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;
}
