package com.gestion.stock.repository;

import com.gestion.stock.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<UserPermission,Long> {
}
