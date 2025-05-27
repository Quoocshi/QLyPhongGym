// package hahaha.service;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import hahaha.repository.NhanVienRepository;

// @Service
// public class NhanVienServiceImpl implements NhanVienService{
//     @Autowired
//     NhanVienRepository nhanVienRepository; 
//     @Override
//     public String generateNextMaNV() {
//         Integer max = nhanVienRepository.findMaxMaNVNumber();
//         int next = (max != null) ? max + 1 : 1;
//         return String.format("NV%03d", next);
//     }
//     @Override
//     public String generateNextMaQL(){
//         Integer max = nhanVienRepository.findMaxMaQLNumber();
//         int next = (max != null) ? max + 1 : 1;
//         return String.format("QL%03d", next);
//     }
//     @Override
//     public String generateNextMaPT(){
//         Integer max = nhanVienRepository.findMaxMaPTNumber();
//         int next = (max != null) ? max + 1 : 1;
//         return String.format("PT%03d", next);
//     }

// }