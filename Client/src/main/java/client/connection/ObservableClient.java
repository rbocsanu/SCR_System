package client.connection;

import client.dtos.ClientEvent;
import client.userInterface.ObserverClientGui;

import java.util.ArrayList;

public abstract class ObservableClient {

    ArrayList<ObserverClientGui> listeningGuis;

    public ObservableClient() {
        listeningGuis = new ArrayList<>();
    }

    public void register(ObserverClientGui listeningComponent) {
        listeningGuis.add(listeningComponent);
    }

    public void unregister(ObserverClientGui listeningComponent) {
        listeningGuis.remove(listeningComponent);
    }

    public void notifyAll(ClientEvent guiEvent, String[] msg) {
        System.out.println(listeningGuis);
        for (ObserverClientGui listeningComponent : listeningGuis) {
            listeningComponent.update(guiEvent, msg);
        }
    }
    
}
