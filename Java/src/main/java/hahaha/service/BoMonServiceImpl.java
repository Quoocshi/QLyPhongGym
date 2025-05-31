package hahaha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.model.BoMon;
import hahaha.repository.BoMonRepository;

@Service
public class BoMonServiceImpl implements BoMonService{

    @Autowired
    BoMonRepository boMonRepository;
    
    @Override
    public List<BoMon> getAllBoMon() {
        return boMonRepository.findAll();
    }

    @Override
    public Boolean createBoMon(BoMon boMon) {
        if(checkNameExist(boMon.getTenBM()) == false){
            String newMaBM = generateNextMaBoMon();
            boMon.setMaBM(newMaBM);
            
            boMonRepository.save(boMon);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteBoMon(String maBM) {
        BoMon boMon = boMonRepository.findByMaBM(maBM);
        try {
            boMonRepository.delete(boMon);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public Boolean updateBoMon(BoMon boMon) {
       try {
           boMonRepository.save(boMon);
           return true;
       } catch (Exception e) {
        e.printStackTrace();
       }
       return false;
    }

    @Override
    public String generateNextMaBoMon() {
        List<BoMon> allBoMon = getAllBoMon();
        int maxNumber = 0;
        
        for (BoMon bm : allBoMon) {
            String maDV = bm.getMaBM();
            if (maDV != null && maDV.startsWith("BM")) {
                try {
                    int number = Integer.parseInt(maDV.substring(2));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {

                }
            }
        }
        
        return String.format("BM%02d", maxNumber + 1);
    }

    @Override
    public BoMon findByid(String maBM) {
        return boMonRepository.findByMaBM(maBM);
    }

    @Override
    public Boolean checkNameExist(String tenBM) {
        List<BoMon> listBM = getAllBoMon();
        for(BoMon bm : listBM){
            String ten = bm.getTenBM();
            if(ten.equals(tenBM)) return true;
        }
        return false;
    }
    
}
