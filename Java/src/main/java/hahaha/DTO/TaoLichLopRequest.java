package hahaha.DTO;

import lombok.Data;

@Data
public class TaoLichLopRequest {
    private String maLop;
    private String ngayTap; // Single day (legacy)
    private String thuTap; // Multiple days (e.g., "246CN")
    private String caTap;
    private String maKV; // optional
}
