package server.dtos;

public enum ServerGuiEvent {
    ADD_TO_QUEUE,
    REMOVE_FROM_QUEUE,
    EXECUTE,
    EXECUTE_COMPLETE,
    EXECUTE_START, // Send to server that a query has started being executed
}
