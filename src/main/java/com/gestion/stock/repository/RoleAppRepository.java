package com.gestion.stock.repository;

import com.gestion.stock.entity.RoleApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleAppRepository extends JpaRepository<RoleApp,Long> {
    Optional<RoleApp> findByName(String name);
    boolean existsByName(String name);
}
