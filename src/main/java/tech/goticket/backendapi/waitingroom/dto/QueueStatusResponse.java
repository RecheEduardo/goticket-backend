package tech.goticket.backendapi.waitingroom.dto;

public record QueueStatusResponse(
        String state,
        Long eventId,
        Long position,
        Long totalInQueue,
        Long estimatedWaitSeconds,
        String admissionToken
) {
    public static QueueStatusResponse waiting(Long eventId, long position, long total, long waitSeconds) {
        return new QueueStatusResponse("WAITING", eventId, position, total, waitSeconds, null);
    }

    public static QueueStatusResponse admitted(Long eventId, String token) {
        return new QueueStatusResponse("ADMITTED", eventId, null, null, null, token);
    }
}
