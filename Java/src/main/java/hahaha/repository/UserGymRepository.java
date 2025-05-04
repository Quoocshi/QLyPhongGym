package hahaha.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hahaha.model.UserGym;

public interface UserGymRepository extends JpaRepository<UserGym, Long> {
    boolean existsByEmail(String email);
    @Query("SELECT u.userId FROM UserGym u WHERE u.email = ?1")
    Long findUserIdByEmail(String email);

}
