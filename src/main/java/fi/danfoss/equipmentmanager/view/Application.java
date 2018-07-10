/*
 * Test http://localhost:8080/test
 */
package fi.danfoss.equipmentmanager.view;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/*
 * Run the application run-run as application by selecting this class
 * Then use browser with url http://localhost:8080/
 * 
 * 
 * taskkill /f /im javaw.exe
 */
//use componentscan if your @RestController locates in another package
@ComponentScan({"fi.danfoss.equipmentmanager.controller"})

@SpringBootApplication
@Import (SwaggerConfig.class) //for Swagger documentation - Swagger home http://localhost:8080/EquipmentManager/swagger-ui.html
public class Application extends SpringBootServletInitializer {
  public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
      return builder.sources(Application.class);
  }
}