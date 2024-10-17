package dev.coms4156.project.individualproject;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;
// import com.google.firebase.internal.FirebaseService;

/**
 * Class contains all the startup logic for the application.
 */
@SpringBootApplication
@RestController
public class MainApplication implements CommandLineRunner {
    /**
     * The main launcher for the service all it does
     * is make a call to the overridden run method.
     *
     * @param args A {@code String[]} of any potential
     *             runtime arguments
     */
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MainApplication.class, args);
        // FIREBASE DATABASE
        context.getBean(FirebaseService.class);
    }

    /**
     * This contains all the setup logic, it will mainly be focused
     * on loading up and creating an instance of the database based
     * off a saved file or will create a fresh database if the file
     * is not present.
     *
     * @param args A {@code String[]} of any potential runtime args
     */
    public void run(String[] args) {
        System.out.println("Start up");
    }
}
