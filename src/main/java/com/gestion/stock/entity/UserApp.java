package com.gestion.stock.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserApp {
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

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleApp role;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserPermission> userPermissions = new HashSet<>();
}