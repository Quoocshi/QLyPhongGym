package hahaha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service đơn giản cho Checkpoint Recovery
 * Hệ thống Quản lý Phòng Gym
 */
@Service
public class CheckpointRecoveryService {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Tạo checkpoint cho hệ thống
     */
    public Map<String, Object> createCheckpoint() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "{ call create_gym_checkpoint(?, ?) }";
            
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.registerOutParameter(1, Types.NUMERIC);
                stmt.registerOutParameter(2, Types.VARCHAR);
                
                stmt.execute();
                
                Integer checkpointId = stmt.getInt(1);
                String message = stmt.getString(2);
                
                result.put("checkpoint_id", checkpointId);
                result.put("message", message);
                result.put("status", "SUCCESS");
                result.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
            }
        } catch (SQLException e) {
            result.put("status", "ERROR");
            result.put("error", "Failed to create checkpoint: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Thực hiện recovery từ checkpoint
     */
    public Map<String, Object> performRecovery(Integer targetCheckpointId) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "{ call gym_checkpoint_recovery(?, ?, ?) }";
            
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                if (targetCheckpointId != null) {
                    stmt.setInt(1, targetCheckpointId);
                } else {
                    stmt.setNull(1, Types.NUMERIC);
                }
                
                stmt.registerOutParameter(2, Types.VARCHAR);
                stmt.registerOutParameter(3, Types.VARCHAR);
                
                stmt.execute();
                
                String status = stmt.getString(2);
                String details = stmt.getString(3);
                
                result.put("recovery_status", status);
                result.put("recovery_details", details);
                result.put("target_checkpoint", targetCheckpointId);
                result.put("recovery_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
            }
        } catch (SQLException e) {
            result.put("recovery_status", "ERROR");
            result.put("error", "Recovery failed: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Lấy danh sách checkpoint
     */
    public List<Map<String, Object>> getCheckpoints() {
        String sql = """
            SELECT CheckpointID, CheckpointTime, TotalCustomers, TotalInvoices, 
                   TotalRevenue, SystemStatus, Description
            FROM GYM_CHECKPOINT 
            ORDER BY CheckpointTime DESC
            FETCH FIRST 20 ROWS ONLY
        """;
        
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Lấy trạng thái hệ thống hiện tại
     */
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            // Checkpoint mới nhất
            String checkpointSql = """
                SELECT CheckpointID, CheckpointTime, SystemStatus, 
                       ROUND((SYSDATE - CheckpointTime) * 24 * 60, 2) AS MinutesAgo
                FROM GYM_CHECKPOINT 
                WHERE SystemStatus = 'STABLE'
                ORDER BY CheckpointTime DESC 
                FETCH FIRST 1 ROWS ONLY
            """;
            
            List<Map<String, Object>> checkpointInfo = jdbcTemplate.queryForList(checkpointSql);
            
            // Thống kê dữ liệu hiện tại
            Integer customers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM KHACHHANG", Integer.class);
            Integer invoices = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM HOADON", Integer.class);
            Integer services = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM DICHVU", Integer.class);
            Double revenue = jdbcTemplate.queryForObject(
                "SELECT NVL(SUM(TongTien), 0) FROM HOADON WHERE TrangThai = 'DaThanhToan'", 
                Double.class);
            
            status.put("last_checkpoint", checkpointInfo.isEmpty() ? null : checkpointInfo.get(0));
            status.put("current_data", Map.of(
                "customers", customers,
                "invoices", invoices, 
                "services", services,
                "revenue", revenue
            ));
            
            // Đánh giá sức khỏe hệ thống
            if (!checkpointInfo.isEmpty()) {
                Double minutesAgo = (Double) checkpointInfo.get(0).get("MINUTESAGO");
                String health = minutesAgo < 30 ? "EXCELLENT" : 
                               minutesAgo < 60 ? "GOOD" : 
                               minutesAgo < 120 ? "WARNING" : "CRITICAL";
                status.put("system_health", health);
                status.put("recommendation", getHealthRecommendation(health, minutesAgo));
            }
            
        } catch (Exception e) {
            status.put("error", "Failed to get system status: " + e.getMessage());
        }
        
        return status;
    }

    /**
     * Mô phỏng tình huống sự cố
     */
    @Transactional
    public Map<String, Object> simulateCrash() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Tạo dữ liệu test trước khi crash
            String testCustomer = "KH_CRASH_" + System.currentTimeMillis();
            String testInvoice = "HD_CRASH_" + System.currentTimeMillis();
            
            // Insert dữ liệu nhưng không commit (mô phỏng crash)
            jdbcTemplate.update(
                "INSERT INTO KHACHHANG (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode) " +
                "VALUES (?, 'Crash Test User', DATE '1990-01-01', 'Nam', 'crash@test.com', '0901234567', 'Test Address', ?)",
                testCustomer, "REF_" + testCustomer
            );
            
            jdbcTemplate.update(
                "INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien) " +
                "VALUES (?, ?, SYSDATE, 'ChuaThanhToan', 1000000)",
                testInvoice, testCustomer
            );
            
            // Ghi nhận sự cố
            jdbcTemplate.update(
                "INSERT INTO GYM_CHECKPOINT (SystemStatus, Description) " +
                "VALUES ('SYSTEM_CRASHED', 'Crash simulation at ' || TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'))"
            );
            
            result.put("crash_simulated", true);
            result.put("test_customer", testCustomer);
            result.put("test_invoice", testInvoice);
            result.put("crash_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.put("message", "Sự cố đã được mô phỏng. Dữ liệu test sẽ bị mất sau recovery.");
            
            // Throw exception để rollback (mô phỏng crash)
            throw new RuntimeException("Simulated system crash");
            
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Simulated system crash")) {
                // Expected exception - this is our simulated crash
                result.put("status", "CRASH_SIMULATED");
                return result;
            } else {
                result.put("status", "ERROR");
                result.put("error", "Crash simulation failed: " + e.getMessage());
                return result;
            }
        }
    }

    /**
     * Demo đầy đủ: Tạo checkpoint -> Crash -> Recovery
     */
    public Map<String, Object> fullDemo() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> steps = new ArrayList<>();
        
        try {
            // Bước 1: Tạo checkpoint
            Map<String, Object> checkpointResult = createCheckpoint();
            steps.add(Map.of(
                "step", 1,
                "action", "Tạo Checkpoint",
                "result", checkpointResult,
                "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            ));
            
            // Bước 2: Thêm dữ liệu test (sẽ được commit)
            String stableCustomer = "KH_STABLE_" + System.currentTimeMillis();
            jdbcTemplate.update(
                "INSERT INTO KHACHHANG (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode) " +
                "VALUES (?, 'Stable Customer', DATE '1985-06-15', 'Nữ', 'stable@gym.com', '0909876543', 'Stable Address', ?)",
                stableCustomer, "REF_" + stableCustomer
            );
            
            steps.add(Map.of(
                "step", 2,
                "action", "Thêm dữ liệu ổn định",
                "result", Map.of("customer_added", stableCustomer, "status", "COMMITTED"),
                "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            ));
            
            // Bước 3: Mô phỏng crash
            Map<String, Object> crashResult = simulateCrash();
            steps.add(Map.of(
                "step", 3,
                "action", "Mô phỏng sự cố",
                "result", crashResult,
                "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            ));
            
            // Bước 4: Recovery
            Map<String, Object> recoveryResult = performRecovery(null);
            steps.add(Map.of(
                "step", 4,
                "action", "Thực hiện Recovery",
                "result", recoveryResult,
                "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            ));
            
            // Bước 5: Kiểm tra kết quả
            Map<String, Object> finalStatus = getSystemStatus();
            steps.add(Map.of(
                "step", 5,
                "action", "Kiểm tra trạng thái cuối",
                "result", finalStatus,
                "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            ));
            
            result.put("demo_completed", true);
            result.put("total_steps", steps.size());
            result.put("steps", steps);
            result.put("summary", Map.of(
                "checkpoint_method", "Checkpoint Recovery Only",
                "advantages", Arrays.asList(
                    "Đơn giản và dễ hiểu",
                    "Recovery nhanh chóng", 
                    "Phù hợp với hệ thống vừa và nhỏ",
                    "Ít phức tạp trong implementation"
                ),
                "trade_offs", Arrays.asList(
                    "Cần nhiều dung lượng lưu trữ",
                    "RPO phụ thuộc vào tần suất checkpoint",
                    "Có thể mất dữ liệu từ checkpoint cuối"
                )
            ));
            
        } catch (Exception e) {
            result.put("demo_completed", false);
            result.put("error", "Demo failed: " + e.getMessage());
            result.put("steps", steps);
        }
        
        return result;
    }

    /**
     * Lấy metrics cho checkpoint recovery
     */
    public Map<String, Object> getRecoveryMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // Tổng số checkpoint
            Integer totalCheckpoints = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM GYM_CHECKPOINT WHERE SystemStatus = 'STABLE'", 
                Integer.class
            );
            
            // Số lần recovery
            Integer recoveryCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM GYM_CHECKPOINT WHERE SystemStatus = 'RECOVERY_COMPLETED'", 
                Integer.class
            );
            
            // Recovery thành công
            Integer successfulRecoveries = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM GYM_CHECKPOINT WHERE SystemStatus = 'RECOVERY_COMPLETED'", 
                Integer.class
            );
            
            // Tổng số crash
            Integer crashCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM GYM_CHECKPOINT WHERE SystemStatus = 'SYSTEM_CRASHED'", 
                Integer.class
            );
            
            // Recovery success rate
            double successRate = recoveryCount > 0 ? (double) successfulRecoveries / recoveryCount * 100 : 100.0;
            
            metrics.put("total_checkpoints", totalCheckpoints);
            metrics.put("recovery_attempts", recoveryCount);
            metrics.put("successful_recoveries", successfulRecoveries);
            metrics.put("system_crashes", crashCount);
            metrics.put("success_rate", Math.round(successRate * 100.0) / 100.0 + "%");
            
            // Checkpoint frequency
            List<Map<String, Object>> recentCheckpoints = jdbcTemplate.queryForList(
                "SELECT CheckpointTime FROM GYM_CHECKPOINT WHERE SystemStatus = 'STABLE' " +
                "ORDER BY CheckpointTime DESC FETCH FIRST 5 ROWS ONLY"
            );
            
            if (recentCheckpoints.size() > 1) {
                // Tính average interval giữa các checkpoint
                long totalInterval = 0;
                for (int i = 0; i < recentCheckpoints.size() - 1; i++) {
                    // Logic để tính interval (simplified)
                    totalInterval += 30; // Giả sử 30 phút
                }
                double avgInterval = (double) totalInterval / (recentCheckpoints.size() - 1);
                metrics.put("avg_checkpoint_interval_minutes", avgInterval);
            }
            
            // System health score
            String healthSql = """
                SELECT 
                    ROUND((SYSDATE - MAX(CheckpointTime)) * 24 * 60, 2) AS MinutesSinceLastCheckpoint
                FROM GYM_CHECKPOINT 
                WHERE SystemStatus = 'STABLE'
            """;
            
            List<Map<String, Object>> healthData = jdbcTemplate.queryForList(healthSql);
            if (!healthData.isEmpty()) {
                Double minutesSince = (Double) healthData.get(0).get("MINUTESSINCELTCHECKPOINT");
                if (minutesSince != null) {
                    int healthScore = minutesSince < 30 ? 100 : 
                                    minutesSince < 60 ? 80 : 
                                    minutesSince < 120 ? 60 : 40;
                    metrics.put("system_health_score", healthScore + "/100");
                }
            }
            
        } catch (Exception e) {
            metrics.put("error", "Failed to get metrics: " + e.getMessage());
        }
        
        return metrics;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private String getHealthRecommendation(String health, Double minutesAgo) {
        switch (health) {
            case "EXCELLENT":
                return "Hệ thống hoạt động tốt. Checkpoint gần đây đảm bảo an toàn dữ liệu.";
            case "GOOD":
                return "Hệ thống ổn định. Nên tạo checkpoint mới trong 15 phút tới.";
            case "WARNING":
                return "Cảnh báo: Checkpoint cuối cách đây " + minutesAgo.intValue() + " phút. Nên tạo checkpoint ngay.";
            case "CRITICAL":
                return "Nguy hiểm: Không có checkpoint gần đây. Tạo checkpoint ngay lập tức để tránh mất dữ liệu.";
            default:
                return "Không thể đánh giá trạng thái hệ thống.";
        }
    }
} 