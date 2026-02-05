package project.iw3.iw3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Iw3Application extends SpringBootServletInitializer { // <--- 1. Extender esta clase

    // 2. Sobrescribir este método para configurar la aplicación en el servidor
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Iw3Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Iw3Application.class, args);
    }
}