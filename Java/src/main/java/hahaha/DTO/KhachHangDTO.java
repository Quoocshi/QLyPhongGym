package hahaha.DTO;

import lombok.Data;

@Data
public class KhachHangDTO {
    private String maKH;
    private String hoTen;

    public KhachHangDTO(){}

    public KhachHangDTO(String maKH, String hoTen) {
        this.maKH = maKH;
        this.hoTen = hoTen;
    }
}
