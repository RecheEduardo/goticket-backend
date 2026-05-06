package tech.goticket.backendapi.ticket.service;

import org.springframework.stereotype.Service;
import tech.goticket.backendapi.event.EventDateSector;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.TicketBatch;
import tech.goticket.backendapi.ticket.TicketType;

import java.math.BigDecimal;

@Service
public class BatchAllotmentValidator {

    public void validateAllotment(BatchAllotment allotment) {
        validatePriceRules(allotment);
        validateQuotaWithinBatch(allotment);
    }

    public void validateBatchQuotasMatchTotal(TicketBatch batch) {
        int sumOfQuotas = batch.getAllotments().stream()
                .mapToInt(BatchAllotment::getQuota)
                .sum();

        if (sumOfQuotas != batch.getTotalTickets()) {
            throw new InvalidArgumentException(
                    "Soma das cotas dos allotments (" + sumOfQuotas +
                            ") deve ser igual ao total do lote (" + batch.getTotalTickets() + ")."
            );
        }
    }

    public void validateSectorHalfQuotaCompliance(EventDateSector sector) {
        boolean hasPaidBatches = sector.getBatches().stream()
                .anyMatch(b -> b.getPrice().compareTo(BigDecimal.ZERO) > 0);
        if (!hasPaidBatches) return;

        int totalSectorTickets = sector.getTotalTickets();
        int totalHalfQuota = sector.getBatches().stream()
                .flatMap(b -> b.getAllotments().stream())
                .filter(a -> a.getTicketType().isHalf())
                .mapToInt(BatchAllotment::getQuota)
                .sum();

        int legalMinimum = (int) Math.floor(totalSectorTickets * 0.4);

        if (totalHalfQuota < legalMinimum) {
            throw new InvalidArgumentException(
                    "Cota de meia-entrada deve ser de no mínimo 40% do total deste setor " +
                            "no dia (Lei 12.933/2013). Mínimo: " + legalMinimum +
                            " ingressos. Total atual: " + totalHalfQuota + "."
            );
        }
    }

    private void validatePriceRules(BatchAllotment allotment) {
        TicketType type = allotment.getTicketType();

        if (type.isSolidary()) {
            if (allotment.getPrice() == null) {
                throw new InvalidArgumentException(
                        "Preço do ingresso solidário deve ser informado pelo organizador."
                );
            }
            if (allotment.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidArgumentException(
                        "Preço do ingresso solidário não pode ser negativo."
                );
            }
        } else if (allotment.getPrice() != null) {
            throw new InvalidArgumentException(
                    "Preço de inteira/meia é derivado do preço do lote — não deve ser informado."
            );
        }
    }

    private void validateQuotaWithinBatch(BatchAllotment allotment) {
        if (allotment.getQuota() == null || allotment.getQuota() < 0) {
            throw new InvalidArgumentException("Cota deve ser zero ou positiva.");
        }
        if (allotment.getQuota() > allotment.getBatch().getTotalTickets()) {
            throw new InvalidArgumentException(
                    "Cota do allotment não pode exceder o total do lote."
            );
        }
    }
}