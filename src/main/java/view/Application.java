/*
 * Test http://localhost:8080/test
 */
package view;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * Run the application run-run as application by selecting this class
 * Then use browser with url http://localhost:8080/
 * 
 * 
 * taskkill /f /im javaw.exe
 */
//use componentscan if your @RestController locates in another package
@ComponentScan({"controller"})
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}