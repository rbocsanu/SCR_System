package client.dtos;

import java.io.Serializable;

public record ClientNotification (
        ClientEvent clientEvent,
        String[] message
) {
}

