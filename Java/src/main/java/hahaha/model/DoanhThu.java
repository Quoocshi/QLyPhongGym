package hahaha.model;

import java.sql.Date;

public class DoanhThu {
    private Date ngay;
    private Double tongTien;

    public DoanhThu(Date ngay, Double tongTien) {
        this.ngay = ngay;
        this.tongTien = tongTien;
    }

    public Date getNgay() {
        return ngay;
    }

    public void setNgay(Date ngay) {
        this.ngay = ngay;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }
}
