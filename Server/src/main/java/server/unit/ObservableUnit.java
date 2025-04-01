package server.unit;

import server.dtos.ServerGuiEvent;
import server.userinterface.ServerObserver;

import java.util.ArrayList;

public abstract class ObservableUnit {

    ArrayList<ServerObserver> listeningGuis;

    public ObservableUnit() {
        listeningGuis = new ArrayList<>();
    }

    public void register(ServerObserver listeningComponent) {
        listeningGuis.add(listeningComponent);
    }

    public void unregister(ServerObserver listeningComponent) {
        listeningGuis.remove(listeningComponent);
    }

    public void notifyAll(ServerGuiEvent guiEvent, Object msg) {
        for (ServerObserver listeningComponent : listeningGuis) {
            listeningComponent.update(guiEvent, msg);
        }
    }

}

