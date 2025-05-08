package hahaha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import hahaha.repository.PackageRepository;

public class PackageServiceImpl implements PackageService {
    @Autowired
    PackageRepository packageRepository;
    @Override
    public List<Package> getAllPackages() {
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
