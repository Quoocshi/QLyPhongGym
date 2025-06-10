package hahaha.controller;

import hahaha.service.CheckpointRecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller cho Checkpoint Recovery System
 * Hệ thống Quản lý Phòng Gym
 */
@RestController
@RequestMapping("/api/checkpoint")
@CrossOrigin(origins = "*")
public class CheckpointController {

    @Autowired
    private CheckpointRecoveryService checkpointService;

    /**
     * Demo đầy đủ hệ thống Checkpoint Recovery
     */
    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> fullDemo() {
        try {
            Map<String, Object> result = checkpointService.fullDemo();
            return ResponseEntity.ok(Map.of(
                "title", "🎯 DEMO CHECKPOINT RECOVERY - HỆ THỐNG QUẢN LÝ PHÒNG GYM",
                "description", "Mô phỏng đầy đủ quy trình: Checkpoint → Sự cố → Recovery",
                "demo_result", result,
                "conclusion", Map.of(
                    "method", "Checkpoint Recovery",
                    "suitability", "Phù hợp cho hệ thống gym vì đơn giản, hiệu quả",
                    "key_benefits", "Dễ hiểu, dễ bảo trì, recovery nhanh"
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Demo failed: " + e.getMessage()));
        }
    }

    /**
     * Tạo checkpoint thủ công
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCheckpoint() {
        try {
            Map<String, Object> result = checkpointService.createCheckpoint();
            return ResponseEntity.ok(Map.of(
                "action", "Tạo Checkpoint",
                "result", result,
                "message", "Checkpoint đã được tạo thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to create checkpoint: " + e.getMessage()));
        }
    }

    /**
     * Thực hiện recovery
     */
    @PostMapping("/recover")
    public ResponseEntity<Map<String, Object>> performRecovery(@RequestParam(required = false) Integer checkpointId) {
        try {
            Map<String, Object> result = checkpointService.performRecovery(checkpointId);
            return ResponseEntity.ok(Map.of(
                "action", "Thực hiện Recovery", 
                "result", result,
                "message", checkpointId != null ? 
                    "Recovery từ checkpoint " + checkpointId + " hoàn tất" :
                    "Recovery từ checkpoint gần nhất hoàn tất"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Recovery failed: " + e.getMessage()));
        }
    }

    /**
     * Mô phỏng sự cố
     */
    @PostMapping("/crash")
    public ResponseEntity<Map<String, Object>> simulateCrash() {
        try {
            Map<String, Object> result = checkpointService.simulateCrash();
            return ResponseEntity.ok(Map.of(
                "action", "Mô phỏng Sự cố",
                "result", result,
                "message", "Sự cố hệ thống đã được mô phỏng thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Crash simulation failed: " + e.getMessage()));
        }
    }

    /**
     * Lấy trạng thái hệ thống
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        try {
            Map<String, Object> result = checkpointService.getSystemStatus();
            return ResponseEntity.ok(Map.of(
                "system_status", result,
                "timestamp", java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get system status: " + e.getMessage()));
        }
    }

    /**
     * Lấy danh sách checkpoint
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getCheckpoints() {
        try {
            var checkpoints = checkpointService.getCheckpoints();
            return ResponseEntity.ok(Map.of(
                "checkpoints", checkpoints,
                "total_count", checkpoints.size(),
                "message", "Danh sách 20 checkpoint gần nhất"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get checkpoints: " + e.getMessage()));
        }
    }

    /**
     * Lấy metrics và thống kê
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        try {
            Map<String, Object> result = checkpointService.getRecoveryMetrics();
            return ResponseEntity.ok(Map.of(
                "metrics", result,
                "description", "Thống kê hiệu suất Checkpoint Recovery System",
                "generated_at", java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get metrics: " + e.getMessage()));
        }
    }

    /**
     * Scenarios cụ thể cho presentation
     */
    @PostMapping("/scenario/{type}")
    public ResponseEntity<Map<String, Object>> runScenario(@PathVariable String type) {
        try {
            Map<String, Object> result;
            
            switch (type.toLowerCase()) {
                case "cao-diem":
                    result = runPeakHourScenario();
                    break;
                case "thanh-toan":
                    result = runPaymentScenario();
                    break;
                case "bao-tri":
                    result = runMaintenanceScenario();
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unknown scenario type: " + type));
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Scenario failed: " + e.getMessage()));
        }
    }

    /**
     * API tổng quan về Checkpoint Recovery
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        return ResponseEntity.ok(Map.of(
            "title", "🏋️‍♂️ HỆ THỐNG CHECKPOINT RECOVERY - QUẢN LÝ PHÒNG GYM",
            "method", "Checkpoint Recovery Only",
            "description", "Hệ thống phục hồi dữ liệu đơn giản, hiệu quả cho phòng gym",
            
            "how_it_works", Map.of(
                "step1", "📁 Tạo Checkpoint: Backup toàn bộ dữ liệu quan trọng",
                "step2", "📝 Ghi Log: Theo dõi các transaction đã commit sau checkpoint",
                "step3", "⚡ Khi có sự cố: Hệ thống phát hiện và ghi nhận crash",
                "step4", "🔄 Recovery: Khôi phục từ backup + redo các commit sau checkpoint"
            ),
            
            "advantages", Map.of(
                "simplicity", "Đơn giản, dễ hiểu và dễ implement",
                "speed", "Recovery nhanh chóng (chỉ restore + redo)",
                "reliability", "Backup đầy đủ đảm bảo an toàn dữ liệu",
                "maintenance", "Ít phức tạp trong bảo trì và troubleshooting"
            ),
            
            "trade_offs", Map.of(
                "storage", "Cần nhiều dung lượng cho full backup",
                "rpo", "RPO phụ thuộc vào tần suất tạo checkpoint",
                "data_loss", "Có thể mất dữ liệu từ checkpoint cuối đến khi crash"
            ),
            
            "gym_specific_benefits", Map.of(
                "member_data", "Bảo vệ thông tin khách hàng và thẻ thành viên",
                "payment_data", "Đảm bảo giao dịch thanh toán không bị mất",
                "service_registration", "Khôi phục đăng ký dịch vụ và lịch tập",
                "business_continuity", "Giảm thiểu downtime, duy trì hoạt động kinh doanh"
            ),
            
            "recommended_schedule", Map.of(
                "peak_hours", "Mỗi 15-30 phút (17:00-21:00)",
                "normal_hours", "Mỗi 1-2 giờ",
                "maintenance", "Backup đầy đủ hàng ngày vào 2:00 AM"
            ),
            
            "api_endpoints", Map.of(
                "demo", "POST /api/checkpoint/demo - Demo đầy đủ",
                "create", "POST /api/checkpoint/create - Tạo checkpoint",
                "recover", "POST /api/checkpoint/recover - Thực hiện recovery",
                "status", "GET /api/checkpoint/status - Trạng thái hệ thống",
                "metrics", "GET /api/checkpoint/metrics - Thống kê hiệu suất"
            )
        ));
    }

    // ==================== PRIVATE SCENARIO METHODS ====================

    private Map<String, Object> runPeakHourScenario() {
        return Map.of(
            "scenario", "🕕 Cao điểm tối thứ 6",
            "situation", "20 khách hàng đăng ký dịch vụ đồng thời lúc 18:30",
            "timeline", Map.of(
                "18:20", "Tạo checkpoint trước giờ cao điểm",
                "18:25", "Khách hàng bắt đầu đăng ký massively", 
                "18:35", "⚡ Mất điện đột ngột do quá tải",
                "18:40", "Phát hiện sự cố, bắt đầu recovery",
                "18:43", "Recovery hoàn tất từ checkpoint 18:20"
            ),
            "result", Map.of(
                "recovered_registrations", 15,
                "lost_registrations", 5,
                "downtime", "3 phút",
                "customer_impact", "5 khách hàng cần đăng ký lại"
            ),
            "lesson", "Checkpoint thường xuyên trong giờ cao điểm rất quan trọng"
        );
    }

    private Map<String, Object> runPaymentScenario() {
        return Map.of(
            "scenario", "💳 Sự cố thanh toán",
            "situation", "Hệ thống crash khi đang xử lý nhiều giao dịch thanh toán",
            "timeline", Map.of(
                "14:00", "Checkpoint tạo sau giờ trưa",
                "14:30", "Nhiều khách thanh toán gói tập và PT",
                "14:45", "🔄 System crash khi xử lý payment gateway",
                "14:48", "Recovery được kích hoạt tự động",
                "14:52", "Tất cả thanh toán được verify và restore"
            ),
            "result", Map.of(
                "payments_recovered", 12,
                "payments_verified", 12,
                "no_double_charge", true,
                "customer_satisfaction", "100%"
            ),
            "lesson", "Checkpoint bảo vệ tốt dữ liệu tài chính quan trọng"
        );
    }

    private Map<String, Object> runMaintenanceScenario() {
        return Map.of(
            "scenario", "🔧 Bảo trì hệ thống",
            "situation", "Nâng cấp database trong giờ ít khách",
            "timeline", Map.of(
                "02:00", "Tạo checkpoint trước bảo trì",
                "02:05", "Bắt đầu upgrade database schema",
                "02:30", "❌ Upgrade thất bại, cần rollback",
                "02:35", "Recovery về checkpoint 02:00",
                "02:40", "Hệ thống hoạt động bình thường"
            ),
            "result", Map.of(
                "rollback_time", "5 phút",
                "data_integrity", "100%", 
                "zero_data_loss", true,
                "business_impact", "Không ảnh hưởng (giờ đóng cửa)"
            ),
            "lesson", "Checkpoint là safety net hoàn hảo cho maintenance"
        );
    }
} 