package dev.coms4156.project.druginteraction;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;

// import com.google.firebase.internal.FirebaseService;

/**
 * The main class for the DrugInteraction service.
 */
@SpringBootApplication
@RestController
public class DrugInteraction implements CommandLineRunner {
  /**
   * The main launcher for the service all it does is make a call to the
   * overridden run method.
   *
   * @param args A {@code String[]} of any potential runtime arguments
   */
  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(DrugInteraction.class, args);
    // FIREBASE DATABASE
    context.getBean(FirebaseService.class);
  }

  /**
   * This contains all the setup logic, it will mainly be focused on loading up
   * and creating an
   * instance of the database based off a saved file or will create a fresh
   * database if the file is
   * not present.
   *
   * @param args A {@code String[]} of any potential runtime args
   */
  @Override
  public void run(String[] args) {
    System.out.println("Start up");
  }
  // This is a test comment
}
