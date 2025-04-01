package server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import server.connection.ServerManager;
import server.unit.ObservableUnit;
import server.unit.RobotUnit;
import server.userinterface.RobotUnitGui;

import java.util.Scanner;

@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        Scanner idInput = new Scanner(System.in);
        System.out.println("robot id: ");
        String id = idInput.nextLine();
        System.out.println("password: ");
        String password = idInput.nextLine();
        idInput.close();

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ServerApplication.class).headless(false).run(args);
        //ObservableUnit guiObservable = context.getBean(ObservableUnit.class)
        ServerManager server = context.getBean(ServerManager.class);
        //RobotUnit robotUnit = context.getBean(RobotUnit.class);

        server.initConnection(id, password);

        RobotUnitGui gui = context.getBean(RobotUnitGui.class);
        gui.setUp(id);
    }
}