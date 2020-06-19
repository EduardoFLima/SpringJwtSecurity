package edu.learn.jwt;

import edu.learn.jwt.auth.entities.User;
import edu.learn.jwt.auth.repositories.UserRepository;
import edu.learn.jwt.auth.utils.PasswordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static edu.learn.jwt.auth.utils.Role.NORMAL;


@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LogManager.getLogger();

    private UserRepository userRepository;

    @Autowired
    public Application(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args-> {
            LOGGER.info("Inserting initial data - START");

            User user = new User("TestingUser", PasswordUtils.encrypt("1234"), NORMAL);

            userRepository.save(user);

            LOGGER.info("Inserting initial data - END");
        };
    }
}
