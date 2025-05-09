package hahaha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import hahaha.model.GymPackage;
import hahaha.repository.GymPackageRepository;

public class GymPackageServiceImpl implements PackageService {
    @Autowired
    GymPackageRepository packageRepository;
    @Override
    public List<GymPackage> getAllPackages() {
        return packageRepository.findAll();
    }
    @Override
    public void createPackage() {
    }

    @Override
    public void updatePackage() {
    }

    @Override
    public void deletePackage() {
    }
    
}
