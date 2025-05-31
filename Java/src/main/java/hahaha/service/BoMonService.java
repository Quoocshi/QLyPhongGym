package hahaha.service;

import java.util.List;

import hahaha.model.BoMon;

public interface BoMonService {
    List<BoMon> getAllBoMon();
    Boolean createBoMon(BoMon boMon);
    Boolean deleteBoMon(String maBM);
    Boolean updateBoMon(BoMon boMon);
    String generateNextMaBoMon();
    BoMon findByid(String maBM);
    Boolean checkNameExist(String tenBM);
}
