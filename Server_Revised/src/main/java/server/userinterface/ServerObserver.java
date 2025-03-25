package server.userinterface;


import server.dtos.ServerGuiEvent;

public interface ServerObserver {
    public void update(ServerGuiEvent guiEvent, Object msg);
}
