package client;

import client.connection.ClientManager;
import client.userInterface.ClientGui;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@AllArgsConstructor
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        Scanner idInput = new Scanner(System.in);
        System.out.println("user id: ");
        String id = idInput.nextLine();
        System.out.println("password: ");
        String password = idInput.nextLine();
        idInput.close();

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ClientApplication.class).headless(false).run(args);
        ClientManager clientManager = context.getBean(ClientManager.class);

        ClientGui gui = new ClientGui(clientManager, id);
        gui.setUp();

        clientManager.initConnection(id, password);

    }

}
