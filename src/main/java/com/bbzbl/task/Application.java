package com.bbzbl.task;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Task Application.
 * This class serves as the entry point for the Spring Boot application.
 * It also configures the theme for the Vaadin application.
 */
@SpringBootApplication
@Theme(value = "426-taskapp")
public class Application implements AppShellConfigurator {

    /**
     * Main method to run the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
