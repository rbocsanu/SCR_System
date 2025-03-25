package server.unit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.dtos.ServerGuiEvent;
import server.entities.Query;
import server.entities.TaskQuery;

import java.util.HashMap;
import java.util.PriorityQueue;

@Component
@RequiredArgsConstructor
public class RobotUnit implements Runnable {

    private final ObservableUnit guiObservable;

    private PriorityQueue<Query> taskQueue = new PriorityQueue<>();

    private State currentState;
    private Processor currentProcess;
    private Thread currentProcessThread;

    @Override
    public void run() {
        int sleepTime = 5000;

        while ( true ) {
            try {
                Thread.sleep(sleepTime);
            } catch ( InterruptedException e ) {
                System.out.println(e);
            }

            //System.out.println("WAITING");

            while (!taskQueue.isEmpty() && currentState == State.IDLE) {
                currentState = State.EXECUTING;
                Query nextQuery = taskQueue.poll();
                currentProcess = new Processor(nextQuery);
                currentProcessThread = new Thread(currentProcess);
                currentProcessThread.start();

                guiObservable.notifyAll(ServerGuiEvent.REMOVE_FROM_QUEUE, nextQuery.getName());
                guiObservable.notifyAll(ServerGuiEvent.EXECUTE, nextQuery.getName());
                guiObservable.notifyAll(ServerGuiEvent.EXECUTE_SEND, nextQuery);

                try {
                    currentProcessThread.join();

                    guiObservable.notifyAll(ServerGuiEvent.FINISHED_EXECUTING, "");

                    if (currentState != State.EMERGENCY) {
                        currentState = State.IDLE;
                    }
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }

            if (currentProcessThread == null && currentState == State.EXECUTING) {
                currentState = State.IDLE;
            }
        }
    }

    public void addActivityToQueue(Query query) {
        guiObservable.notifyAll(ServerGuiEvent.ADD_TO_QUEUE, query.getName());

        taskQueue.add(query);
    }
    
    public enum State {
        IDLE,
        EXECUTING,
        EMERGENCY
    }

    @RequiredArgsConstructor
    private class Processor implements Runnable {

        @Getter
        private final Query processQuery;
        private boolean terminated = false;

        // Thread
        @Override
        public void run() {

            try {
                System.out.println("Currently executing: " + processQuery.getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            if (!terminated) System.out.println("Finished executing: " + processQuery.getName());
        }

    }
}
