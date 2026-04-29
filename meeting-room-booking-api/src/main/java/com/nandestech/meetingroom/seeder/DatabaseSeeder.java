package com.nandestech.meetingroom.seeder;

import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        seedSuperAdmin();
    }

    private void seedSuperAdmin() {
        try {
            User superAdmin = new User();
            superAdmin.setName("Super Admin");
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail("admin@example.com");
            superAdmin.setPassword("password");
            superAdmin.setRole("SUPERADMIN");

            userService.createUser(superAdmin);
            System.out.println("Super Admin seeded successfully");
        } catch (RuntimeException e) {
            System.out.println("Seeding skipped: " + e.getMessage());
        } finally {
            System.exit(0);
        }
    }
}
