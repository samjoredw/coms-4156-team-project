package dev.coms4156.project.druginteraction;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Unit tests for the DrugInteraction class.
 */
class DrugInteractionUnitTests {

  @Test
  void testMainMethod() {
    // Mock the ConfigurableApplicationContext and FirebaseService
    ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
    FirebaseService mockFirebaseService = mock(FirebaseService.class);

    // Simulate behavior when getBean is called
    when(mockContext.getBean(FirebaseService.class)).thenReturn(mockFirebaseService);

    // Mock the SpringApplication.run method to return the mock context
    try (MockedStatic<SpringApplication> mockStaticSpringApplication = mockStatic(SpringApplication.class)) {
      mockStaticSpringApplication
          .when(() -> SpringApplication.run(DrugInteraction.class, new String[] {}))
          .thenReturn(mockContext);

      // Call the main method
      DrugInteraction.main(new String[] {});

      // Verify that SpringApplication.run was called with the correct parameters
      mockStaticSpringApplication.verify(() -> SpringApplication.run(DrugInteraction.class, new String[] {}));

      // Verify that getBean was called to retrieve the FirebaseService
      verify(mockContext).getBean(FirebaseService.class);
    }
  }
}
