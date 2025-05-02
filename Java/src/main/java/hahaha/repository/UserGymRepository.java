package hahaha.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hahaha.model.UserGym;

public interface UserGymRepository extends JpaRepository<UserGym, Long> {
    boolean existsByEmail(String email);
}
