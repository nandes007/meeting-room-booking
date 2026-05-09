package com.nandestech.meetingroom.seeder;

import com.nandestech.meetingroom.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private com.nandestech.meetingroom.repository.UserRepository userRepository;

    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        seedSuperAdmin();
    }

    private void seedSuperAdmin() {
        try {
            if (userRepository.findByUsername("superadmin").isPresent()) {
                System.out.println("Super Admin already exists, skipping seeding");
                return;
            }

            User superAdmin = new User();
            superAdmin.setName("Super Admin");
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail("admin@example.com");
            superAdmin.setPassword(passwordEncoder.encode("password"));
            superAdmin.setRole("SUPERADMIN");
            superAdmin.setCreatedAt(java.time.LocalDateTime.now());
            superAdmin.setUpdatedAt(java.time.LocalDateTime.now());

            userRepository.save(superAdmin);
            System.out.println("Super Admin seeded successfully");
        } catch (RuntimeException e) {
            System.out.println("Seeding skipped: " + e.getMessage());
        } finally {
            System.exit(0);
        }
    }
}
