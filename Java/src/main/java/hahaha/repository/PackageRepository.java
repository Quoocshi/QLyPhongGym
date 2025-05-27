package hahaha.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hahaha.model.GymPackage;

public interface  PackageRepository extends JpaRepository<GymPackage, String>{
}
