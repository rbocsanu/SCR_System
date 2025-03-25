package client.dtos;

import java.io.Serializable;

public enum ClientEvent implements Serializable {
    SET_CURRENT_INPUT,
    ADD_ALL_UNITS,
    ADD_UNIT,
    REMOVE_UNIT,
    ADD_PENDING,
    REMOVE_PENDING,
    ADD_SCHEDULED,
    REMOVE_SCHEDULED,
}
