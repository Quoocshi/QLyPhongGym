package hahaha.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hahaha.model.GymPackage;

public interface  GymPackageRepository extends JpaRepository<GymPackage, String>{
    List<GymPackage> findAll();
    // void createPackage();
    // void updatePackage();
    // void deletePackage();
}
