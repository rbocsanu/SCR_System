package operator.entities;

public record OperatorNotification(
        OperatorEvent operatorEvent,
        Object message
) {}
