package operator.dtos;

public record OperatorNotification(
        OperatorEvent clientEvent,
        String[] message
) {
}

