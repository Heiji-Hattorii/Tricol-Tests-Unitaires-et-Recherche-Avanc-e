package com.gestion.stock.repository;

import com.gestion.stock.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp,Long> {
    Optional<UserApp> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM UserApp u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.permissions LEFT JOIN FETCH u.userPermissions up LEFT JOIN FETCH up.permission WHERE u.email = :email")
    Optional<UserApp> findByEmailWithRoleAndPermissions(@Param("email") String email);

    @Query("SELECT u FROM UserApp u LEFT JOIN FETCH u.userPermissions up LEFT JOIN FETCH up.permission WHERE u.id = :id")
    Optional<UserApp> findByIdWithPermissions(@Param("id") Long id);
}
