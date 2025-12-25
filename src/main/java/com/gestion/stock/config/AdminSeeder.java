package com.gestion.stock.config;

import com.gestion.stock.entity.Permission;
import com.gestion.stock.entity.RoleApp;
import com.gestion.stock.entity.UserApp;
import com.gestion.stock.entity.UserPermission;
import com.gestion.stock.repository.PermissionRepository;
import com.gestion.stock.repository.RoleAppRepository;
import com.gestion.stock.repository.UserAppRepository;
import com.gestion.stock.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserAppRepository userRepository;
    private final RoleAppRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminRoleIfNotExists();
        createAdminUser();
    }

    private void createAdminRoleIfNotExists() {
        if (!roleRepository.existsByName("ADMIN")) {
            RoleApp adminRole = new RoleApp();
            adminRole.setName("ADMIN");
            adminRole.setPermissions(new HashSet<>(permissionRepository.findAll()));
            roleRepository.save(adminRole);
            System.out.println("Admin role created with " + adminRole.getPermissions().size() + " permissions");
        }
    }

    private void createAdminUser() {
        if (!userRepository.existsByEmail("admin@tricol.com")) {
            RoleApp adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));

            UserApp admin = new UserApp();
            admin.setEmail("admin@tricol.com");
            admin.setPassword(passwordEncoder.encode("secret"));
            admin.setCreatedAt(LocalDateTime.now());
            admin.setEnabled(true);
            admin.setAccountNonExpired(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);
            admin.setRole(adminRole);

            admin = userRepository.save(admin);
            syncAdminPermissions(admin);
            
            System.out.println("Admin user created: admin@tricol.com");
        }
    }

    private void syncAdminPermissions(UserApp admin) {
        try {
            System.out.println("Syncing admin permissions...");
            
            // Charger le rôle avec ses permissions
            RoleApp adminRoleWithPermissions = roleRepository.findByNameWithPermissions("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));
            
            // Supprimer les anciennes permissions si elles existent
            userPermissionRepository.deleteByUser(admin);
            
            // Ajouter toutes les permissions du rôle ADMIN
            int savedCount = 0;
            for (Permission permission : adminRoleWithPermissions.getPermissions()) {
                UserPermission userPermission = new UserPermission();
                userPermission.setUser(admin);
                userPermission.setPermission(permission);
                userPermission.setActive(true);
                
                userPermissionRepository.save(userPermission);
                savedCount++;
            }
            
            System.out.println("Successfully synced " + savedCount + " permissions for admin user");
            
        } catch (Exception e) {
            System.err.println("Error syncing admin permissions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}