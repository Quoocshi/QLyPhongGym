package hahaha.DTO;

import lombok.Data;

@Data
public class TaoLichLopRequest {
    private String maLop;
    private String ngayTap;
    private String caTap;
    private String maKV; // optional
}
