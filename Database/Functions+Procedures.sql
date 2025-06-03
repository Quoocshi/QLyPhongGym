--ACCOUNT
-- 1. Hàm tìm Account theo userName
CREATE OR REPLACE FUNCTION find_account_by_username(p_username IN VARCHAR2)
RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT a.account_id, a.role_group_id, a.MaKH, a.MaNV, a.username, 
               a.password_hash, a.status, a.created_at, a.updated_at, a.is_deleted
        FROM account a
        WHERE a.username = p_username
        AND a.is_deleted = 0;
    RETURN v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20001, 'Error in find_account_by_username: ' || SQLERRM);
END;
/
-- 2. Hàm tìm Account theo accountId
CREATE OR REPLACE FUNCTION find_account_by_account_id(p_account_id IN NUMBER)
RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT a.account_id, a.role_group_id, a.MaKH, a.MaNV, a.username, 
               a.password_hash, a.status, a.created_at, a.updated_at, a.is_deleted
        FROM account a
        WHERE a.account_id = p_account_id
        AND a.is_deleted = 0;
    RETURN v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20002, 'Error in find_account_by_account_id: ' || SQLERRM);
END;
/
-- 3. Hàm tìm role_group_id theo accountId
CREATE OR REPLACE FUNCTION find_role_group_id_by_account_id(p_account_id IN NUMBER)
RETURN NUMBER
AS
    v_role_group_id NUMBER;
BEGIN
    SELECT a.role_group_id
    INTO v_role_group_id
    FROM account a
    WHERE a.account_id = p_account_id
    AND a.is_deleted = 0;
    
    RETURN v_role_group_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/
-- 4. Hàm tìm account_id theo email
CREATE OR REPLACE FUNCTION find_account_id_by_email(p_email IN VARCHAR2)
RETURN NUMBER
AS
    v_account_id NUMBER;
BEGIN
    SELECT a.account_id
    INTO v_account_id
    FROM account a
    LEFT JOIN KHACHHANG kh ON a.MaKH = kh.MaKH
    LEFT JOIN NHANVIEN nv ON a.MaNV = nv.MaNV
    WHERE (kh.email = p_email OR nv.email = p_email)
    AND a.is_deleted = 0;
    
    RETURN v_account_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/
-- 5. Hàm tìm Account theo email
CREATE OR REPLACE FUNCTION find_account_by_email(p_email IN VARCHAR2)
RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT a.account_id, a.role_group_id, a.MaKH, a.MaNV, a.username, 
               a.password_hash, a.status, a.created_at, a.updated_at, a.is_deleted
        FROM account a
        LEFT JOIN KHACHHANG kh ON a.MaKH = kh.MaKH
        LEFT JOIN NHANVIEN nv ON a.MaNV = nv.MaNV
        WHERE (kh.email = p_email OR nv.email = p_email)
        AND a.is_deleted = 0;
    RETURN v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20003, 'Error in find_account_by_email: ' || SQLERRM);
END;
/
-- 6. Hàm kiểm tra sự tồn tại của Account theo email
CREATE OR REPLACE FUNCTION exists_by_email(p_email IN VARCHAR2)
RETURN NUMBER
AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM account a
    LEFT JOIN KHACHHANG kh ON a.MaKH = kh.MaKH
    LEFT JOIN NHANVIEN nv ON a.MaNV = nv.MaNV
    WHERE (kh.email = p_email OR nv.email = p_email)
    AND a.is_deleted = 0;
    
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
/
-- 7. Hàm tìm Account theo mã khách hàng (MaKH)
CREATE OR REPLACE FUNCTION find_account_by_ma_kh(p_ma_kh IN VARCHAR2)
RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT a.account_id, a.role_group_id, a.MaKH, a.MaNV, a.username, 
               a.password_hash, a.status, a.created_at, a.updated_at, a.is_deleted
        FROM account a
        WHERE a.MaKH = p_ma_kh
        AND a.is_deleted = 0;
    RETURN v_cursor;
END;
/
--DKY DICH VU
CREATE OR REPLACE FUNCTION get_max_ctdk_number
RETURN NUMBER
AS
    v_max_number NUMBER;
BEGIN
    SELECT MAX(TO_NUMBER(SUBSTR(ct.ma_ctdk, 3)))
    INTO v_max_number
    FROM ct_dkdv ct
    WHERE ct.ma_ctdk LIKE 'CT%';
    
    RETURN v_max_number;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0; -- Trả về 0 nếu không có mã nào
END;
/
--DỊCH VỤ
CREATE OR REPLACE FUNCTION list_dichvu_khachhang_chua_dangky(p_ma_kh IN VARCHAR2)
RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT d.MaDV, d.TenDV, d.LoaiDV, d.ThoiHan, d.DonGia, d.MaBM
        FROM dichvu d
        WHERE d.MaDV NOT IN (
            SELECT ct.MaDV
            FROM ct_dkdv ct
            JOIN hoadon hd ON ct.MaHD = hd.MaHD
            WHERE hd.MaKH = p_ma_kh
        );
    RETURN v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20004, 'Error in list_dichvu_khachhang_chua_dangky: ' || SQLERRM);
END;
/
-- 3. Hàm liệt kê các dịch vụ theo bộ môn mà khách hàng chưa đăng ký
CREATE OR REPLACE FUNCTION list_dichvu_theo_bomon_khachhang_chua_dangky(
    p_ma_bm IN VARCHAR2,
    p_ma_kh IN VARCHAR2
)
RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT d.MaDV, d.TenDV, d.LoaiDV, d.ThoiHan, d.DonGia, d.MaBM
        FROM dichvu d
        WHERE d.MaBM = p_ma_bm
        AND d.MaDV NOT IN (
            SELECT ct.ma_dv
            FROM ct_dkdv ct
            JOIN hoadon hd ON ct.MaHD = hd.MaHD
            WHERE hd.MaKH= p_ma_kh
        );
    RETURN v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20005, 'Error in list_dichvu_theo_bomon_khachhang_chua_dangky: ' || SQLERRM);
END;
/
--hoadon
CREATE OR REPLACE FUNCTION FindMaxMaHoaDonNumber
RETURN NUMBER AS
    v_max_number NUMBER;
BEGIN
    SELECT MAX(TO_NUMBER(SUBSTR(MaHD, 3)))
    INTO v_max_number
    FROM HOADON
    WHERE MaHD LIKE 'HD%';
    RETURN v_max_number;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
    WHEN VALUE_ERROR THEN
        RETURN NULL;
END;
/

--khachhang
CREATE OR REPLACE FUNCTION FindMaxMaKHNumber
RETURN NUMBER AS
    v_max_number NUMBER;
BEGIN
    SELECT MAX(TO_NUMBER(SUBSTR(MaKH, 3)))
    INTO v_max_number
    FROM KHACHHANG
    WHERE MaKH LIKE 'KH%';
    RETURN v_max_number;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
    WHEN VALUE_ERROR THEN
        RETURN NULL;
END;
/

CREATE OR REPLACE FUNCTION FindMaxReferralCodeNumber
RETURN NUMBER AS
    v_max_number NUMBER;
BEGIN
    SELECT MAX(TO_NUMBER(SUBSTR(ReferralCode, 4)))
    INTO v_max_number
    FROM KHACHHANG
    WHERE ReferralCode LIKE 'REF%';
    RETURN v_max_number;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
    WHEN VALUE_ERROR THEN
        RETURN NULL;
END;
/

CREATE OR REPLACE PROCEDURE FindAllWithActiveAccount (p_cursor OUT SYS_REFCURSOR) AS
BEGIN
    OPEN p_cursor FOR
        SELECT k.*
        FROM KHACHHANG k
        JOIN ACCOUNT a ON k.MaKH = a.MaKH
        WHERE a.IS_DELETED = 0;
END;
/

--rolegroup
CREATE OR REPLACE FUNCTION FindRoleNameByRoleGroupId (p_role_group_id IN NUMBER)
RETURN VARCHAR2 AS
    v_role_name VARCHAR2(100);
BEGIN
    SELECT NAME_ROLE_GROUP
    INTO v_role_name
    FROM ROLE_GROUP
    WHERE ROLE_GROUP_ID = p_role_group_id
    AND IS_DELETED = 0;
    RETURN v_role_name;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
    WHEN TOO_MANY_ROWS THEN
        RETURN NULL; -- Không xảy ra do ROLE_GROUP_ID là khóa chính
END;
/
