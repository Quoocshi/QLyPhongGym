package hahaha.service;

import java.util.List;

public interface PackageService{
    List<Package> getAllPackages();
    void createPackage();
    void updatePackage();
    void deletePackage();
}
