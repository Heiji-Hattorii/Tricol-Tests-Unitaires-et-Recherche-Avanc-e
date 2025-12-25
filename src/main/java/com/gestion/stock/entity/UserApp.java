package com.gestion.stock.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name="users")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserApp implements UserDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Column(nullable=false,unique = true)
    @EqualsAndHashCode.Include
    private String email;
    
    @Column(nullable=false)
    private String password;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "is_enabled")
    private boolean enabled = true;
    
    @Column(name = "is_account_non_expired")
    private boolean accountNonExpired = true;
    
    @Column(name = "is_account_non_locked")
    private boolean accountNonLocked = true;
    
    @Column(name = "is_credentials_non_expired")
    private boolean credentialsNonExpired = true;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleApp role;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserPermission> userPermissions = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Ajouter le rôle
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            
            // Ajouter les permissions du rôle
            role.getPermissions().forEach(permission -> 
                authorities.add(new SimpleGrantedAuthority(permission.getName()))
            );
        }
        
        // Ajouter les permissions personnalisées actives
        userPermissions.stream()
            .filter(UserPermission::isActive)
            .forEach(userPermission -> 
                authorities.add(new SimpleGrantedAuthority(userPermission.getPermission().getName()))
            );
            
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
