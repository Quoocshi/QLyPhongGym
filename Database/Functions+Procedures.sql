-- Function
-- Lấy ds gói tập đã dki của khách hàng
CREATE OR REPLACE FUNCTION SubPackage(p_customer_ID VARCHAR2)
RETURN SYS_REFCURSOR
IS
  v_cursor SYS_REFCURSOR;
BEGIN
  OPEN v_cursor FOR
    SELECT 
    FROM SUBSCRIPTION s
    JOIN Package p ON P.MaGoi = s.MaGoi 
    WHERE cp.CustomerId = p_customerId;
    
  RETURN v_cursor;
END;
/
-- Kiểm tra thông tin gói tập
CREATE OR REPLACE FUNCTION BookPackage(p_package_ID NUMBER)
RETURN VARCHAR2
IS
  v_info VARCHAR2;
BEGIN
  SELECT 'Gói: ' || TenGoi || ', Giá: ' || GiaTien || ', Thời hạn: ' ThoiHan || ' tháng'
  INTO V_INFO
  FROM PACKAGE p 
  WHERE P.MAGOI  = P_PACKAGE_ID;
  RETURN V_INFO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
  RETURN 'Không tìm thấy gói tập';
END;
