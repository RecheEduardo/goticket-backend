package tech.goticket.backendapi.fee.enums;

public enum FeeScope {
    PLATFORM,    // taxa aplicada a TODAS as orders
    ORGANIZER,   // taxa de um organizador específico (scope_ref_id = organizer_id)
    EVENT        // taxa de um evento específico (scope_ref_id = event_id)
}
