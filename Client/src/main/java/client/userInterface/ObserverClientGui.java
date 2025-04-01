package client.userInterface;

import client.dtos.ClientEvent;

public interface ObserverClientGui {
    public void update(ClientEvent guiEvent, String[] msg);
}
