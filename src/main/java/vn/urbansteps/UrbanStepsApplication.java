package vn.urbansteps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"vn.urbansteps", "vn.urbansteps.config"})
public class UrbanStepsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrbanStepsApplication.class, args);
    }

}
