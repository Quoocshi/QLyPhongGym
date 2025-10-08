CREATE OR REPLACE PROCEDURE PROCEDURE_UPDATE_DICHVU (
    p_MaDV    IN DICHVU.MaDV%TYPE,
    p_TenDV   IN DICHVU.TenDV%TYPE,
    p_LoaiDV  IN DICHVU.LoaiDV%TYPE,
    p_ThoiHan IN DICHVU.ThoiHan%TYPE,
    p_DonGia  IN DICHVU.DonGia%TYPE,
    p_MaBM    IN DICHVU.MaBM%TYPE,
    p_Version IN DICHVU.Version%TYPE
) AS
    v_count NUMBER;
BEGIN
    -- Kiểm tra tồn tại và đúng version
    SELECT COUNT(*) INTO v_count
    FROM DICHVU
    WHERE MaDV = p_MaDV AND Version = p_Version;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Xung đột phiên bản dịch vụ, vui lòng tải lại trang');
    END IF;

    -- Nếu đúng thì cập nhật
    UPDATE DICHVU
    SET TenDV   = p_TenDV,
        LoaiDV  = p_LoaiDV,
        ThoiHan = p_ThoiHan,
        DonGia  = p_DonGia,
        MaBM    = p_MaBM,
        Version = Version + 1
    WHERE MaDV = p_MaDV AND Version = p_Version;

    COMMIT;
END;
/

