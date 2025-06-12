-- =====================================================================================
-- PROCEDURE ĐĂNG KÝ DỊCH VỤ TỔNG HỢP HOÀN CHỈNH (THAY THẾ TẤT CẢ PROCEDURE CŨ)
-- Xử lý đăng ký hỗn hợp: TuDo + PT + Lop trong cùng 1 hóa đơn
-- =====================================================================================

CREATE OR REPLACE PROCEDURE proc_dang_ky_dich_vu_universal(
    p_ma_kh IN VARCHAR2,
    p_list_ma_dv IN VARCHAR2, -- Danh sách mã dịch vụ cách nhau bởi dấu phẩy
    p_list_trainer_id IN VARCHAR2, -- Danh sách trainerId tương ứng (NULL hoặc empty nếu không có)
    p_list_class_id IN VARCHAR2, -- Danh sách classId tương ứng (NULL hoặc empty nếu không có)
    p_ma_hd OUT VARCHAR2,
    p_tong_tien OUT NUMBER,
    p_result OUT VARCHAR2,
    p_error_msg OUT VARCHAR2
)
AS
    v_ma_hd VARCHAR2(10);
    v_tong_tien NUMBER := 0;
    v_ma_ctdk VARCHAR2(10);
    v_ma_dv VARCHAR2(10);
    v_trainer_id VARCHAR2(10);
    v_class_id VARCHAR2(10);
    v_don_gia NUMBER;
    v_thoi_han NUMBER;
    v_loai_dv VARCHAR2(20);
    v_ma_bm VARCHAR2(10);
    v_ma_lop VARCHAR2(10);
    v_ma_nv VARCHAR2(10);
    
    -- Variables cho parsing
    v_pos NUMBER := 1;
    v_next_pos NUMBER;
    v_temp_dv_list VARCHAR2(4000) := p_list_ma_dv || ',';
    v_temp_trainer_list VARCHAR2(4000) := NVL(p_list_trainer_id, '') || ',';
    v_temp_class_list VARCHAR2(4000) := NVL(p_list_class_id, '') || ',';
    
    -- Index cho từng service
    v_service_index NUMBER := 0;
    
    -- Số thứ tự cho mã
    v_max_hd_number NUMBER;
    v_current_ctdk_number NUMBER;
    
    -- Kiểm tra tồn tại
    v_count NUMBER;
    
    -- Variables cho retry logic (xử lý concurrency)
    v_retry_count NUMBER := 0;
    v_max_retries NUMBER := 3;
    
    -- Exception tự định nghĩa
    ex_khach_hang_not_found EXCEPTION;
    ex_dich_vu_not_found EXCEPTION;
    ex_trainer_not_found EXCEPTION;
    ex_class_not_found EXCEPTION;
    ex_dich_vu_da_dang_ky EXCEPTION;
    ex_trainer_khong_phu_hop EXCEPTION;
    ex_class_khong_phu_hop EXCEPTION;
    ex_duplicate_key EXCEPTION;
    
    -- Exception cho Oracle system errors
    PRAGMA EXCEPTION_INIT(ex_duplicate_key, -00001); -- ORA-00001: unique constraint violated
    
BEGIN
    -- Loop để retry trong trường hợp có conflict
    WHILE v_retry_count <= v_max_retries LOOP
        BEGIN
            -- Savepoint trước khi thực hiện insert
            SAVEPOINT before_processing;
            
            -- 1. Kiểm tra khách hàng tồn tại
            SELECT COUNT(*) INTO v_count
            FROM KHACHHANG 
            WHERE MaKH = p_ma_kh;
            
            IF v_count = 0 THEN
                RAISE ex_khach_hang_not_found;
            END IF;
            
            -- 2. Tạo mã hóa đơn unique
            SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaHD, 3))), 0) + 1 
            INTO v_max_hd_number
            FROM HOADON 
            WHERE MaHD LIKE 'HD%';
            
            v_ma_hd := 'HD' || LPAD(v_max_hd_number, 3, '0');
            
            -- Kiểm tra unique và tạo lại nếu trùng
            LOOP
                SELECT COUNT(*) INTO v_count FROM HOADON WHERE MaHD = v_ma_hd;
                EXIT WHEN v_count = 0;
                v_max_hd_number := v_max_hd_number + 1;
                v_ma_hd := 'HD' || LPAD(v_max_hd_number, 3, '0');
            END LOOP;
            
            -- 3. Tạo hóa đơn
            INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
            VALUES (v_ma_hd, p_ma_kh, SYSDATE, 'ChuaThanhToan', 0);
            
            -- 4. Reset variables cho processing
            v_pos := 1;
            v_service_index := 0;
            v_tong_tien := 0;
            
            -- 5. Xử lý từng dịch vụ trong danh sách
            WHILE v_pos <= LENGTH(v_temp_dv_list) LOOP
                -- Tìm vị trí dấu phẩy tiếp theo
                v_next_pos := INSTR(v_temp_dv_list, ',', v_pos);
                
                IF v_next_pos > 0 THEN
                    -- Lấy mã dịch vụ
                    v_ma_dv := TRIM(SUBSTR(v_temp_dv_list, v_pos, v_next_pos - v_pos));
                    
                    IF LENGTH(v_ma_dv) > 0 THEN
                        -- Lấy trainerId và classId tương ứng (parse manually)
                        DECLARE
                            v_trainer_start NUMBER := 1;
                            v_trainer_end NUMBER;
                            v_class_start NUMBER := 1;
                            v_class_end NUMBER;
                            v_current_idx NUMBER := 0;
                        BEGIN
                            -- Parse trainerId
                            WHILE v_current_idx <= v_service_index AND v_trainer_start <= LENGTH(v_temp_trainer_list) LOOP
                                v_trainer_end := INSTR(v_temp_trainer_list, ',', v_trainer_start);
                                IF v_trainer_end = 0 THEN
                                    v_trainer_end := LENGTH(v_temp_trainer_list) + 1;
                                END IF;
                                
                                IF v_current_idx = v_service_index THEN
                                    v_trainer_id := TRIM(SUBSTR(v_temp_trainer_list, v_trainer_start, v_trainer_end - v_trainer_start));
                                    IF LENGTH(v_trainer_id) = 0 OR v_trainer_id = 'null' THEN
                                        v_trainer_id := NULL;
                                    END IF;
                                    EXIT;
                                END IF;
                                
                                v_current_idx := v_current_idx + 1;
                                v_trainer_start := v_trainer_end + 1;
                            END LOOP;
                            
                            -- Parse classId
                            v_current_idx := 0;
                            WHILE v_current_idx <= v_service_index AND v_class_start <= LENGTH(v_temp_class_list) LOOP
                                v_class_end := INSTR(v_temp_class_list, ',', v_class_start);
                                IF v_class_end = 0 THEN
                                    v_class_end := LENGTH(v_temp_class_list) + 1;
                                END IF;
                                
                                IF v_current_idx = v_service_index THEN
                                    v_class_id := TRIM(SUBSTR(v_temp_class_list, v_class_start, v_class_end - v_class_start));
                                    IF LENGTH(v_class_id) = 0 OR v_class_id = 'null' THEN
                                        v_class_id := NULL;
                                    END IF;
                                    EXIT;
                                END IF;
                                
                                v_current_idx := v_current_idx + 1;
                                v_class_start := v_class_end + 1;
                            END LOOP;
                        END;
                        
                        -- Kiểm tra dịch vụ tồn tại và lấy thông tin
                        SELECT COUNT(*), MAX(DonGia), MAX(ThoiHan), MAX(LoaiDV), MAX(MaBM)
                        INTO v_count, v_don_gia, v_thoi_han, v_loai_dv, v_ma_bm
                        FROM DICHVU 
                        WHERE MaDV = v_ma_dv;
                        
                        IF v_count = 0 THEN
                            p_error_msg := 'Không tìm thấy dịch vụ: ' || v_ma_dv;
                            RAISE ex_dich_vu_not_found;
                        END IF;
                        
                        -- Kiểm tra khách hàng đã đăng ký dịch vụ này chưa
                        SELECT COUNT(*) INTO v_count
                        FROM CT_DKDV ct
                        JOIN HOADON hd ON ct.MaHD = hd.MaHD
                        WHERE hd.MaKH = p_ma_kh AND ct.MaDV = v_ma_dv;
                        
                        IF v_count > 0 THEN
                            p_error_msg := 'Khách hàng đã đăng ký dịch vụ: ' || v_ma_dv;
                            RAISE ex_dich_vu_da_dang_ky;
                        END IF;
                        
                        -- Tạo mã chi tiết đăng ký unique
                        SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaCTDK, 3))), 0) + 1
                        INTO v_current_ctdk_number
                        FROM CT_DKDV 
                        WHERE MaCTDK LIKE 'CT%';
                        
                        v_ma_ctdk := 'CT' || LPAD(v_current_ctdk_number, 3, '0');
                        
                        -- Kiểm tra unique và tạo lại nếu trùng
                        LOOP
                            SELECT COUNT(*) INTO v_count FROM CT_DKDV WHERE MaCTDK = v_ma_ctdk;
                            EXIT WHEN v_count = 0;
                            v_current_ctdk_number := v_current_ctdk_number + 1;
                            v_ma_ctdk := 'CT' || LPAD(v_current_ctdk_number, 3, '0');
                        END LOOP;
                        
                        -- Xử lý assignment theo loại dịch vụ
                        v_ma_lop := NULL;
                        v_ma_nv := NULL;
                        
                        -- =================================================================
                        -- LOGIC XỬ LÝ THEO LOẠI DỊCH VỤ
                        -- =================================================================
                        
                        IF v_loai_dv = 'PT' THEN
                            -- ============= DỊCH VỤ PT =============
                            IF v_trainer_id IS NOT NULL THEN
                                -- Sử dụng trainer được chỉ định
                                SELECT COUNT(*) INTO v_count
                                FROM NHANVIEN 
                                WHERE MaNV = v_trainer_id AND LoaiNV = 'Trainer';
                                
                                IF v_count = 0 THEN
                                    p_error_msg := 'Không tìm thấy trainer: ' || v_trainer_id;
                                    RAISE ex_trainer_not_found;
                                END IF;
                                
                                -- Kiểm tra trainer có chuyên môn phù hợp
                                SELECT COUNT(*) INTO v_count
                                FROM CT_CHUYENMON cm
                                WHERE cm.MaNV = v_trainer_id AND cm.MaBM = v_ma_bm;
                                
                                IF v_count = 0 THEN
                                    p_error_msg := 'Trainer ' || v_trainer_id || ' không có chuyên môn phù hợp với ' || v_ma_bm;
                                    RAISE ex_trainer_khong_phu_hop;
                                END IF;
                                
                                v_ma_nv := v_trainer_id;
                                
                            ELSE
                                -- Auto-assign trainer có chuyên môn phù hợp
                                BEGIN
                                    SELECT MaNV INTO v_ma_nv
                                    FROM (
                                        SELECT nv.MaNV
                                        FROM NHANVIEN nv
                                        JOIN CT_CHUYENMON cm ON nv.MaNV = cm.MaNV
                                        WHERE nv.LoaiNV = 'Trainer' AND cm.MaBM = v_ma_bm
                                        ORDER BY nv.MaNV
                                    ) WHERE ROWNUM = 1;
                                EXCEPTION
                                    WHEN NO_DATA_FOUND THEN
                                        p_error_msg := 'Không tìm thấy trainer phù hợp cho bộ môn: ' || v_ma_bm;
                                        RAISE ex_trainer_not_found;
                                END;
                            END IF;
                            
                        ELSIF v_loai_dv = 'Lop' THEN
                            -- ============= DỊCH VỤ LỚP =============
                            IF v_class_id IS NOT NULL THEN
                                -- Sử dụng lớp được chỉ định
                                SELECT COUNT(*), MAX(MaNV) INTO v_count, v_ma_nv
                                FROM LOP 
                                WHERE MaLop = v_class_id AND MaBM = v_ma_bm AND TinhTrang = 'ChuaDay';
                                
                                IF v_count = 0 THEN
                                    -- Kiểm tra xem lớp có tồn tại không
                                    SELECT COUNT(*) INTO v_count FROM LOP WHERE MaLop = v_class_id;
                                    IF v_count = 0 THEN
                                        p_error_msg := 'Không tìm thấy lớp: ' || v_class_id;
                                    ELSE
                                        p_error_msg := 'Lớp ' || v_class_id || ' không thuộc bộ môn ' || v_ma_bm || ' hoặc đã đầy';
                                    END IF;
                                    RAISE ex_class_not_found;
                                END IF;
                                
                                v_ma_lop := v_class_id;
                                
                            ELSE
                                -- Auto-assign lớp chưa đầy
                                BEGIN
                                    SELECT MaLop, MaNV INTO v_ma_lop, v_ma_nv
                                    FROM (
                                        SELECT MaLop, MaNV
                                        FROM LOP 
                                        WHERE MaBM = v_ma_bm AND TinhTrang = 'ChuaDay'
                                        ORDER BY MaLop
                                    ) WHERE ROWNUM = 1;
                                EXCEPTION
                                    WHEN NO_DATA_FOUND THEN
                                        p_error_msg := 'Không còn lớp trống cho bộ môn: ' || v_ma_bm;
                                        RAISE ex_class_not_found;
                                END;
                            END IF;
                            
                        ELSIF v_loai_dv = 'TuDo' THEN
                            -- ============= DỊCH VỤ TỰ DO =============
                            -- Không cần assignment, giữ nguyên v_ma_lop = NULL, v_ma_nv = NULL
                            NULL;
                            
                        ELSE
                            p_error_msg := 'Loại dịch vụ không hợp lệ: ' || v_loai_dv;
                            RAISE ex_dich_vu_not_found;
                        END IF;
                        
                        -- =================================================================
                        -- THÊM CHI TIẾT ĐĂNG KÝ DỊCH VỤ
                        -- =================================================================
                        
                        INSERT INTO CT_DKDV (
                            MaCTDK, MaHD, MaDV, NgayBD, NgayKT, MaLop, MaNV
                        ) VALUES (
                            v_ma_ctdk,
                            v_ma_hd,
                            v_ma_dv,
                            SYSDATE,
                            SYSDATE + v_thoi_han,
                            v_ma_lop,
                            v_ma_nv
                        );
                        
                        -- Cộng dồn tổng tiền
                        v_tong_tien := v_tong_tien + v_don_gia;
                        
                        -- Tăng service index
                        v_service_index := v_service_index + 1;
                        
                        -- Log thành công
                        DBMS_OUTPUT.PUT_LINE('✅ Đã thêm: ' || v_ma_dv || ' (' || v_loai_dv || ') - Trainer: ' || 
                                           NVL(v_ma_nv, 'NULL') || ' - Lớp: ' || NVL(v_ma_lop, 'NULL'));
                    END IF;
                    
                    -- Chuyển đến vị trí tiếp theo
                    v_pos := v_next_pos + 1;
                ELSE
                    EXIT; -- Hết danh sách
                END IF;
            END LOOP;
            
            -- 6. Cập nhật tổng tiền hóa đơn
            UPDATE HOADON 
            SET TongTien = v_tong_tien 
            WHERE MaHD = v_ma_hd;
            
            -- 7. Commit transaction nếu thành công
            COMMIT;
            
            -- 8. Trả về kết quả
            p_ma_hd := v_ma_hd;
            p_tong_tien := v_tong_tien;
            p_result := 'SUCCESS';
            p_error_msg := NULL;
            
            -- Thoát khỏi retry loop nếu thành công
            EXIT;
            
        EXCEPTION
            WHEN ex_duplicate_key THEN
                -- Rollback và retry
                ROLLBACK TO before_processing;
                v_retry_count := v_retry_count + 1;
                
                IF v_retry_count > v_max_retries THEN
                    p_result := 'ERROR';
                    p_error_msg := 'Hệ thống đang bận, vui lòng thử lại sau (Mã trùng lặp)';
                    ROLLBACK;
                    RETURN;
                END IF;
                
                -- Đợi một chút trước khi retry
                DBMS_LOCK.SLEEP(0.1);
                
            WHEN ex_khach_hang_not_found THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                p_error_msg := 'Không tìm thấy khách hàng: ' || p_ma_kh;
                ROLLBACK;
                RETURN;
                
            WHEN ex_dich_vu_not_found THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN ex_trainer_not_found THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN ex_class_not_found THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN ex_dich_vu_da_dang_ky THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN ex_trainer_khong_phu_hop THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN OTHERS THEN
                -- Kiểm tra nếu là lỗi concurrent access
                IF SQLCODE = -54 OR SQLCODE = -30006 OR SQLCODE = -1 THEN -- Resource busy hoặc unique constraint
                    ROLLBACK TO before_processing;
                    v_retry_count := v_retry_count + 1;
                    
                    IF v_retry_count > v_max_retries THEN
                        p_result := 'ERROR';
                        p_error_msg := 'Hệ thống đang bận, vui lòng thử lại sau (Lỗi: ' || SQLCODE || ')';
                        ROLLBACK;
                        RETURN;
                    END IF;
                    
                    -- Đợi một chút trước khi retry
                    DBMS_LOCK.SLEEP(0.2);
                ELSE
                    ROLLBACK TO before_processing;
                    p_result := 'ERROR';
                    p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
                    ROLLBACK;
                    RETURN;
                END IF;
        END;
    END LOOP;
    
    -- Nếu vẫn không thành công sau max retries
    IF v_retry_count > v_max_retries THEN
        p_result := 'ERROR';
        p_error_msg := 'Không thể hoàn thành đăng ký sau ' || v_max_retries || ' lần thử';
        ROLLBACK;
    END IF;
    
END;
/

-- =====================================================================================
-- PROCEDURE THANH TOÁN HÓA ĐƠN
-- =====================================================================================

CREATE OR REPLACE PROCEDURE proc_thanh_toan_hoa_don(
    p_ma_hd IN VARCHAR2,
    p_result OUT VARCHAR2,
    p_error_msg OUT VARCHAR2
)
AS
    v_count NUMBER;
    ex_hoa_don_not_found EXCEPTION;
    ex_hoa_don_da_thanh_toan EXCEPTION;
    v_trang_thai VARCHAR2(50);
BEGIN
    -- Kiểm tra hóa đơn tồn tại
    SELECT COUNT(*), MAX(TrangThai) 
    INTO v_count, v_trang_thai
    FROM HOADON 
    WHERE MaHD = p_ma_hd;
    
    IF v_count = 0 THEN
        RAISE ex_hoa_don_not_found;
    END IF;
    
    IF v_trang_thai = 'DaThanhToan' THEN
        RAISE ex_hoa_don_da_thanh_toan;
    END IF;
    
    -- Cập nhật trạng thái thanh toán
    UPDATE HOADON 
    SET TrangThai = 'DaThanhToan',
        NgayTT = SYSDATE
    WHERE MaHD = p_ma_hd;
    
    COMMIT;
    
    p_result := 'SUCCESS';
    p_error_msg := NULL;
    
EXCEPTION
    WHEN ex_hoa_don_not_found THEN
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy hóa đơn: ' || p_ma_hd;
        
    WHEN ex_hoa_don_da_thanh_toan THEN
        p_result := 'ERROR';
        p_error_msg := 'Hóa đơn đã được thanh toán: ' || p_ma_hd;
        
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
END;
/

-- =====================================================================================
-- FUNCTION HỖ TRỢ: Lấy thông tin hóa đơn chi tiết
-- =====================================================================================

CREATE OR REPLACE FUNCTION get_hoa_don_chi_tiet(p_ma_hd IN VARCHAR2)
RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT 
            hd.MaHD,
            hd.TongTien,
            hd.NgayLap,
            hd.TrangThai,
            hd.NgayTT,
            kh.MaKH,
            kh.HoTen,
            kh.Email,
            ct.MaCTDK,
            ct.NgayBD,
            ct.NgayKT,
            dv.MaDV,
            dv.TenDV,
            dv.DonGia,
            dv.ThoiHan,
            dv.LoaiDV,
            bm.TenBM,
            l.TenLop,
            nv.TenNV as TenNhanVien
        FROM HOADON hd
        JOIN KHACHHANG kh ON hd.MaKH = kh.MaKH
        JOIN CT_DKDV ct ON hd.MaHD = ct.MaHD
        JOIN DICHVU dv ON ct.MaDV = dv.MaDV
        LEFT JOIN BOMON bm ON dv.MaBM = bm.MaBM
        LEFT JOIN LOP l ON ct.MaLop = l.MaLop
        LEFT JOIN NHANVIEN nv ON ct.MaNV = nv.MaNV
        WHERE hd.MaHD = p_ma_hd
        ORDER BY ct.MaCTDK;
        
    RETURN v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20006, 'Error in get_hoa_don_chi_tiet: ' || SQLERRM);
END;
/

-- =====================================================================================
-- PROCEDURE KIỂM TRA ĐIỀU KIỆN ĐĂNG KÝ
-- =====================================================================================

CREATE OR REPLACE PROCEDURE proc_kiem_tra_dieu_kien_dang_ky(
    p_ma_kh IN VARCHAR2,
    p_ma_dv IN VARCHAR2,
    p_can_dang_ky OUT NUMBER, -- 1: có thể đăng ký, 0: không thể
    p_ly_do OUT VARCHAR2
)
AS
    v_count NUMBER;
    v_loai_dv VARCHAR2(20);
    v_ma_bm VARCHAR2(10);
BEGIN
    -- Kiểm tra khách hàng tồn tại
    SELECT COUNT(*) INTO v_count
    FROM KHACHHANG WHERE MaKH = p_ma_kh;
    
    IF v_count = 0 THEN
        p_can_dang_ky := 0;
        p_ly_do := 'Khách hàng không tồn tại';
        RETURN;
    END IF;
    
    -- Kiểm tra dịch vụ tồn tại
    SELECT COUNT(*), MAX(LoaiDV), MAX(MaBM)
    INTO v_count, v_loai_dv, v_ma_bm
    FROM DICHVU WHERE MaDV = p_ma_dv;
    
    IF v_count = 0 THEN
        p_can_dang_ky := 0;
        p_ly_do := 'Dịch vụ không tồn tại';
        RETURN;
    END IF;
    
    -- Kiểm tra đã đăng ký chưa
    SELECT COUNT(*) INTO v_count
    FROM CT_DKDV ct
    JOIN HOADON hd ON ct.MaHD = hd.MaHD
    WHERE hd.MaKH = p_ma_kh AND ct.MaDV = p_ma_dv;
    
    IF v_count > 0 THEN
        p_can_dang_ky := 0;
        p_ly_do := 'Đã đăng ký dịch vụ này rồi';
        RETURN;
    END IF;
    
    -- Kiểm tra lớp còn chỗ (nếu không phải dịch vụ tự do)
    IF v_loai_dv != 'TuDo' THEN
        SELECT COUNT(*) INTO v_count
        FROM LOP 
        WHERE MaBM = v_ma_bm AND TinhTrang = 'ChuaDay';
        
        IF v_count = 0 THEN
            p_can_dang_ky := 0;
            p_ly_do := 'Không còn lớp trống cho bộ môn này';
            RETURN;
        END IF;
    END IF;
    
    -- Tất cả điều kiện đều OK
    p_can_dang_ky := 1;
    p_ly_do := 'Có thể đăng ký';
    
EXCEPTION
    WHEN OTHERS THEN
        p_can_dang_ky := 0;
        p_ly_do := 'Lỗi hệ thống: ' || SQLERRM;
END;
/

-- =====================================================================================
-- PROCEDURE ĐĂNG KÝ DỊCH VỤ PT VỚI TRAINER CỤ THỂ
-- =====================================================================================

CREATE OR REPLACE PROCEDURE proc_dang_ky_dich_vu_pt(
    p_ma_kh IN VARCHAR2,
    p_ma_dv IN VARCHAR2,
    p_ma_trainer IN VARCHAR2, -- Mã trainer được chỉ định
    p_ma_hd OUT VARCHAR2,
    p_ma_ctdk OUT VARCHAR2,
    p_result OUT VARCHAR2,
    p_error_msg OUT VARCHAR2
)
AS
    v_ma_hd VARCHAR2(10);
    v_ma_ctdk VARCHAR2(10);
    v_don_gia NUMBER;
    v_thoi_han NUMBER;
    v_loai_dv VARCHAR2(20);
    v_ma_bm VARCHAR2(10);
    
    -- Số thứ tự cho mã
    v_max_hd_number NUMBER;
    v_max_ctdk_number NUMBER;
    
    -- Kiểm tra tồn tại
    v_count_kh NUMBER;
    v_count_dv NUMBER;
    v_count_trainer NUMBER;
    v_count_existing NUMBER;
    
    -- Exception tự định nghĩa
    ex_khach_hang_not_found EXCEPTION;
    ex_dich_vu_not_found EXCEPTION;
    ex_trainer_not_found EXCEPTION;
    ex_dich_vu_da_dang_ky EXCEPTION;
    ex_khong_phai_pt EXCEPTION;
    ex_trainer_khong_phu_hop EXCEPTION;
    
BEGIN
    -- 1. Kiểm tra khách hàng tồn tại
    SELECT COUNT(*) INTO v_count_kh
    FROM KHACHHANG 
    WHERE MaKH = p_ma_kh;
    
    IF v_count_kh = 0 THEN
        RAISE ex_khach_hang_not_found;
    END IF;
    
    -- 2. Kiểm tra dịch vụ tồn tại và lấy thông tin
    SELECT COUNT(*), MAX(DonGia), MAX(ThoiHan), MAX(LoaiDV), MAX(MaBM)
    INTO v_count_dv, v_don_gia, v_thoi_han, v_loai_dv, v_ma_bm
    FROM DICHVU 
    WHERE MaDV = p_ma_dv;
    
    IF v_count_dv = 0 THEN
        RAISE ex_dich_vu_not_found;
    END IF;
    
    -- 3. Kiểm tra xem có phải dịch vụ PT không
    IF v_loai_dv != 'PT' THEN
        RAISE ex_khong_phai_pt;
    END IF;
    
    -- 4. Kiểm tra trainer tồn tại
    SELECT COUNT(*) INTO v_count_trainer
    FROM NHANVIEN 
    WHERE MaNV = p_ma_trainer AND LoaiNV = 'Trainer';
    
    IF v_count_trainer = 0 THEN
        RAISE ex_trainer_not_found;
    END IF;
    
    -- 5. Kiểm tra trainer có chuyên môn phù hợp không
    SELECT COUNT(*) INTO v_count_trainer
    FROM CT_CHUYENMON cm
    WHERE cm.MaNV = p_ma_trainer AND cm.MaBM = v_ma_bm;
    
    IF v_count_trainer = 0 THEN
        RAISE ex_trainer_khong_phu_hop;
    END IF;
    
    -- 6. Kiểm tra khách hàng đã đăng ký dịch vụ này chưa
    SELECT COUNT(*) INTO v_count_existing
    FROM CT_DKDV ct
    JOIN HOADON hd ON ct.MaHD = hd.MaHD
    WHERE hd.MaKH = p_ma_kh AND ct.MaDV = p_ma_dv;
    
    IF v_count_existing > 0 THEN
        RAISE ex_dich_vu_da_dang_ky;
    END IF;
    
    -- 7. Tạo mã hóa đơn mới
    SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaHD, 3))), 0) + 1 
    INTO v_max_hd_number
    FROM HOADON 
    WHERE MaHD LIKE 'HD%';
    
    v_ma_hd := 'HD' || LPAD(v_max_hd_number, 3, '0');
    
    -- 8. Tạo hóa đơn
    INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
    VALUES (v_ma_hd, p_ma_kh, SYSDATE, 'ChuaThanhToan', v_don_gia);
    
    -- 9. Tạo mã chi tiết đăng ký
    SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaCTDK, 3))), 0) + 1
    INTO v_max_ctdk_number
    FROM CT_DKDV 
    WHERE MaCTDK LIKE 'CT%';
    
    v_ma_ctdk := 'CT' || LPAD(v_max_ctdk_number, 3, '0');
    
    -- 10. Thêm chi tiết đăng ký dịch vụ PT với trainer
    INSERT INTO CT_DKDV (
        MaCTDK, MaHD, MaDV, NgayBD, NgayKT, MaLop, MaNV
    ) VALUES (
        v_ma_ctdk,
        v_ma_hd,
        p_ma_dv,
        SYSDATE,
        SYSDATE + v_thoi_han,
        NULL, -- PT không cần lớp
        p_ma_trainer -- Gán trainer được chỉ định
    );
    
    -- 11. Commit transaction
    COMMIT;
    
    -- 12. Trả về kết quả
    p_ma_hd := v_ma_hd;
    p_ma_ctdk := v_ma_ctdk;
    p_result := 'SUCCESS';
    p_error_msg := NULL;
    
EXCEPTION
    WHEN ex_khach_hang_not_found THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy khách hàng: ' || p_ma_kh;
        
    WHEN ex_dich_vu_not_found THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy dịch vụ: ' || p_ma_dv;
        
    WHEN ex_trainer_not_found THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy trainer: ' || p_ma_trainer;
        
    WHEN ex_khong_phai_pt THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Dịch vụ không phải loại PT: ' || p_ma_dv;
        
    WHEN ex_trainer_khong_phu_hop THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Trainer không có chuyên môn phù hợp với dịch vụ này';
        
    WHEN ex_dich_vu_da_dang_ky THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Khách hàng đã đăng ký dịch vụ này rồi';
        
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
END;
/

-- =====================================================================================
-- PROCEDURE CẬP NHẬT TRAINER CHO CHI TIẾT ĐÃ TỒN TẠI (Để xử lý sau VNPay)
-- =====================================================================================

CREATE OR REPLACE PROCEDURE proc_cap_nhat_trainer_cho_ctdk(
    p_ma_ctdk IN VARCHAR2,
    p_ma_trainer IN VARCHAR2,
    p_result OUT VARCHAR2,
    p_error_msg OUT VARCHAR2
)
AS
    v_count_ctdk NUMBER;
    v_count_trainer NUMBER;
    v_ma_dv VARCHAR2(10);
    v_ma_bm VARCHAR2(10);
    v_loai_dv VARCHAR2(20);
    
    ex_ctdk_not_found EXCEPTION;
    ex_trainer_not_found EXCEPTION;
    ex_trainer_khong_phu_hop EXCEPTION;
    ex_khong_phai_pt EXCEPTION;
    
BEGIN
    -- 1. Kiểm tra chi tiết đăng ký tồn tại và lấy thông tin dịch vụ
    SELECT COUNT(*), MAX(ct.MaDV), MAX(dv.MaBM), MAX(dv.LoaiDV)
    INTO v_count_ctdk, v_ma_dv, v_ma_bm, v_loai_dv
    FROM CT_DKDV ct
    JOIN DICHVU dv ON ct.MaDV = dv.MaDV
    WHERE ct.MaCTDK = p_ma_ctdk;
    
    IF v_count_ctdk = 0 THEN
        RAISE ex_ctdk_not_found;
    END IF;
    
    -- 2. Kiểm tra xem có phải dịch vụ PT không
    IF v_loai_dv != 'PT' THEN
        RAISE ex_khong_phai_pt;
    END IF;
    
    -- 3. Kiểm tra trainer tồn tại
    SELECT COUNT(*) INTO v_count_trainer
    FROM NHANVIEN 
    WHERE MaNV = p_ma_trainer AND LoaiNV = 'Trainer';
    
    IF v_count_trainer = 0 THEN
        RAISE ex_trainer_not_found;
    END IF;
    
    -- 4. Kiểm tra trainer có chuyên môn phù hợp không
    SELECT COUNT(*) INTO v_count_trainer
    FROM CT_CHUYENMON cm
    WHERE cm.MaNV = p_ma_trainer AND cm.MaBM = v_ma_bm;
    
    IF v_count_trainer = 0 THEN
        RAISE ex_trainer_khong_phu_hop;
    END IF;
    
    -- 5. Cập nhật trainer cho chi tiết đăng ký
    UPDATE CT_DKDV
    SET MaNV = p_ma_trainer
    WHERE MaCTDK = p_ma_ctdk;
    
    COMMIT;
    
    p_result := 'SUCCESS';
    p_error_msg := NULL;
    
EXCEPTION
    WHEN ex_ctdk_not_found THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy chi tiết đăng ký: ' || p_ma_ctdk;
        
    WHEN ex_trainer_not_found THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy trainer: ' || p_ma_trainer;
        
    WHEN ex_khong_phai_pt THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Chi tiết đăng ký không phải dịch vụ PT';
        
    WHEN ex_trainer_khong_phu_hop THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Trainer không có chuyên môn phù hợp với dịch vụ này';
        
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
END;
/

-- =====================================================================================
-- PROCEDURE ĐĂNG KÝ DỊCH VỤ TỔNG HỢP VỚI TRAINER/CLASS ASSIGNMENT
-- Xử lý đăng ký hỗn hợp PT và Lop trong cùng 1 hóa đơn
-- =====================================================================================

CREATE OR REPLACE PROCEDURE proc_dang_ky_dich_vu_voi_assignment(
    p_ma_kh IN VARCHAR2,
    p_list_ma_dv IN VARCHAR2, -- Danh sách mã dịch vụ cách nhau bởi dấu phẩy
    p_list_trainer_id IN VARCHAR2, -- Danh sách trainerId tương ứng (NULL nếu không có)
    p_list_class_id IN VARCHAR2, -- Danh sách classId tương ứng (NULL nếu không có)
    p_ma_hd OUT VARCHAR2,
    p_tong_tien OUT NUMBER,
    p_result OUT VARCHAR2,
    p_error_msg OUT VARCHAR2
)
AS
    v_ma_hd VARCHAR2(10);
    v_tong_tien NUMBER := 0;
    v_ma_ctdk VARCHAR2(10);
    v_ma_dv VARCHAR2(10);
    v_trainer_id VARCHAR2(10);
    v_class_id VARCHAR2(10);
    v_don_gia NUMBER;
    v_thoi_han NUMBER;
    v_loai_dv VARCHAR2(20);
    v_ma_bm VARCHAR2(10);
    v_ma_lop VARCHAR2(10);
    v_ma_nv VARCHAR2(10);
    
    -- Variables cho parsing
    v_pos NUMBER := 1;
    v_next_pos NUMBER;
    v_temp_dv_list VARCHAR2(4000) := p_list_ma_dv || ',';
    v_temp_trainer_list VARCHAR2(4000) := NVL(p_list_trainer_id, '') || ',';
    v_temp_class_list VARCHAR2(4000) := NVL(p_list_class_id, '') || ',';
    
    -- Index cho từng service
    v_service_index NUMBER := 0;
    
    -- Số thứ tự cho mã
    v_max_hd_number NUMBER;
    v_current_ctdk_number NUMBER;
    
    -- Kiểm tra tồn tại
    v_count NUMBER;
    
    -- Variables cho retry logic (xử lý concurrency)
    v_retry_count NUMBER := 0;
    v_max_retries NUMBER := 3;
    
    -- Exception tự định nghĩa
    ex_khach_hang_not_found EXCEPTION;
    ex_dich_vu_not_found EXCEPTION;
    ex_trainer_not_found EXCEPTION;
    ex_class_not_found EXCEPTION;
    ex_dich_vu_da_dang_ky EXCEPTION;
    ex_trainer_khong_phu_hop EXCEPTION;
    ex_class_khong_phu_hop EXCEPTION;
    ex_duplicate_key EXCEPTION;
    
    -- Exception cho Oracle system errors
    PRAGMA EXCEPTION_INIT(ex_duplicate_key, -00001); -- ORA-00001: unique constraint violated
    
BEGIN
    -- Loop để retry trong trường hợp có conflict
    WHILE v_retry_count <= v_max_retries LOOP
        BEGIN
            -- Savepoint trước khi thực hiện insert
            SAVEPOINT before_processing;
            
            -- 1. Kiểm tra khách hàng tồn tại
            SELECT COUNT(*) INTO v_count
            FROM KHACHHANG 
            WHERE MaKH = p_ma_kh;
            
            IF v_count = 0 THEN
                RAISE ex_khach_hang_not_found;
            END IF;
            
            -- 2. Tạo mã hóa đơn unique
            SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaHD, 3))), 0) + 1 
            INTO v_max_hd_number
            FROM HOADON 
            WHERE MaHD LIKE 'HD%';
            
            v_ma_hd := 'HD' || LPAD(v_max_hd_number, 3, '0');
            
            -- Kiểm tra unique và tạo lại nếu trùng
            LOOP
                SELECT COUNT(*) INTO v_count FROM HOADON WHERE MaHD = v_ma_hd;
                EXIT WHEN v_count = 0;
                v_max_hd_number := v_max_hd_number + 1;
                v_ma_hd := 'HD' || LPAD(v_max_hd_number, 3, '0');
            END LOOP;
            
            -- 3. Tạo hóa đơn
            INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
            VALUES (v_ma_hd, p_ma_kh, SYSDATE, 'ChuaThanhToan', 0);
            
            -- 4. Reset variables cho processing
            v_pos := 1;
            v_service_index := 0;
            v_tong_tien := 0;
            
            -- 5. Xử lý từng dịch vụ trong danh sách
            WHILE v_pos <= LENGTH(v_temp_dv_list) LOOP
                -- Tìm vị trí dấu phẩy tiếp theo
                v_next_pos := INSTR(v_temp_dv_list, ',', v_pos);
                
                IF v_next_pos > 0 THEN
                    -- Lấy mã dịch vụ
                    v_ma_dv := TRIM(SUBSTR(v_temp_dv_list, v_pos, v_next_pos - v_pos));
                    
                    IF LENGTH(v_ma_dv) > 0 THEN
                        -- Lấy trainerId và classId tương ứng (parse manually)
                        DECLARE
                            v_trainer_start NUMBER := 1;
                            v_trainer_end NUMBER;
                            v_class_start NUMBER := 1;
                            v_class_end NUMBER;
                            v_current_idx NUMBER := 0;
                        BEGIN
                            -- Parse trainerId
                            WHILE v_current_idx <= v_service_index AND v_trainer_start <= LENGTH(v_temp_trainer_list) LOOP
                                v_trainer_end := INSTR(v_temp_trainer_list, ',', v_trainer_start);
                                IF v_trainer_end = 0 THEN
                                    v_trainer_end := LENGTH(v_temp_trainer_list) + 1;
                                END IF;
                                
                                IF v_current_idx = v_service_index THEN
                                    v_trainer_id := TRIM(SUBSTR(v_temp_trainer_list, v_trainer_start, v_trainer_end - v_trainer_start));
                                    IF LENGTH(v_trainer_id) = 0 OR v_trainer_id = 'null' THEN
                                        v_trainer_id := NULL;
                                    END IF;
                                    EXIT;
                                END IF;
                                
                                v_current_idx := v_current_idx + 1;
                                v_trainer_start := v_trainer_end + 1;
                            END LOOP;
                            
                            -- Parse classId
                            v_current_idx := 0;
                            WHILE v_current_idx <= v_service_index AND v_class_start <= LENGTH(v_temp_class_list) LOOP
                                v_class_end := INSTR(v_temp_class_list, ',', v_class_start);
                                IF v_class_end = 0 THEN
                                    v_class_end := LENGTH(v_temp_class_list) + 1;
                                END IF;
                                
                                IF v_current_idx = v_service_index THEN
                                    v_class_id := TRIM(SUBSTR(v_temp_class_list, v_class_start, v_class_end - v_class_start));
                                    IF LENGTH(v_class_id) = 0 OR v_class_id = 'null' THEN
                                        v_class_id := NULL;
                                    END IF;
                                    EXIT;
                                END IF;
                                
                                v_current_idx := v_current_idx + 1;
                                v_class_start := v_class_end + 1;
                            END LOOP;
                        END;
                        
                        -- Kiểm tra dịch vụ tồn tại và lấy thông tin
                        SELECT COUNT(*), MAX(DonGia), MAX(ThoiHan), MAX(LoaiDV), MAX(MaBM)
                        INTO v_count, v_don_gia, v_thoi_han, v_loai_dv, v_ma_bm
                        FROM DICHVU 
                        WHERE MaDV = v_ma_dv;
                        
                        IF v_count = 0 THEN
                            p_error_msg := 'Không tìm thấy dịch vụ: ' || v_ma_dv;
                            RAISE ex_dich_vu_not_found;
                        END IF;
                        
                        -- Kiểm tra khách hàng đã đăng ký dịch vụ này chưa
                        SELECT COUNT(*) INTO v_count
                        FROM CT_DKDV ct
                        JOIN HOADON hd ON ct.MaHD = hd.MaHD
                        WHERE hd.MaKH = p_ma_kh AND ct.MaDV = v_ma_dv;
                        
                        IF v_count > 0 THEN
                            p_error_msg := 'Khách hàng đã đăng ký dịch vụ: ' || v_ma_dv;
                            RAISE ex_dich_vu_da_dang_ky;
                        END IF;
                        
                        -- Tạo mã chi tiết đăng ký unique
                        SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaCTDK, 3))), 0) + 1
                        INTO v_current_ctdk_number
                        FROM CT_DKDV 
                        WHERE MaCTDK LIKE 'CT%';
                        
                        v_ma_ctdk := 'CT' || LPAD(v_current_ctdk_number, 3, '0');
                        
                        -- Kiểm tra unique và tạo lại nếu trùng
                        LOOP
                            SELECT COUNT(*) INTO v_count FROM CT_DKDV WHERE MaCTDK = v_ma_ctdk;
                            EXIT WHEN v_count = 0;
                            v_current_ctdk_number := v_current_ctdk_number + 1;
                            v_ma_ctdk := 'CT' || LPAD(v_current_ctdk_number, 3, '0');
                        END LOOP;
                        
                        -- Xử lý assignment theo loại dịch vụ
                        v_ma_lop := NULL;
                        v_ma_nv := NULL;
                        
                        -- =================================================================
                        -- LOGIC XỬ LÝ THEO LOẠI DỊCH VỤ
                        -- =================================================================
                        
                        IF v_loai_dv = 'PT' THEN
                            -- ============= DỊCH VỤ PT =============
                            IF v_trainer_id IS NOT NULL THEN
                                -- Sử dụng trainer được chỉ định
                                SELECT COUNT(*) INTO v_count
                                FROM NHANVIEN 
                                WHERE MaNV = v_trainer_id AND LoaiNV = 'Trainer';
                                
                                IF v_count = 0 THEN
                                    p_error_msg := 'Không tìm thấy trainer: ' || v_trainer_id;
                                    RAISE ex_trainer_not_found;
                                END IF;
                                
                                -- Kiểm tra trainer có chuyên môn phù hợp
                                SELECT COUNT(*) INTO v_count
                                FROM CT_CHUYENMON cm
                                WHERE cm.MaNV = v_trainer_id AND cm.MaBM = v_ma_bm;
                                
                                IF v_count = 0 THEN
                                    p_error_msg := 'Trainer ' || v_trainer_id || ' không có chuyên môn phù hợp với ' || v_ma_bm;
                                    RAISE ex_trainer_khong_phu_hop;
                                END IF;
                                
                                v_ma_nv := v_trainer_id;
                                
                            ELSE
                                -- Auto-assign trainer có chuyên môn phù hợp
                                BEGIN
                                    SELECT MaNV INTO v_ma_nv
                                    FROM (
                                        SELECT nv.MaNV
                                        FROM NHANVIEN nv
                                        JOIN CT_CHUYENMON cm ON nv.MaNV = cm.MaNV
                                        WHERE nv.LoaiNV = 'Trainer' AND cm.MaBM = v_ma_bm
                                        ORDER BY nv.MaNV
                                    ) WHERE ROWNUM = 1;
                                EXCEPTION
                                    WHEN NO_DATA_FOUND THEN
                                        p_error_msg := 'Không tìm thấy trainer phù hợp cho bộ môn: ' || v_ma_bm;
                                        RAISE ex_trainer_not_found;
                                END;
                            END IF;
                            
                        ELSIF v_loai_dv = 'Lop' THEN
                            -- ============= DỊCH VỤ LỚP =============
                            IF v_class_id IS NOT NULL THEN
                                -- Sử dụng lớp được chỉ định
                                SELECT COUNT(*), MAX(MaNV) INTO v_count, v_ma_nv
                                FROM LOP 
                                WHERE MaLop = v_class_id AND MaBM = v_ma_bm AND TinhTrang = 'ChuaDay';
                                
                                IF v_count = 0 THEN
                                    -- Kiểm tra xem lớp có tồn tại không
                                    SELECT COUNT(*) INTO v_count FROM LOP WHERE MaLop = v_class_id;
                                    IF v_count = 0 THEN
                                        p_error_msg := 'Không tìm thấy lớp: ' || v_class_id;
                                    ELSE
                                        p_error_msg := 'Lớp ' || v_class_id || ' không thuộc bộ môn ' || v_ma_bm || ' hoặc đã đầy';
                                    END IF;
                                    RAISE ex_class_not_found;
                                END IF;
                                
                                v_ma_lop := v_class_id;
                                
                            ELSE
                                -- Auto-assign lớp chưa đầy
                                BEGIN
                                    SELECT MaLop, MaNV INTO v_ma_lop, v_ma_nv
                                    FROM (
                                        SELECT MaLop, MaNV
                                        FROM LOP 
                                        WHERE MaBM = v_ma_bm AND TinhTrang = 'ChuaDay'
                                        ORDER BY MaLop
                                    ) WHERE ROWNUM = 1;
                                EXCEPTION
                                    WHEN NO_DATA_FOUND THEN
                                        p_error_msg := 'Không còn lớp trống cho bộ môn: ' || v_ma_bm;
                                        RAISE ex_class_not_found;
                                END;
                            END IF;
                            
                        ELSIF v_loai_dv = 'TuDo' THEN
                            -- ============= DỊCH VỤ TỰ DO =============
                            -- Không cần assignment, giữ nguyên v_ma_lop = NULL, v_ma_nv = NULL
                            NULL;
                            
                        ELSE
                            p_error_msg := 'Loại dịch vụ không hợp lệ: ' || v_loai_dv;
                            RAISE ex_dich_vu_not_found;
                        END IF;
                        
                        -- =================================================================
                        -- THÊM CHI TIẾT ĐĂNG KÝ DỊCH VỤ
                        -- =================================================================
                        
                        INSERT INTO CT_DKDV (
                            MaCTDK, MaHD, MaDV, NgayBD, NgayKT, MaLop, MaNV
                        ) VALUES (
                            v_ma_ctdk,
                            v_ma_hd,
                            v_ma_dv,
                            SYSDATE,
                            SYSDATE + v_thoi_han,
                            v_ma_lop,
                            v_ma_nv
                        );
                        
                        -- Cộng dồn tổng tiền
                        v_tong_tien := v_tong_tien + v_don_gia;
                        
                        -- Tăng service index
                        v_service_index := v_service_index + 1;
                        
                        -- Log thành công
                        DBMS_OUTPUT.PUT_LINE('✅ Đã thêm: ' || v_ma_dv || ' (' || v_loai_dv || ') - Trainer: ' || 
                                           NVL(v_ma_nv, 'NULL') || ' - Lớp: ' || NVL(v_ma_lop, 'NULL'));
                    END IF;
                    
                    -- Chuyển đến vị trí tiếp theo
                    v_pos := v_next_pos + 1;
                ELSE
                    EXIT; -- Hết danh sách
                END IF;
            END LOOP;
            
            -- 6. Cập nhật tổng tiền hóa đơn
            UPDATE HOADON 
            SET TongTien = v_tong_tien 
            WHERE MaHD = v_ma_hd;
            
            -- 7. Commit transaction nếu thành công
            COMMIT;
            
            -- 8. Trả về kết quả
            p_ma_hd := v_ma_hd;
            p_tong_tien := v_tong_tien;
            p_result := 'SUCCESS';
            p_error_msg := NULL;
            
            -- Thoát khỏi retry loop nếu thành công
            EXIT;
            
        EXCEPTION
            WHEN ex_duplicate_key THEN
                -- Rollback và retry
                ROLLBACK TO before_processing;
                v_retry_count := v_retry_count + 1;
                
                IF v_retry_count > v_max_retries THEN
                    p_result := 'ERROR';
                    p_error_msg := 'Hệ thống đang bận, vui lòng thử lại sau (Mã trùng lặp)';
                    ROLLBACK;
                    RETURN;
                END IF;
                
                -- Đợi một chút trước khi retry
                DBMS_LOCK.SLEEP(0.1);
                
            WHEN ex_khach_hang_not_found THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                p_error_msg := 'Không tìm thấy khách hàng: ' || p_ma_kh;
                ROLLBACK;
                RETURN;
                
            WHEN ex_dich_vu_not_found THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN ex_trainer_not_found THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN ex_class_not_found THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN ex_dich_vu_da_dang_ky THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN ex_trainer_khong_phu_hop THEN
                ROLLBACK TO before_processing;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN OTHERS THEN
                -- Kiểm tra nếu là lỗi concurrent access
                IF SQLCODE = -54 OR SQLCODE = -30006 OR SQLCODE = -1 THEN -- Resource busy hoặc unique constraint
                    ROLLBACK TO before_processing;
                    v_retry_count := v_retry_count + 1;
                    
                    IF v_retry_count > v_max_retries THEN
                        p_result := 'ERROR';
                        p_error_msg := 'Hệ thống đang bận, vui lòng thử lại sau (Lỗi: ' || SQLCODE || ')';
                        ROLLBACK;
                        RETURN;
                    END IF;
                    
                    -- Đợi một chút trước khi retry
                    DBMS_LOCK.SLEEP(0.2);
                ELSE
                    ROLLBACK TO before_processing;
                    p_result := 'ERROR';
                    p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
                    ROLLBACK;
                    RETURN;
                END IF;
        END;
    END LOOP;
    
    -- Nếu vẫn không thành công sau max retries
    IF v_retry_count > v_max_retries THEN
        p_result := 'ERROR';
        p_error_msg := 'Không thể hoàn thành đăng ký sau ' || v_max_retries || ' lần thử';
        ROLLBACK;
    END IF;
    
END;
/ 