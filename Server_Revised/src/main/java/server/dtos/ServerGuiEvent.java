package server.dtos;

public enum ServerGuiEvent {
    ADD_TO_QUEUE,
    REMOVE_FROM_QUEUE,
    EXECUTE,
    FINISHED_EXECUTING,
    EXECUTE_SEND, // Send to server that a query has started being executed
}
