package hahaha.model;

public class TongQuanDoanhThu {
    private Long tongDangKy;
    private Long tongThanhToan;
    private Double doanhThuThang;

    public TongQuanDoanhThu(Long tongDangKy, Long tongThanhToan, Double doanhThuThang) {
        this.tongDangKy = tongDangKy;
        this.tongThanhToan = tongThanhToan;
        this.doanhThuThang = doanhThuThang;
    }

    public Long getTongDangKy() {
        return tongDangKy;
    }

    public void setTongDangKy(Long tongDangKy) {
        this.tongDangKy = tongDangKy;
    }

    public Long getTongThanhToan() {
        return tongThanhToan;
    }

    public void setTongThanhToan(Long tongThanhToan) {
        this.tongThanhToan = tongThanhToan;
    }

    public Double getDoanhThuThang() {
        return doanhThuThang;
    }

    public void setDoanhThuThang(Double doanhThuThang) {
        this.doanhThuThang = doanhThuThang;
    }
}
