package test.backend.roles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import test.backend.roles.config.AppConfig;
import test.backend.roles.config.JPAConfig;

@SpringBootApplication
@ComponentScan(basePackages = {"test.backend.roles", "test.backend.roles.config", "test.backend.roles.RolePackage"})
@Import({AppConfig.class, JPAConfig.class})
public class RolesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RolesApplication.class, args);
    }

}
