-- =====================================================================================
-- TRUE CHECKPOINT RECOVERY DEMO - ASCII VERSION
-- Demonstrates correct checkpoint recovery: restoring to EXACT checkpoint state
-- =====================================================================================
-- STEP 1: ADD STABLE DATA BEFORE CHECKPOINT (WILL BE PRESERVED)
-- =====================================================================================
BEGIN
    -- Add stable customer BEFORE checkpoint
    INSERT INTO KHACHHANG (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
    VALUES ('KH_BEFORE', 'Nguyen Van Before', DATE '1990-01-15', 'Nam', 'before@gym.com', '0901234567', 'Before Checkpoint St', 'REF_BEFORE');
    
    -- Add stable invoice BEFORE checkpoint
    INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
    VALUES ('HD_BEFORE', 'KH_BEFORE', SYSDATE, 'DaThanhToan', 2000000);
    
    COMMIT; -- IMPORTANT: Commit this data
    DBMS_OUTPUT.PUT_LINE('+ Stable data added and committed BEFORE checkpoint');
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('- Error adding data: ' || SQLERRM);
END;
/

-- Check data before checkpoint
SELECT 
    'PRE-CHECKPOINT STATE' AS Status,
    (SELECT COUNT(*) FROM KHACHHANG) AS Total_Customers,
    (SELECT COUNT(*) FROM HOADON) AS Total_Invoices,
    (SELECT COUNT(*) FROM KHACHHANG WHERE MaKH = 'KH_BEFORE') AS Before_Customer,
    TO_CHAR(SYSDATE, 'HH24:MI:SS') AS Current_Time
FROM DUAL;

-- =====================================================================================
-- STEP 2: CREATE CHECKPOINT (BACKUP STABLE DATA)
-- =====================================================================================
DECLARE
    v_checkpoint_id NUMBER;
    v_message VARCHAR2(500);
BEGIN
    create_gym_checkpoint(v_checkpoint_id, v_message);
    DBMS_OUTPUT.PUT_LINE('+ ' || v_message);
    DBMS_OUTPUT.PUT_LINE('+ Checkpoint has backed up current stable data');
END;
/

-- =====================================================================================
-- STEP 3: ADD DATA AFTER CHECKPOINT (WILL BE LOST - CORRECT BEHAVIOR)
-- =====================================================================================
BEGIN
    -- Add data after checkpoint and commit. This data will be LOST during recovery.
    INSERT INTO KHACHHANG (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
    VALUES ('KH_AFTER', 'Tran Van After', DATE '1992-05-20', 'Nam', 'after@gym.com', '0909111222', 'After Checkpoint St', 'REF_AFTER');
    
    INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
    VALUES ('HD_AFTER', 'KH_AFTER', SYSDATE, 'DaThanhToan', 1500000);
    
    COMMIT; -- Commit this data
    DBMS_OUTPUT.PUT_LINE('+ Data AFTER checkpoint has been committed');
    DBMS_OUTPUT.PUT_LINE('  (This data WILL BE LOST during checkpoint recovery - this is CORRECT behavior)');
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('- Error: ' || SQLERRM);
END;
/

-- =====================================================================================
-- STEP 4: ADD UNCOMMITTED DATA (SIMULATE CRASH)
-- =====================================================================================
BEGIN
    -- Add data but DO NOT commit (simulate crash)
    INSERT INTO KHACHHANG (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
    VALUES ('KH_CRASH', 'Le Thi Crash', DATE '1995-08-25', 'Nu', 'crash@gym.com', '0909333444', 'Crash Street', 'REF_CRASH');
    
    INSERT INTO HOADON (MaHD, MaKH, NgayLap, TrangThai, TongTien)
    VALUES ('HD_CRASH', 'KH_CRASH', SYSDATE, 'ChuaThanhToan', 999999);
    
    DBMS_OUTPUT.PUT_LINE('+ Uncommitted data added (will be lost)');
    
    -- Log crash (only commit this log, not the data above)
    INSERT INTO GYM_CHECKPOINT (SystemStatus, Description)
    VALUES ('SYSTEM_CRASHED', 'Demo crash at ' || TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'));
    COMMIT; -- Only commit crash log
    
    DBMS_OUTPUT.PUT_LINE('+ System crash simulated - uncommitted data lost');
END;
/

-- =====================================================================================
-- STEP 5: CHECK STATE BEFORE RECOVERY
-- =====================================================================================
SELECT 
    'BEFORE RECOVERY' AS Phase,
    (SELECT COUNT(*) FROM KHACHHANG) AS Total_Customers,
    (SELECT COUNT(*) FROM HOADON) AS Total_Invoices,
    (SELECT COUNT(*) FROM KHACHHANG WHERE MaKH = 'KH_BEFORE') AS Before_Checkpoint_Data,
    (SELECT COUNT(*) FROM KHACHHANG WHERE MaKH = 'KH_AFTER') AS After_Checkpoint_Data,
    (SELECT COUNT(*) FROM KHACHHANG WHERE MaKH = 'KH_CRASH') AS Uncommitted_Data,
    TO_CHAR(SYSDATE, 'HH24:MI:SS') AS Check_Time
FROM DUAL;

BEGIN
DBMS_OUTPUT.PUT_LINE('=== STATE BEFORE RECOVERY ===');
DBMS_OUTPUT.PUT_LINE('+ Data BEFORE checkpoint (will be kept): KH_BEFORE');
DBMS_OUTPUT.PUT_LINE('+ Data AFTER checkpoint (will be LOST): KH_AFTER');
DBMS_OUTPUT.PUT_LINE('+ Uncommitted data (will be lost): KH_CRASH');
END;
/

-- =====================================================================================
-- STEP 6: PERFORM RECOVERY
-- =====================================================================================
DECLARE
    v_status VARCHAR2(100);
    v_details VARCHAR2(1000);
BEGIN
    gym_checkpoint_recovery(NULL, v_status, v_details);
    
    DBMS_OUTPUT.PUT_LINE('>> Recovery Status: ' || v_status);
    IF LENGTH(v_details) > 100 THEN
        DBMS_OUTPUT.PUT_LINE('>> Details: ' || SUBSTR(v_details, 1, 100) || '...');
    ELSE
        DBMS_OUTPUT.PUT_LINE('>> Details: ' || v_details);
    END IF;
END;
/

-- =====================================================================================
-- STEP 7: CHECK RESULTS AFTER RECOVERY
-- =====================================================================================
SELECT 
    'AFTER RECOVERY' AS Phase,
    (SELECT COUNT(*) FROM KHACHHANG) AS Total_Customers,
    (SELECT COUNT(*) FROM HOADON) AS Total_Invoices,
    (SELECT COUNT(*) FROM KHACHHANG WHERE MaKH = 'KH_BEFORE') AS Before_Checkpoint_Data,
    (SELECT COUNT(*) FROM KHACHHANG WHERE MaKH = 'KH_AFTER') AS After_Checkpoint_Data,
    (SELECT COUNT(*) FROM KHACHHANG WHERE MaKH = 'KH_CRASH') AS Uncommitted_Data,
    TO_CHAR(SYSDATE, 'HH24:MI:SS') AS Recovery_Time
FROM DUAL;

-- =====================================================================================
-- STEP 8: VALIDATION AND CONCLUSION
-- =====================================================================================
DECLARE
    v_before_count NUMBER;
    v_after_count NUMBER;
    v_crash_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_before_count FROM KHACHHANG WHERE MaKH = 'KH_BEFORE';
    SELECT COUNT(*) INTO v_after_count FROM KHACHHANG WHERE MaKH = 'KH_AFTER';
    SELECT COUNT(*) INTO v_crash_count FROM KHACHHANG WHERE MaKH = 'KH_CRASH';
    
    DBMS_OUTPUT.PUT_LINE('=== VALIDATION RESULTS ===');
    
    IF v_before_count = 1 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: Pre-checkpoint data (KH_BEFORE) preserved');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: Pre-checkpoint data lost!');
    END IF;
    
    IF v_after_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: Post-checkpoint data (KH_AFTER) properly LOST');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: Post-checkpoint data should have been lost!');
    END IF;
    
    IF v_crash_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: Uncommitted data (KH_CRASH) properly removed');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: Uncommitted data still exists!');
    END IF;
    
    DBMS_OUTPUT.PUT_LINE('=========================');
    
    IF v_before_count = 1 AND v_after_count = 0 AND v_crash_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('SUCCESS: TRUE CHECKPOINT RECOVERY WORKS CORRECTLY!');
        DBMS_OUTPUT.PUT_LINE('   System restored to EXACT checkpoint state.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('WARNING: RECOVERY ISSUES DETECTED');
    END IF;
    
END;
/

-- =====================================================================================
-- CLEANUP
-- =====================================================================================
BEGIN
    DELETE FROM CHECKPOINT_LOG WHERE Description LIKE '%KH_BEFORE%' OR Description LIKE '%KH_AFTER%' OR Description LIKE '%KH_CRASH%';
    DELETE FROM HOADON WHERE MaHD IN ('HD_BEFORE', 'HD_AFTER', 'HD_CRASH');
    DELETE FROM KHACHHANG WHERE MaKH IN ('KH_BEFORE', 'KH_AFTER', 'KH_CRASH');
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('+ Demo data cleaned up');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('! Cleanup warning: ' || SUBSTR(SQLERRM, 1, 50));
END;
/



