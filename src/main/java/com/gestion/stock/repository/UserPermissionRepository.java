package com.gestion.stock.repository;

import com.gestion.stock.entity.Permission;
import com.gestion.stock.entity.UserApp;
import com.gestion.stock.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission,Long> {
    Optional<UserPermission> findByUserAndPermission(UserApp user, Permission permission);
    void deleteByUser(UserApp user);
}
