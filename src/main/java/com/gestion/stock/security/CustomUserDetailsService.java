package com.gestion.stock.security;

import com.gestion.stock.entity.UserApp;
import com.gestion.stock.entity.UserPermission;
import com.gestion.stock.repository.UserAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAppRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserApp user = userRepository.findByEmailWithRoleAndPermissions(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));

        List<String> permissions = getUserPermissions(user);
        
        return new User(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, permissions.stream().map(SimpleGrantedAuthority::new).toList());
    }

    public UserApp findByEmail(String email) {
        return userRepository.findByEmailWithRoleAndPermissions(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));
    }

    public List<String> getUserPermissions(String email) {
        return getUserPermissions(findByEmail(email));
    }

    private List<String> getUserPermissions(UserApp user) {
        Set<String> permissions = new HashSet<>();

        if (user.getRole() != null && user.getRole().getPermissions() != null) {
            permissions.add("ROLE_" + user.getRole().getName());
            user.getRole().getPermissions().forEach(p -> permissions.add(p.getName()));
        }

        user.getUserPermissions().stream()
            .filter(UserPermission::isActive)
            .forEach(up -> permissions.add(up.getPermission().getName()));

        return permissions.stream().toList();
    }
}