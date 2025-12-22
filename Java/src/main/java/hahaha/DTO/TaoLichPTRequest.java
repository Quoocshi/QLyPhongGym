package hahaha.DTO;

import lombok.Data;

@Data
public class TaoLichPTRequest {
    private String maKH;
    private String ngayTap; // Single date for old API (deprecated)
    private String ngayBatDau; // Start date for new API
    private String thuTap; // Multiple days concatenated (e.g., "246CN")
    private String caTap;
    private String maKV; // optional
}
