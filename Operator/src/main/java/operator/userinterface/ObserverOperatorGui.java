package operator.userinterface;

import operator.dtos.OperatorEvent;

public interface ObserverOperatorGui {
    public void update(OperatorEvent guiEvent, Object msg);
}
