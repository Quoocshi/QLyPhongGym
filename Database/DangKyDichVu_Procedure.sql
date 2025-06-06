﻿-- =====================================================================================
-- PROCEDURE ĐĂNG KÝ DỊCH VỤ TỔNG HỢP
-- Thay thế toàn bộ logic trong Java Service Layer
-- Cải thiện với savepoint để xử lý race condition
-- =====================================================================================

-- 1. Procedure chính để đăng ký dịch vụ (Lost-update)
CREATE OR REPLACE PROCEDURE proc_dang_ky_dich_vu_tong_hop(
    p_ma_kh IN VARCHAR2,
    p_list_ma_dv IN VARCHAR2, -- Danh sách mã dịch vụ cách nhau bởi dấu phẩy
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
    v_don_gia NUMBER;
    v_thoi_han NUMBER;
    v_loai_dv VARCHAR2(20);
    v_ma_bm VARCHAR2(10);
    v_ma_lop VARCHAR2(10);
    v_ma_nv VARCHAR2(10);
    
    -- Variables cho cursor
    v_pos NUMBER := 1;
    v_next_pos NUMBER;
    v_temp_list VARCHAR2(4000) := p_list_ma_dv || ','; -- Thêm dấu phẩy cuối
    
    -- Số thứ tự cho chi tiết đăng ký
    v_max_ctdk_number NUMBER;
    v_current_ctdk_number NUMBER;
    
    -- Kiểm tra dịch vụ tồn tại
    v_count_dv NUMBER;
    
    -- Exception tự định nghĩa
    ex_khach_hang_not_found EXCEPTION;
    ex_dich_vu_not_found EXCEPTION;
    ex_dich_vu_da_dang_ky EXCEPTION;
    
BEGIN
    -- 1. Kiểm tra khách hàng tồn tại
    SELECT COUNT(*) INTO v_count_dv
    FROM KHACHHANG 
    WHERE MaKH = p_ma_kh;
    
    IF v_count_dv = 0 THEN
        RAISE ex_khach_hang_not_found;
    END IF;
    
    -- 2. Tạo mã hóa đơn mới sử dụng function đã có
    v_max_ctdk_number := FindMaxMaHoaDonNumber();
    v_max_ctdk_number := NVL(v_max_ctdk_number, 0) + 1;
    v_ma_hd := 'HD' || LPAD(v_max_ctdk_number, 3, '0');

    DBMS_LOCK.SLEEP(5);

    -- 3. Tạo hóa đơn
    INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
    VALUES (v_ma_hd, p_ma_kh, SYSDATE, 'ChuaThanhToan', 0);
    
    -- 4. Lấy số thứ tự chi tiết đăng ký tiếp theo
    v_max_ctdk_number := get_max_ctdk_number();
    v_current_ctdk_number := NVL(v_max_ctdk_number, 0) + 1;
    
    -- 5. Xử lý từng dịch vụ trong danh sách
    WHILE v_pos <= LENGTH(v_temp_list) LOOP
        -- Tìm vị trí dấu phẩy tiếp theo
        v_next_pos := INSTR(v_temp_list, ',', v_pos);
        
        IF v_next_pos > 0 THEN
            -- Lấy mã dịch vụ
            v_ma_dv := TRIM(SUBSTR(v_temp_list, v_pos, v_next_pos - v_pos));
            
            IF LENGTH(v_ma_dv) > 0 THEN
                -- Kiểm tra dịch vụ tồn tại
                SELECT COUNT(*) INTO v_count_dv
                FROM DICHVU 
                WHERE MaDV = v_ma_dv;
                
                IF v_count_dv = 0 THEN
                    p_error_msg := 'Dịch vụ không tồn tại: ' || v_ma_dv;
                    RAISE ex_dich_vu_not_found;
                END IF;
                
                -- Kiểm tra khách hàng đã đăng ký dịch vụ này chưa
                SELECT COUNT(*) INTO v_count_dv
                FROM CT_DKDV ct
                JOIN HOADON hd ON ct.MaHD = hd.MaHD
                WHERE hd.MaKH = p_ma_kh AND ct.MaDV = v_ma_dv;
                
                IF v_count_dv > 0 THEN
                    p_error_msg := 'Khách hàng đã đăng ký dịch vụ: ' || v_ma_dv;
                    RAISE ex_dich_vu_da_dang_ky;
                END IF;
                
                -- Lấy thông tin dịch vụ
                SELECT DonGia, ThoiHan, LoaiDV, MaBM
                INTO v_don_gia, v_thoi_han, v_loai_dv, v_ma_bm
                FROM DICHVU 
                WHERE MaDV = v_ma_dv;
                
                -- Tạo mã chi tiết đăng ký
                v_ma_ctdk := 'CT' || LPAD(v_current_ctdk_number, 3, '0');
                
                -- Xử lý phân lớp (nếu không phải dịch vụ tự do)
                v_ma_lop := NULL;
                v_ma_nv := NULL;
                
                IF v_loai_dv != 'TuDo' THEN
                    -- Tìm lớp chưa đầy theo bộ môn
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
                            v_ma_lop := NULL;
                            v_ma_nv := NULL;
                    END;
                END IF;
                
                -- Thêm chi tiết đăng ký dịch vụ
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
                
                -- Tăng số thứ tự cho lần tiếp theo
                v_current_ctdk_number := v_current_ctdk_number + 1;
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
    
    -- 7. Commit transaction
    COMMIT;
    
    -- 8. Trả về kết quả
    p_ma_hd := v_ma_hd;
    p_tong_tien := v_tong_tien;
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
        -- p_error_msg đã được set ở trên
        
    WHEN ex_dich_vu_da_dang_ky THEN
        ROLLBACK;
        p_result := 'ERROR';
        -- p_error_msg đã được set ở trên
        
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
END;
/

-- 1. Procedure chính để đăng ký dịch vụ
CREATE OR REPLACE PROCEDURE proc_dang_ky_dich_vu_tong_hop(
    p_ma_kh IN VARCHAR2,
    p_list_ma_dv IN VARCHAR2, -- Danh sách mã dịch vụ cách nhau bởi dấu phẩy
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
    v_don_gia NUMBER;
    v_thoi_han NUMBER;
    v_loai_dv VARCHAR2(20);
    v_ma_bm VARCHAR2(10);
    v_ma_lop VARCHAR2(10);
    v_ma_nv VARCHAR2(10);
    
    -- Variables cho cursor
    v_pos NUMBER := 1;
    v_next_pos NUMBER;
    v_temp_list VARCHAR2(4000) := p_list_ma_dv || ','; -- Thêm dấu phẩy cuối
    
    -- Số thứ tự cho chi tiết đăng ký
    v_max_ctdk_number NUMBER;
    v_current_ctdk_number NUMBER;
    
    -- Kiểm tra dịch vụ tồn tại
    v_count_dv NUMBER;
    
    -- Variables cho retry logic
    v_retry_count NUMBER := 0;
    v_max_retries NUMBER := 3;
    
    -- Exception tự định nghĩa
    ex_khach_hang_not_found EXCEPTION;
    ex_dich_vu_not_found EXCEPTION;
    ex_dich_vu_da_dang_ky EXCEPTION;
    ex_ma_hd_duplicate EXCEPTION;
    ex_ma_ctdk_duplicate EXCEPTION;
    
    -- Exception cho Oracle system errors
    PRAGMA EXCEPTION_INIT(ex_ma_hd_duplicate, -00001); -- ORA-00001: unique constraint violated
    PRAGMA EXCEPTION_INIT(ex_ma_ctdk_duplicate, -00001); -- ORA-00001: unique constraint violated
    
BEGIN
    -- Loop để retry trong trường hợp có conflict
    WHILE v_retry_count <= v_max_retries LOOP
        BEGIN
            -- Savepoint trước khi thực hiện insert
            SAVEPOINT before_insert;
            
            -- 1. Kiểm tra khách hàng tồn tại
            SELECT COUNT(*) INTO v_count_dv
            FROM KHACHHANG 
            WHERE MaKH = p_ma_kh;
            
            IF v_count_dv = 0 THEN
                RAISE ex_khach_hang_not_found;
            END IF;
            
            -- 2. Tạo mã hóa đơn mới với sequence-like approach
            DECLARE
                v_temp_count NUMBER;
                v_lock_acquired BOOLEAN := FALSE;
            BEGIN
                -- Thử lock bảng HOADON để tránh concurrent access
                DECLARE
                    CURSOR c_lock IS
                        SELECT 1 FROM HOADON WHERE ROWNUM = 1 FOR UPDATE NOWAIT;
                    v_dummy NUMBER;
                BEGIN
                    OPEN c_lock;
                    FETCH c_lock INTO v_dummy;
                    CLOSE c_lock;
                    v_lock_acquired := TRUE;
                EXCEPTION
                    WHEN OTHERS THEN
                        v_lock_acquired := FALSE;
                        IF c_lock%ISOPEN THEN
                            CLOSE c_lock;
                        END IF;
                END;
                
                -- Lấy mã hóa đơn tiếp theo
                SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaHD, 3))), 0) + 1 
                INTO v_max_ctdk_number
                FROM HOADON 
                WHERE MaHD LIKE 'HD%';
                
                v_ma_hd := 'HD' || LPAD(v_max_ctdk_number, 3, '0');
                
                -- Kiểm tra xem mã đã tồn tại chưa và tạo mã unique
                LOOP
                    SELECT COUNT(*) INTO v_temp_count
                    FROM HOADON 
                    WHERE MaHD = v_ma_hd;
                    
                    EXIT WHEN v_temp_count = 0;
                    
                    -- Nếu mã đã tồn tại, tạo mã mới
                    v_max_ctdk_number := v_max_ctdk_number + 1;
                    v_ma_hd := 'HD' || LPAD(v_max_ctdk_number, 3, '0');
                END LOOP;
            END;
            
            DBMS_LOCK.SLEEP(5);

            -- 3. Tạo hóa đơn
            INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
            VALUES (v_ma_hd, p_ma_kh, SYSDATE, 'ChuaThanhToan', 0);
            
            -- 4. Lấy số thứ tự chi tiết đăng ký tiếp theo
            SELECT NVL(MAX(TO_NUMBER(SUBSTR(MaCTDK, 3))), 0) + 1
            INTO v_current_ctdk_number
            FROM CT_DKDV 
            WHERE MaCTDK LIKE 'CT%';
            
            -- 5. Xử lý từng dịch vụ trong danh sách
            WHILE v_pos <= LENGTH(v_temp_list) LOOP
                -- Tìm vị trí dấu phẩy tiếp theo
                v_next_pos := INSTR(v_temp_list, ',', v_pos);
                
                IF v_next_pos > 0 THEN
                    -- Lấy mã dịch vụ
                    v_ma_dv := TRIM(SUBSTR(v_temp_list, v_pos, v_next_pos - v_pos));
                    
                    IF LENGTH(v_ma_dv) > 0 THEN
                        -- Savepoint cho từng dịch vụ
                        SAVEPOINT before_service_insert;
                        
                        -- Kiểm tra dịch vụ tồn tại
                        SELECT COUNT(*) INTO v_count_dv
                        FROM DICHVU 
                        WHERE MaDV = v_ma_dv;
                        
                        IF v_count_dv = 0 THEN
                            p_error_msg := 'Dịch vụ không tồn tại: ' || v_ma_dv;
                            RAISE ex_dich_vu_not_found;
                        END IF;
                        
                        -- Kiểm tra khách hàng đã đăng ký dịch vụ này chưa
                        SELECT COUNT(*) INTO v_count_dv
                        FROM CT_DKDV ct
                        JOIN HOADON hd ON ct.MaHD = hd.MaHD
                        WHERE hd.MaKH = p_ma_kh AND ct.MaDV = v_ma_dv;
                        
                        IF v_count_dv > 0 THEN
                            p_error_msg := 'Khách hàng đã đăng ký dịch vụ: ' || v_ma_dv;
                            RAISE ex_dich_vu_da_dang_ky;
                        END IF;
                        
                        -- Lấy thông tin dịch vụ
                        SELECT DonGia, ThoiHan, LoaiDV, MaBM
                        INTO v_don_gia, v_thoi_han, v_loai_dv, v_ma_bm
                        FROM DICHVU 
                        WHERE MaDV = v_ma_dv;
                        
                        -- Tạo mã chi tiết đăng ký unique
                        DECLARE
                            v_temp_count_ct NUMBER;
                        BEGIN
                            LOOP
                                v_ma_ctdk := 'CT' || LPAD(v_current_ctdk_number, 3, '0');
                                
                                SELECT COUNT(*) INTO v_temp_count_ct
                                FROM CT_DKDV 
                                WHERE MaCTDK = v_ma_ctdk;
                                
                                EXIT WHEN v_temp_count_ct = 0;
                                
                                v_current_ctdk_number := v_current_ctdk_number + 1;
                            END LOOP;
                        END;
                        
                        -- Xử lý phân lớp (nếu không phải dịch vụ tự do)
                        v_ma_lop := NULL;
                        v_ma_nv := NULL;
                        
                        IF v_loai_dv != 'TuDo' THEN
                            -- Tìm lớp chưa đầy theo bộ môn
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
                                    v_ma_lop := NULL;
                                    v_ma_nv := NULL;
                            END;
                        END IF;
                        
                        -- Thêm chi tiết đăng ký dịch vụ
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
                        
                        -- Tăng số thứ tự cho lần tiếp theo
                        v_current_ctdk_number := v_current_ctdk_number + 1;
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
            WHEN ex_ma_hd_duplicate OR ex_ma_ctdk_duplicate THEN
                -- Rollback về savepoint và retry
                ROLLBACK TO before_insert;
                v_retry_count := v_retry_count + 1;
                
                IF v_retry_count > v_max_retries THEN
                    p_result := 'ERROR';
                    p_error_msg := 'Hệ thống đang bận, vui lòng thử lại sau (Mã trùng lặp)';
                    ROLLBACK;
                    RETURN;
                END IF;
                
                -- Đợi một chút trước khi retry
                DBMS_LOCK.SLEEP(0.1);
                
                -- Reset variables cho retry
                v_pos := 1;
                v_tong_tien := 0;
                
            WHEN ex_khach_hang_not_found THEN
                ROLLBACK TO before_insert;
                p_result := 'ERROR';
                p_error_msg := 'Không tìm thấy khách hàng: ' || p_ma_kh;
                ROLLBACK;
                RETURN;
                
            WHEN ex_dich_vu_not_found THEN
                ROLLBACK TO before_insert;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN ex_dich_vu_da_dang_ky THEN
                ROLLBACK TO before_insert;
                p_result := 'ERROR';
                -- p_error_msg đã được set ở trên
                ROLLBACK;
                RETURN;
                
            WHEN OTHERS THEN
                -- Kiểm tra nếu là lỗi concurrent access
                IF SQLCODE = -54 OR SQLCODE = -30006 OR SQLCODE = -1 THEN -- Resource busy hoặc unique constraint
                    ROLLBACK TO before_insert;
                    v_retry_count := v_retry_count + 1;
                    
                    IF v_retry_count > v_max_retries THEN
                        p_result := 'ERROR';
                        p_error_msg := 'Hệ thống đang bận, vui lòng thử lại sau (Lỗi: ' || SQLCODE || ')';
                        ROLLBACK;
                        RETURN;
                    END IF;
                    
                    -- Đợi một chút trước khi retry
                    DBMS_LOCK.SLEEP(0.2);
                    
                    -- Reset variables cho retry
                    v_pos := 1;
                    v_tong_tien := 0;
                ELSE
                    ROLLBACK TO before_insert;
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