package operator.connection;

import operator.entities.OperatorEvent;
import operator.userinterface.ObserverOperatorGui;

import java.util.ArrayList;

public abstract class ObservableOperator {

    ArrayList<ObserverOperatorGui> listeningGuis;

    public ObservableOperator() {
        listeningGuis = new ArrayList<ObserverOperatorGui>();
    }

    public void register(ObserverOperatorGui listeningComponent) {
        listeningGuis.add(listeningComponent);
    }

    public void unregister(ObserverOperatorGui listeningComponent) {
        listeningGuis.remove(listeningComponent);
    }

    public void notifyAll(OperatorEvent guiEvent, Object msg) {
        for (ObserverOperatorGui listeningComponent : listeningGuis) {
            listeningComponent.update(guiEvent, msg);
        }
    }
    
}
