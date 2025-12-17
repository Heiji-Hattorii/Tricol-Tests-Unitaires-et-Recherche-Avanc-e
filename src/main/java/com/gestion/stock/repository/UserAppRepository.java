package com.gestion.stock.repository;

import com.gestion.stock.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAppRepository extends JpaRepository<UserApp,Long> {
}
