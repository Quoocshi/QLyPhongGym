package hahaha.service;

import java.util.List;

import hahaha.model.GymPackage;

public interface PackageService{
    List<GymPackage> getAllPackages();
    void createPackage();
    void updatePackage();
    void deletePackage();
}
