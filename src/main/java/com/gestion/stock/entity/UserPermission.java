package com.gestion.stock.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="user_permissions")
@Data
public class UserPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserApp user;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(nullable = false)
    private boolean active = true;
}
