CREATE OR REPLACE PROCEDURE procedure_update_dichvu(
    p_madv VARCHAR,
    p_tendv VARCHAR,
    p_loaidev VARCHAR,
    p_thoihan INTEGER,
    p_dongia DOUBLE PRECISION,
    p_mabm VARCHAR,
    p_version INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Kiểm tra tồn tại và đúng version
    IF NOT EXISTS (SELECT 1 FROM dichvu WHERE madv = p_madv AND version = p_version) THEN
        RAISE EXCEPTION 'Xung đột phiên bản dịch vụ, vui lòng tải lại trang';
    END IF;

    -- Cập nhật
    UPDATE dichvu
    SET tendv   = p_tendv,
        loaidv  = p_loaidev,
        thoihan = p_thoihan,
        dongia  = p_dongia,
        mabm    = p_mabm,
        version = version + 1
    WHERE madv = p_madv AND version = p_version;
END;
$$;
