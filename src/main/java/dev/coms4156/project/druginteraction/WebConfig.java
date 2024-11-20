package dev.coms4156.project.druginteraction;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for customizing Spring MVC settings.
 * This class implements the {@link WebMvcConfigurer} interface to define custom CORS (Cross-Origin
 * Resource Sharing) settings
 * for the application, allowing specific frontend URLs to access backend APIs.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
  /**
   * Configures CORS (Cross-Origin Resource Sharing) mappings for the application.
   * <p>
   * This method defines the CORS rules for APIs under the "/api/**" path, specifying allowed
   * origins, methods, headers, and
   * other CORS-related settings. By configuring CORS, the backend can interact with allowed
   * frontend applications hosted on
   * different origins.
   * </p>
   *
   * @param registry the {@link CorsRegistry} used to add CORS mappings.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins("null", "http://127.0.0.1:5500") // Add your frontend URLs here
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(false); // Changed to false since we don't need credentials
  }
}