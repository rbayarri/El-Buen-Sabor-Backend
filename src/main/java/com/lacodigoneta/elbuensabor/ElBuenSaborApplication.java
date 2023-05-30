package com.lacodigoneta.elbuensabor;

import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.enums.Role;
import com.lacodigoneta.elbuensabor.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ElBuenSaborApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElBuenSaborApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository repository, PasswordEncoder passwordEncoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) {

                repository.save(
                        User.builder()
                                .username("renzobayarri@gmail.com")
                                .name("Renzo")
                                .lastName("Bayarri")
                                .role(Role.ADMIN)
                                .emailConfirmed(true)
                                .active(true)
                                .password(passwordEncoder.encode("Password1!"))
                                .build()
                );
                repository.save(
                        User.builder()
                                .username("cashier@gmail.com")
                                .name("Renzo")
                                .lastName("Bayarri")
                                .role(Role.CASHIER)
                                .emailConfirmed(true)
                                .active(true)
                                .password(passwordEncoder.encode("Password!1"))
                                .build()
                );
                repository.save(
                        User.builder()
                                .username("chef@gmail.com")
                                .name("Renzo")
                                .lastName("Bayarri")
                                .role(Role.CHEF)
                                .emailConfirmed(true)
                                .active(true)
                                .password(passwordEncoder.encode("Password1!"))
                                .build()
                );
                repository.save(
                        User.builder()
                                .username("delivery@gmail.com")
                                .name("Renzo")
                                .lastName("Bayarri")
                                .role(Role.DELIVERY)
                                .emailConfirmed(true)
                                .active(true)
                                .password(passwordEncoder.encode("Password1!"))
                                .build()
                );
                repository.save(
                        User.builder()
                                .username("user@gmail.com")
                                .name("Renzo")
                                .lastName("Bayarri")
                                .role(Role.USER)
                                .emailConfirmed(true)
                                .active(true)
                                .password(passwordEncoder.encode("Password1!"))
                                .build()
                );
            }
        };
    }
}
