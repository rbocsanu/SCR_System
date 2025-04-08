package operator;

import lombok.AllArgsConstructor;
import operator.connection.OperatorManager;
import operator.userinterface.OperatorGui;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@AllArgsConstructor
@SpringBootApplication
public class OperatorApplication {

    public static void main(String[] args) {

        /*
        Scanner idInput = new Scanner(System.in);
        System.out.println("user id: ");
        String id = idInput.nextLine();
        System.out.println("password: ");
        String password = idInput.nextLine();
        idInput.close();

         */

        ConfigurableApplicationContext context = new SpringApplicationBuilder(OperatorApplication.class).headless(false).run(args);
        OperatorManager operatorManager = context.getBean(OperatorManager.class);

        String id = context.getEnvironment().getProperty("id");
        String password = context.getEnvironment().getProperty("password");

        OperatorGui operatorGui = new OperatorGui(operatorManager);
        operatorGui.setUp();

        operatorManager.initConnection(id, password);

    }
}
