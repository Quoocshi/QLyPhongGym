package hahaha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  PackageRepository extends JpaRepository<Package, String>{
    List<Package> findAll();
    void createPackage();
    void updatePackage();
    void deletePackage();
}
