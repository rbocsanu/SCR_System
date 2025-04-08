package operator.userinterface;

import operator.entities.OperatorEvent;

public interface ObserverOperatorGui {
    public void update(OperatorEvent guiEvent, Object msg);
}
