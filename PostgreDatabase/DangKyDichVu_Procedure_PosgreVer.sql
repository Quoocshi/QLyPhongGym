-- =====================================================================================
-- POSTGRESQL STORED PROCEDURES - GYM MANAGEMENT SYSTEM
-- Chuyển đổi từ Oracle PL/SQL sang PostgreSQL
-- =====================================================================================

-- =====================================================================================
-- PROCEDURE ĐĂNG KÝ DỊCH VỤ TỔNG HỢP HOÀN CHỈNH
-- Xử lý đăng ký hỗn hợp: TuDo + PT + Lop trong cùng 1 hóa đơn
-- =====================================================================================

CREATE OR REPLACE FUNCTION proc_dang_ky_dich_vu_universal(
    p_ma_kh VARCHAR(10),
    p_list_ma_dv TEXT,
    p_list_trainer_id TEXT DEFAULT NULL,
    p_list_class_id TEXT DEFAULT NULL,
    OUT p_ma_hd VARCHAR(10),
    OUT p_tong_tien NUMERIC,
    OUT p_result VARCHAR(20),
    OUT p_error_msg TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_ma_hd VARCHAR(10);
    v_tong_tien NUMERIC := 0;
    v_ma_ctdk VARCHAR(10);
    v_ma_dv VARCHAR(10);
    v_trainer_id VARCHAR(10);
    v_class_id VARCHAR(10);
    v_don_gia NUMERIC;
    v_thoi_han INTEGER;
    v_loai_dv VARCHAR(20);
    v_ma_bm VARCHAR(10);
    v_ma_lop VARCHAR(10);
    v_ma_nv VARCHAR(10);
    v_dv_array TEXT[];
    v_trainer_array TEXT[];
    v_class_array TEXT[];
    v_service_index INTEGER := 1;
    v_max_hd_number INTEGER;
    v_current_ctdk_number INTEGER;
    v_count INTEGER;
    v_retry_count INTEGER := 0;
    v_max_retries INTEGER := 3;
BEGIN
    WHILE v_retry_count <= v_max_retries LOOP
        BEGIN
            SELECT COUNT(*) INTO v_count FROM KHACHHANG WHERE MaKH = p_ma_kh;
            IF v_count = 0 THEN
                p_result := 'ERROR';
                p_error_msg := 'Không tìm thấy khách hàng: ' || p_ma_kh;
                RETURN;
            END IF;

            SELECT COALESCE(MAX(SUBSTRING(MaHD FROM 3)::INTEGER), 0) + 1
            INTO v_max_hd_number FROM HOADON WHERE MaHD LIKE 'HD%';
            v_ma_hd := 'HD' || LPAD(v_max_hd_number::TEXT, 3, '0');

            LOOP
                SELECT COUNT(*) INTO v_count FROM HOADON WHERE MaHD = v_ma_hd;
                EXIT WHEN v_count = 0;
                v_max_hd_number := v_max_hd_number + 1;
                v_ma_hd := 'HD' || LPAD(v_max_hd_number::TEXT, 3, '0');
            END LOOP;

            INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
            VALUES (v_ma_hd, p_ma_kh, CURRENT_TIMESTAMP, 'ChuaThanhToan', 0);

            v_dv_array := string_to_array(p_list_ma_dv, ',');
            v_trainer_array := string_to_array(COALESCE(p_list_trainer_id, ''), ',');
            v_class_array := string_to_array(COALESCE(p_list_class_id, ''), ',');

            FOR v_service_index IN 1..array_length(v_dv_array, 1) LOOP
                v_ma_dv := TRIM(v_dv_array[v_service_index]);

                IF LENGTH(v_ma_dv) > 0 THEN
                    IF v_service_index <= array_length(v_trainer_array, 1) THEN
                        v_trainer_id := NULLIF(TRIM(v_trainer_array[v_service_index]), '');
                        IF v_trainer_id = 'null' THEN v_trainer_id := NULL; END IF;
                    ELSE
                        v_trainer_id := NULL;
                    END IF;

                    IF v_service_index <= array_length(v_class_array, 1) THEN
                        v_class_id := NULLIF(TRIM(v_class_array[v_service_index]), '');
                        IF v_class_id = 'null' THEN v_class_id := NULL; END IF;
                    ELSE
                        v_class_id := NULL;
                    END IF;

                    SELECT COUNT(*), MAX(DonGia), MAX(ThoiHan), MAX(LoaiDV), MAX(MaBM)
                    INTO v_count, v_don_gia, v_thoi_han, v_loai_dv, v_ma_bm
                    FROM DICHVU WHERE MaDV = v_ma_dv;

                    IF v_count = 0 THEN
                        ROLLBACK;
                        p_result := 'ERROR';
                        p_error_msg := 'Không tìm thấy dịch vụ: ' || v_ma_dv;
                        RETURN;
                    END IF;

                    SELECT COUNT(*) INTO v_count FROM CT_DKDV ct
                    JOIN HOADON hd ON ct.MaHD = hd.MaHD
                    WHERE hd.MaKH = p_ma_kh AND ct.MaDV = v_ma_dv AND hd.TrangThai = 'DaThanhToan';

                    IF v_count > 0 THEN
                        ROLLBACK;
                        p_result := 'ERROR';
                        p_error_msg := 'Khách hàng đã đăng ký dịch vụ: ' || v_ma_dv;
                        RETURN;
                    END IF;

                    SELECT COALESCE(MAX(SUBSTRING(MaCTDK FROM 3)::INTEGER), 0) + 1
                    INTO v_current_ctdk_number FROM CT_DKDV WHERE MaCTDK LIKE 'CT%';
                    v_ma_ctdk := 'CT' || LPAD(v_current_ctdk_number::TEXT, 3, '0');

                    LOOP
                        SELECT COUNT(*) INTO v_count FROM CT_DKDV WHERE MaCTDK = v_ma_ctdk;
                        EXIT WHEN v_count = 0;
                        v_current_ctdk_number := v_current_ctdk_number + 1;
                        v_ma_ctdk := 'CT' || LPAD(v_current_ctdk_number::TEXT, 3, '0');
                    END LOOP;

                    v_ma_lop := NULL;
                    v_ma_nv := NULL;

                    IF v_loai_dv = 'PT' THEN
                        IF v_trainer_id IS NOT NULL THEN
                            SELECT COUNT(*) INTO v_count FROM NHANVIEN
                            WHERE MaNV = v_trainer_id AND LoaiNV = 'Trainer';

                            IF v_count = 0 THEN
                                ROLLBACK;
                                p_result := 'ERROR';
                                p_error_msg := 'Không tìm thấy trainer: ' || v_trainer_id;
                                RETURN;
                            END IF;

                            SELECT COUNT(*) INTO v_count FROM CT_CHUYENMON cm
                            WHERE cm.MaNV = v_trainer_id AND cm.MaBM = v_ma_bm;

                            IF v_count = 0 THEN
                                ROLLBACK;
                                p_result := 'ERROR';
                                p_error_msg := 'Trainer ' || v_trainer_id || ' không có chuyên môn phù hợp';
                                RETURN;
                            END IF;

                            v_ma_nv := v_trainer_id;
                        ELSE
                            SELECT nv.MaNV INTO v_ma_nv FROM NHANVIEN nv
                            JOIN CT_CHUYENMON cm ON nv.MaNV = cm.MaNV
                            WHERE nv.LoaiNV = 'Trainer' AND cm.MaBM = v_ma_bm
                            ORDER BY nv.MaNV LIMIT 1;

                            IF v_ma_nv IS NULL THEN
                                ROLLBACK;
                                p_result := 'ERROR';
                                p_error_msg := 'Không tìm thấy trainer phù hợp cho bộ môn: ' || v_ma_bm;
                                RETURN;
                            END IF;
                        END IF;

                    ELSIF v_loai_dv = 'Lop' THEN
                        IF v_class_id IS NOT NULL THEN
                            SELECT COUNT(*), MAX(MaNV) INTO v_count, v_ma_nv FROM LOP
                            WHERE MaLop = v_class_id AND MaBM = v_ma_bm AND TinhTrang = 'ChuaDay';

                            IF v_count = 0 THEN
                                SELECT COUNT(*) INTO v_count FROM LOP WHERE MaLop = v_class_id;
                                ROLLBACK;
                                p_result := 'ERROR';
                                IF v_count = 0 THEN
                                    p_error_msg := 'Không tìm thấy lớp: ' || v_class_id;
                                ELSE
                                    p_error_msg := 'Lớp ' || v_class_id || ' không phù hợp hoặc đã đầy';
                                END IF;
                                RETURN;
                            END IF;
                            v_ma_lop := v_class_id;
                        ELSE
                            SELECT MaLop, MaNV INTO v_ma_lop, v_ma_nv FROM LOP
                            WHERE MaBM = v_ma_bm AND TinhTrang = 'ChuaDay'
                            ORDER BY MaLop LIMIT 1;

                            IF v_ma_lop IS NULL THEN
                                ROLLBACK;
                                p_result := 'ERROR';
                                p_error_msg := 'Không còn lớp trống cho bộ môn: ' || v_ma_bm;
                                RETURN;
                            END IF;
                        END IF;

                    ELSIF v_loai_dv = 'TuDo' THEN
                        NULL;
                    ELSE
                        ROLLBACK;
                        p_result := 'ERROR';
                        p_error_msg := 'Loại dịch vụ không hợp lệ: ' || v_loai_dv;
                        RETURN;
                    END IF;

                    INSERT INTO CT_DKDV (MaCTDK, MaHD, MaDV, NgayBD, NgayKT, MaLop, MaNV)
                    VALUES (v_ma_ctdk, v_ma_hd, v_ma_dv, CURRENT_TIMESTAMP,
                            CURRENT_TIMESTAMP + (v_thoi_han || ' days')::INTERVAL,
                            v_ma_lop, v_ma_nv);

                    v_tong_tien := v_tong_tien + v_don_gia;

                    RAISE NOTICE 'Đã thêm: % (%) - Trainer: % - Lớp: %',
                        v_ma_dv, v_loai_dv, COALESCE(v_ma_nv, 'NULL'), COALESCE(v_ma_lop, 'NULL');
                END IF;
            END LOOP;

            UPDATE HOADON SET TongTien = v_tong_tien WHERE MaHD = v_ma_hd;

            p_ma_hd := v_ma_hd;
            p_tong_tien := v_tong_tien;
            p_result := 'SUCCESS';
            p_error_msg := NULL;
            EXIT;

        EXCEPTION
            WHEN unique_violation THEN
                ROLLBACK;
                v_retry_count := v_retry_count + 1;
                IF v_retry_count > v_max_retries THEN
                    p_result := 'ERROR';
                    p_error_msg := 'Hệ thống đang bận, vui lòng thử lại sau';
                    RETURN;
                END IF;
                PERFORM pg_sleep(0.1);
            WHEN OTHERS THEN
                ROLLBACK;
                p_result := 'ERROR';
                p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
                RETURN;
        END;
    END LOOP;

    IF v_retry_count > v_max_retries THEN
        p_result := 'ERROR';
        p_error_msg := 'Không thể hoàn thành đăng ký sau ' || v_max_retries || ' lần thử';
    END IF;
END;
$$;

-- =====================================================================================
-- PROCEDURE THANH TOÁN HÓA ĐƠN
-- =====================================================================================

CREATE OR REPLACE FUNCTION proc_thanh_toan_hoa_don(
    p_ma_hd VARCHAR(10),
    OUT p_result VARCHAR(20),
    OUT p_error_msg TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_count INTEGER;
    v_trang_thai VARCHAR(50);
BEGIN
    SELECT COUNT(*), MAX(TrangThai) INTO v_count, v_trang_thai
    FROM HOADON WHERE MaHD = p_ma_hd;

    IF v_count = 0 THEN
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy hóa đơn: ' || p_ma_hd;
        RETURN;
    END IF;

    IF v_trang_thai = 'DaThanhToan' THEN
        p_result := 'ERROR';
        p_error_msg := 'Hóa đơn đã được thanh toán: ' || p_ma_hd;
        RETURN;
    END IF;

    UPDATE HOADON SET TrangThai = 'DaThanhToan', NgayTT = CURRENT_TIMESTAMP
    WHERE MaHD = p_ma_hd;

    p_result := 'SUCCESS';
    p_error_msg := NULL;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
END;
$$;

-- =====================================================================================
-- FUNCTION LẤY THÔNG TIN HÓA ĐƠN CHI TIẾT
-- =====================================================================================

CREATE OR REPLACE FUNCTION get_hoa_don_chi_tiet(p_ma_hd VARCHAR(10))
RETURNS TABLE (
    MaHD VARCHAR(10),
    TongTien NUMERIC,
    NgayLap TIMESTAMP,
    TrangThai VARCHAR(50),
    NgayTT TIMESTAMP,
    MaKH VARCHAR(10),
    HoTen VARCHAR(100),
    Email VARCHAR(100),
    MaCTDK VARCHAR(10),
    NgayBD TIMESTAMP,
    NgayKT TIMESTAMP,
    MaDV VARCHAR(10),
    TenDV VARCHAR(200),
    DonGia NUMERIC,
    ThoiHan INTEGER,
    LoaiDV VARCHAR(20),
    TenBM VARCHAR(100),
    TenLop VARCHAR(100),
    TenNhanVien VARCHAR(100)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        hd.MaHD, hd.TongTien, hd.NgayLap, hd.TrangThai, hd.NgayTT,
        kh.MaKH, kh.HoTen, kh.Email,
        ct.MaCTDK, ct.NgayBD, ct.NgayKT,
        dv.MaDV, dv.TenDV, dv.DonGia, dv.ThoiHan, dv.LoaiDV,
        bm.TenBM, l.TenLop, nv.TenNV as TenNhanVien
    FROM HOADON hd
    JOIN KHACHHANG kh ON hd.MaKH = kh.MaKH
    JOIN CT_DKDV ct ON hd.MaHD = ct.MaHD
    JOIN DICHVU dv ON ct.MaDV = dv.MaDV
    LEFT JOIN BOMON bm ON dv.MaBM = bm.MaBM
    LEFT JOIN LOP l ON ct.MaLop = l.MaLop
    LEFT JOIN NHANVIEN nv ON ct.MaNV = nv.MaNV
    WHERE hd.MaHD = p_ma_hd
    ORDER BY ct.MaCTDK;
EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Error in get_hoa_don_chi_tiet: %', SQLERRM;
END;
$$;

-- =====================================================================================
-- PROCEDURE KIỂM TRA ĐIỀU KIỆN ĐĂNG KÝ
-- =====================================================================================

CREATE OR REPLACE FUNCTION proc_kiem_tra_dieu_kien_dang_ky(
    p_ma_kh VARCHAR(10),
    p_ma_dv VARCHAR(10),
    OUT p_can_dang_ky INTEGER,
    OUT p_ly_do TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_count INTEGER;
    v_loai_dv VARCHAR(20);
    v_ma_bm VARCHAR(10);
BEGIN
    SELECT COUNT(*) INTO v_count FROM KHACHHANG WHERE MaKH = p_ma_kh;
    IF v_count = 0 THEN
        p_can_dang_ky := 0;
        p_ly_do := 'Khách hàng không tồn tại';
        RETURN;
    END IF;

    SELECT COUNT(*), MAX(LoaiDV), MAX(MaBM) INTO v_count, v_loai_dv, v_ma_bm
    FROM DICHVU WHERE MaDV = p_ma_dv;
    IF v_count = 0 THEN
        p_can_dang_ky := 0;
        p_ly_do := 'Dịch vụ không tồn tại';
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count FROM CT_DKDV ct
    JOIN HOADON hd ON ct.MaHD = hd.MaHD
    WHERE hd.MaKH = p_ma_kh AND ct.MaDV = p_ma_dv AND hd.TrangThai = 'DaThanhToan';
    IF v_count > 0 THEN
        p_can_dang_ky := 0;
        p_ly_do := 'Đã đăng ký dịch vụ này rồi';
        RETURN;
    END IF;

    IF v_loai_dv != 'TuDo' THEN
        SELECT COUNT(*) INTO v_count FROM LOP
        WHERE MaBM = v_ma_bm AND TinhTrang = 'ChuaDay';
        IF v_count = 0 THEN
            p_can_dang_ky := 0;
            p_ly_do := 'Không còn lớp trống cho bộ môn này';
            RETURN;
        END IF;
    END IF;

    p_can_dang_ky := 1;
    p_ly_do := 'Có thể đăng ký';
EXCEPTION
    WHEN OTHERS THEN
        p_can_dang_ky := 0;
        p_ly_do := 'Lỗi hệ thống: ' || SQLERRM;
END;
$$;

-- =====================================================================================
-- PROCEDURE ĐĂNG KÝ DỊCH VỤ PT VỚI TRAINER CỤ THỂ
-- =====================================================================================

CREATE OR REPLACE FUNCTION proc_dang_ky_dich_vu_pt(
    p_ma_kh VARCHAR(10),
    p_ma_dv VARCHAR(10),
    p_ma_trainer VARCHAR(10),
    OUT p_ma_hd VARCHAR(10),
    OUT p_ma_ctdk VARCHAR(10),
    OUT p_result VARCHAR(20),
    OUT p_error_msg TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_don_gia NUMERIC;
    v_thoi_han INTEGER;
    v_loai_dv VARCHAR(20);
    v_ma_bm VARCHAR(10);
    v_max_hd_number INTEGER;
    v_max_ctdk_number INTEGER;
    v_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM KHACHHANG WHERE MaKH = p_ma_kh;
    IF v_count = 0 THEN
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy khách hàng: ' || p_ma_kh;
        RETURN;
    END IF;

    SELECT COUNT(*), MAX(DonGia), MAX(ThoiHan), MAX(LoaiDV), MAX(MaBM)
    INTO v_count, v_don_gia, v_thoi_han, v_loai_dv, v_ma_bm
    FROM DICHVU WHERE MaDV = p_ma_dv;

    IF v_count = 0 THEN
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy dịch vụ: ' || p_ma_dv;
        RETURN;
    END IF;

    IF v_loai_dv != 'PT' THEN
        p_result := 'ERROR';
        p_error_msg := 'Dịch vụ không phải loại PT: ' || p_ma_dv;
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count FROM NHANVIEN
    WHERE MaNV = p_ma_trainer AND LoaiNV = 'Trainer';
    IF v_count = 0 THEN
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy trainer: ' || p_ma_trainer;
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count FROM CT_CHUYENMON cm
    WHERE cm.MaNV = p_ma_trainer AND cm.MaBM = v_ma_bm;
    IF v_count = 0 THEN
        p_result := 'ERROR';
        p_error_msg := 'Trainer không có chuyên môn phù hợp';
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count FROM CT_DKDV ct
    JOIN HOADON hd ON ct.MaHD = hd.MaHD
    WHERE hd.MaKH = p_ma_kh AND ct.MaDV = p_ma_dv AND hd.TrangThai = 'DaThanhToan';
    IF v_count > 0 THEN
        p_result := 'ERROR';
        p_error_msg := 'Khách hàng đã đăng ký dịch vụ này rồi';
        RETURN;
    END IF;

    SELECT COALESCE(MAX(SUBSTRING(MaHD FROM 3)::INTEGER), 0) + 1
    INTO v_max_hd_number FROM HOADON WHERE MaHD LIKE 'HD%';
    p_ma_hd := 'HD' || LPAD(v_max_hd_number::TEXT, 3, '0');

    INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
    VALUES (p_ma_hd, p_ma_kh, CURRENT_TIMESTAMP, 'ChuaThanhToan', v_don_gia);

    SELECT COALESCE(MAX(SUBSTRING(MaCTDK FROM 3)::INTEGER), 0) + 1
    INTO v_max_ctdk_number FROM CT_DKDV WHERE MaCTDK LIKE 'CT%';
    p_ma_ctdk := 'CT' || LPAD(v_max_ctdk_number::TEXT, 3, '0');

    INSERT INTO CT_DKDV (MaCTDK, MaHD, MaDV, NgayBD, NgayKT, MaLop, MaNV)
    VALUES (p_ma_ctdk, p_ma_hd, p_ma_dv, CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP + (v_thoi_han || ' days')::INTERVAL, NULL, p_ma_trainer);

    p_result := 'SUCCESS';
    p_error_msg := NULL;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
END;
$$;

-- =====================================================================================
-- PROCEDURE CẬP NHẬT TRAINER CHO CHI TIẾT ĐÃ TỒN TẠI
-- =====================================================================================

CREATE OR REPLACE FUNCTION proc_cap_nhat_trainer_cho_ctdk(
    p_ma_ctdk VARCHAR(10),
    p_ma_trainer VARCHAR(10),
    OUT p_result VARCHAR(20),
    OUT p_error_msg TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_count INTEGER;
    v_ma_dv VARCHAR(10);
    v_ma_bm VARCHAR(10);
    v_loai_dv VARCHAR(20);
BEGIN
    SELECT COUNT(*), MAX(ct.MaDV), MAX(dv.MaBM), MAX(dv.LoaiDV)
    INTO v_count, v_ma_dv, v_ma_bm, v_loai_dv
    FROM CT_DKDV ct JOIN DICHVU dv ON ct.MaDV = dv.MaDV
    WHERE ct.MaCTDK = p_ma_ctdk;

    IF v_count = 0 THEN
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy chi tiết đăng ký: ' || p_ma_ctdk;
        RETURN;
    END IF;

    IF v_loai_dv != 'PT' THEN
        p_result := 'ERROR';
        p_error_msg := 'Chi tiết đăng ký không phải dịch vụ PT';
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count FROM NHANVIEN
    WHERE MaNV = p_ma_trainer AND LoaiNV = 'Trainer';
    IF v_count = 0 THEN
        p_result := 'ERROR';
        p_error_msg := 'Không tìm thấy trainer: ' || p_ma_trainer;
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count FROM CT_CHUYENMON cm
    WHERE cm.MaNV = p_ma_trainer AND cm.MaBM = v_ma_bm;
    IF v_count = 0 THEN
        p_result := 'ERROR';
        p_error_msg := 'Trainer không có chuyên môn phù hợp';
        RETURN;
    END IF;

    UPDATE CT_DKDV SET MaNV = p_ma_trainer WHERE MaCTDK = p_ma_ctdk;

    p_result := 'SUCCESS';
    p_error_msg := NULL;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'ERROR';
        p_error_msg := 'Lỗi hệ thống: ' || SQLERRM;
END;
$$;

-- =====================================================================================
-- CÁCH SỬ DỤNG CÁC FUNCTION
-- =====================================================================================

/*
-- 1. Đăng ký nhiều dịch vụ hỗn hợp:
SELECT * FROM proc_dang_ky_dich_vu_universal(
    'KH001',
    'DV001,DV002,DV003',
    'NV001,,NV002',
    ',LOP001,'
);

-- 2. Thanh toán hóa đơn:
SELECT * FROM proc_thanh_toan_hoa_don('HD001');

-- 3. Lấy chi tiết hóa đơn:
SELECT * FROM get_hoa_don_chi_tiet('HD001');

-- 4. Kiểm tra điều kiện đăng ký:
SELECT * FROM proc_kiem_tra_dieu_kien_dang_ky('KH001', 'DV001');

-- 5. Đăng ký PT với trainer cụ thể:
SELECT * FROM proc_dang_ky_dich_vu_pt('KH001', 'DV001', 'NV001');

-- 6. Cập nhật trainer cho chi tiết:
SELECT * FROM proc_cap_nhat_trainer_cho_ctdk('CT001', 'NV002');
*/