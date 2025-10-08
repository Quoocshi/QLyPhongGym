--Trigger đối với khách hàng
-- Ràng buộc nghiệp vụ
-- MaKH phải là duy nhất
CREATE OR REPLACE TRIGGER trg_check_unique_makh
BEFORE INSERT ON CUSTOMER
FOR EACH ROW
DECLARE
  v_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_count 
  FROM CUSTOMER c
  WHERE :NEW.MaKH = 
  IF v_count > 0 THEN
    RAISE_APPLICATION_ERROR(-20001,'Mã khách đã tồn tại!');
    END IF;
END;
-- --
-- --Test case 1: fail
-- INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
-- VALUES ('KH001', 'Nguyễn Văn A', TO_DATE('1990-05-15', 'YYYY-MM-DD'), 'Nam', 'nva@gmail.com', '0909123456', 'Hà Nội', 'REF123');
-- --Test case 2: success
-- INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
-- VALUES ('KH005', 'Trần Thị E', TO_DATE('1998-03-22', 'YYYY-MM-DD'), 'Nữ', 'tte@gmail.com', '0909555777', 'Hải Phòng', 'REF235');
--
-- SELECT * FROM CUSTOMER c;

-- Số điện thoại (SDT) phải có độ dài 10 ký tự và chỉ chứa chữ số.
CREATE OR REPLACE TRIGGER trg_check_phone_number
BEFORE INSERT OR UPDATE ON CUSTOMER
FOR EACH ROW
BEGIN
  IF NOT REGEXP_LIKE(:NEW.SODIENTHOAI, '\d{10}$') THEN 
    RAISE_APPLICATION_ERROR(-20002,'Số điện thoại phải có 10 ký tự và chỉ chứa chữ số');
  END IF;
END;
--
--
-- --Test case 1: SĐT không đủ chữ số
-- INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
-- VALUES ('KH006', 'Nguyễn Văn A', TO_DATE('1990-05-15', 'YYYY-MM-DD'), 'Nam', 'nva@gmail.com', '090912345', 'Hà Nội', 'REF223');
-- --Test case 2: SĐT chứa ký tự hoặc chữ cái
-- INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
-- VALUES ('KH006', 'Trần Thị E', TO_DATE('1998-03-22', 'YYYY-MM-DD'), 'Nữ', 'tte@gmail.com', '09095557a7', 'Hải Phòng', 'REF223');
-- --Test case 3: Success
-- INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
-- VALUES ('KH006', 'Trần Thị E', TO_DATE('1998-03-22', 'YYYY-MM-DD'), 'Nữ', 'tte@gmail.com', '0909555797', 'Hải Phòng', 'REF222');
--

--  Email phải có định dạng hợp lệ (chứa ký tự '@' và '.').
CREATE OR REPLACE TRIGGER trg_check_email
BEFORE INSERT OR UPDATE ON CUSTOMER
FOR EACH ROW
BEGIN
  IF NOT REGEXP_LIKE(:NEW.EMAIL,'^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}') THEN 
    RAISE_APPLICATION_ERROR(-20003,'Email phải chứa ký tự "@" và "."');
  END IF;
END;
-- ^[A-Za-z0-9._%+-]: kiểm tra ký tự đầu có thuộc phạm vi các ký tự này không
-- +: cho phép lặp lại với các ký tự sau
-- \.: là dấu '.' vì trogn regexp_like, '.' có nghĩa là bất kỳ ký tự nào ngoại trừ ký tự xuống dòng
-- {2,} tối thiểu 2 ký tự, tối đa vô hạn

-- Ngày sinh không được lớn hơn ngày hiện tại.
CREATE OR REPLACE TRIGGER trg_check_birth_day
BEFORE INSERT OR UPDATE ON CUSTOMER
FOR EACH ROW
BEGIN
  IF :NEW.NGAYSINH > TRUNC(CURRENT_DATE) THEN 
    RAISE_APPLICATION_ERROR(-20004,'Ngày sinh không được lớn hon ngày hiện tại');
  END IF;
END;

-- Mỗi khách hàng chỉ có thể đăng ký một gói tập đang hoạt động tại một thời điểm.
CREATE OR REPLACE TRIGGER trg_check_active_subscription
BEFORE INSERT OR UPDATE ON SUBSCRIPTION
FOR EACH ROW
DECLARE
  CURSOR active_sub IS
  SELECT MaDK FROM SUBSCRIPTION s
  WHERE s.MAKH = :NEW.MAKH AND s.TINHTRANG = 'Active';
 v_active SUBSCRIPTION.MADK%TYPE; 
BEGIN
  OPEN active_sub;
  FETCH active_sub INTO v_active;
  IF active_sub%FOUND THEN 
    RAISE_APPLICATION_ERROR(-20005,'Mỗi khách hàng chỉ có thể đăng ký một gói tập đang hoạt động tại một thời điểm.');
  END IF;
  CLOSE active_sub;
END;

-- --
-- --Testcase 1: success
-- INSERT INTO SUBSCRIPTION (MaDK, MaKH, MaGoi, MaDV, NGAYDK, NgayHetHan, TinhTrang)
-- VALUES ('DK002', 'KH001', 'PK001', 'SV001', TO_DATE('2025-03-01', 'YYYY-MM-DD'), TO_DATE('2025-04-01', 'YYYY-MM-DD'), 'Active');
--
-- SELECT * FROM SUBSCRIPTION s;

-- Một khách hàng không thể đặt nhiều buổi tập với cùng một huấn luyện viên trong cùng một khung giờ.(*)
CREATE OR REPLACE TRIGGER trg_check_duplicate_schedule
BEFORE INSERT OR UPDATE ON SCHEDULE
FOR EACH ROW
DECLARE
  CURSOR duplicate_schedule IS
  SELECT MAKH FROM SCHEDULE s1
  WHERE s1.MAKH = :NEW.MAKH AND s1.CATAP = :NEW AND s1.NGAYTAP = :NEW.NGAYTAP;
 v_duplicate SCHEDULE.MALICH%TYPE; 
BEGIN
  OPEN duplicate_schedule ;
  FETCH duplicate_schedule  INTO v_duplicate;
  IF duplicate_schedule %FOUND THEN 
    RAISE_APPLICATION_ERROR(-20006,'Một khách hàng không thể đặt nhiều buổi tập với cùng một huấn luyện viên trong cùng một khung giờ.');
  END IF;
  CLOSE duplicate_schedule;
END;

--Mỗi khách hàng chỉ có thể tham gia tối đa 3 buổi tập mỗi ngày và 15 buổi tập mỗi tuần.(*)
CREATE OR REPLACE TRIGGER trg_check_workout_limit
BEFORE INSERT OR UPDATE ON SCHEDULE
DECLARE
COMPOUND TRIGGER
  v_daily_count NUMBER;
  v_weekly_count NUMBER;
BEFORE EACH ROW IS
BEGIN
  SELECT COUNT(*) INTO v_daily_count;
  FROM SCHEDULE S 
  WHERE S.MAKH = :NEW.MAKH AND S.NGAYTAP = :NEW.NGAYTAP
  IF V_DAILY_COUNT > 3 THEN 
    RAISE_APPLICATION_ERROR(-20007,'Mỗi khách hàng chỉ có thể tham gia tối đa 3 buổi tập mỗi ngày');
END BEFORE EACH ROW;

AFTER EACH ROW IS
BEGIN 
  SELECT COUNT(*) INTO v_weekly_count;
  FROM SCHEDULE s
  WHERE MaKH = :NEW.MaKH 
  AND NgayTap BETWEEN TRUNC(:NEW.NgayTap, 'IW') --TRUNC(DATE,'IW'): trả về ngày đầu tuần của tuần chứa DATE
  AND :NEW.NgayTap;
  IF V_DAILY_COUNT > 15 THEN 
    RAISE_APPLICATION_ERROR(-20008,'Mỗi khách hàng chỉ có thể tham gia tối đa 15 buổi tập mỗi tuần');
END AFTER EACH ROW;
END TRG_CHECK_WORKOUT_LIMIT;

-- Nếu khách hàng chưa thanh toán gói tập cũ, họ không thể đăng ký gói mới.
CREATE OR REPLACE TRIGGER trg_check_unpaid_package
BEFORE INSERT OR UPDATE ON SUBSCRIPTION
FOR EACH ROW
DECLARE
  CURSOR unpaid_pkg IS
  SELECT S.MADK FROM SUBSCRIPTION s
  WHERE S.MAKH = :NEW.MAKH AND S.TINHTRANG = 'Active'
  AND NOT EXISTS(
    SELECT 1
    FROM PAYMENT p
    WHERE P.MAKH = :NEW.MAKH
  );
  v_unpaid SUBSCRIPTION.MADK%TYPE;
BEGIN
  OPEN UNPAID_PKG;
  FETCH UNPAID_PKG INTO V_UNPAID;
  IF UNPAID_PKG%FOUND THEN
  RAISE_APPLICATION_ERROR(-20009,'Không thể đăng ký gói mới khi chưa thanh toán gói cũ.');
  END IF;
  CLOSE UNPAID_PKG;
END;


--Nếu khách hàng vắng mặt quá 7 ngày liên tiếp không có thông báo, hệ thống sẽ gửi cảnh báo.
CREATE OR REPLACE TRIGGER trg_notify_absence
AFTER INSERT OR UPDATE ON ATTENDANCE
FOR EACH ROW
DECLARE
    absence_count NUMBER;
BEGIN
    SELECT COUNT(*) 
    INTO absence_count
    FROM ATTENDANCE
    WHERE MaKH = :NEW.MaKH 
          AND TrangThai = 'Vắng mặt'
          AND NgayDiemDanh BETWEEN TRUNC(SYSDATE) - 7 AND TRUNC(SYSDATE);

    IF absence_count = 7 THEN
        INSERT INTO NOTIFICATION (MaKH, NoiDung, NgayGui)
        VALUES (:NEW.MaKH, 'Bạn đã vắng mặt quá 7 ngày, vui lòng quay lại tập luyện!', SYSDATE);
    END IF;
END;


-- Ràng buộc thời gian
-- Khách hàng có thể đặt lịch trước tối đa 30 ngày.
CREATE OR REPLACE TRIGGER trg_check_booking_limit
BEFORE INSERT OR UPDATE ON SCHEDULE
FOR EACH ROW
BEGIN
  IF :NEW.NGAYTAP > CURRENT_DATE + 30 THEN
    RAISE_APPLICATION_ERROR(-20011, 'Khách hàng không thể đặt lịch trước quá 30 ngày.');
  END IF;
END;

-- Nếu khách hàng đến muộn quá 15 phút, buổi tập sẽ bị hủy.(*)
CREATE OR REPLACE TRIGGER trg_cancel_late_booking
FOR UPDATE OR INSERT ON ATTENDANCE
COMPOUND TRIGGER

    -- Khai báo biến lưu danh sách khách hàng đến muộn
    TYPE MaKH_List IS TABLE OF ATTENDANCE.MaKH%TYPE;
    late_customers MaKH_List := MaKH_List();

    BEFORE STATEMENT IS
    BEGIN
        -- Xóa danh sách khách hàng đến muộn trước khi xử lý
        late_customers.DELETE;
    END BEFORE STATEMENT;

    AFTER EACH ROW IS
    BEGIN
        -- Kiểm tra khách có đến muộn hơn 15 phút không
        FOR rec IN (
            SELECT s.MaKH, s.NGAYTAP
            FROM SCHEDULE s
            WHERE s.MaKH = :NEW.MaKH
            AND :NEW.NGAYDIEMDANH > s.NGAYTAP + INTERVAL '15' MINUTE
            AND b.TrangThai != 'Đã hủy'
        ) LOOP
            -- Thêm khách vào danh sách đến muộn
            late_customers.EXTEND;
            late_customers(late_customers.LAST) := rec.MaKH;
        END LOOP;
    END AFTER EACH ROW;

    AFTER STATEMENT IS
    BEGIN
        -- Cập nhật trạng thái của các buổi tập bị hủy
        FOR i IN 1 .. late_customers.COUNT LOOP
            UPDATE BOOKING
            SET TrangThai = 'Đã hủy'
            WHERE MaKH = late_customers(i)
            AND TrangThai != 'Đã hủy';
        END LOOP;
    END AFTER STATEMENT;

END trg_cancel_late_booking;
/

-- Ràng buộc cho nhân viên và quản lý

-- Nhân viên phải có số điện thoại và email hợp lệ

CREATE OR REPLACE TRIGGER trg_check_staff_contact
BEFORE INSERT OR UPDATE ON STAFF
FOR EACH ROW
BEGIN
  -- Kiểm tra số điện thoại: phải có đúng 10 chữ số
  IF NOT REGEXP_LIKE(:NEW.SoDienThoaiNV, '^\d{10}$') THEN
    RAISE_APPLICATION_ERROR(-20031, 'Số điện thoại phải có đúng 10 chữ số.');
  END IF;

  -- Kiểm tra email: phải đúng định dạng email hợp lệ
  IF NOT REGEXP_LIKE(:NEW.EmailNV, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$') THEN
    RAISE_APPLICATION_ERROR(-20032, 'Email không hợp lệ. Vui lòng nhập đúng định dạng.');
  END IF;
END;
--
-- -- Testcase 1: success
-- DELETE FROM staff WHERE MANV IN ('NV005');
-- INSERT INTO STAFF (MaNV, HoTenNV, SoDienThoaiNV, EmailNV, ViTri, Luong)
-- VALUES ('NV005', 'Đặng Thị E', '0987654311', 'e1.dang@example.com', 'Lễ tân', 8000000);
-- -- Testcase 2: SDT không đủ 10 số
-- INSERT INTO STAFF (MaNV, HoTenNV, SoDienThoaiNV, EmailNV, ViTri, Luong)
-- VALUES ('NV006', 'Đặng Thị E', '098765411', 'e2.dang@example.com', 'Lễ tân', 8000000);
-- -- Testcase 3: Email không hợp lệ
-- INSERT INTO STAFF (MaNV, HoTenNV, SoDienThoaiNV, EmailNV, ViTri, Luong)
-- VALUES ('NV007', 'Đặng Thị E', '0987654111', 'e2.dangexample.com', 'Lễ tân', 8000000);

--Nhân viên không được làm việc cả ca sáng và tối trong cùng một ngày
--Thanh toán phải được hoàn tất trước khi bắt đầu gói tập

--Hoàn tiền 50% trong 7 ngày đầu, sau đó không hoàn phí.
CREATE OR REPLACE TRIGGER trg_refund_and_notification
AFTER INSERT OR UPDATE ON PAYMENT
DECLARE
  CURSOR c_payment IS
    SELECT p.MaKH, p.SoTien, p.NgayTT, s.NgayDK
    FROM PAYMENT p
    JOIN SUBSCRIPTION s ON p.MaKH = s.MaKH
    WHERE p.NgayTT >= s.NgayDK;  -- Chỉ xử lý giao dịch có Subscription hợp lệ

  v_days_diff NUMBER;
  v_refund_amount NUMBER(10,2);
BEGIN
  FOR rec IN c_payment LOOP
    -- Tính số ngày từ ngày đăng ký đến ngày thanh toán
    v_days_diff := TRUNC(rec.NgayTT - rec.NgayDK);

    -- Xác định số tiền hoàn lại
    IF v_days_diff <= 7 THEN
      v_refund_amount := rec.SoTien * 0.5;
    ELSE
      v_refund_amount := 0;
    END IF;

    -- Ghi nhận thông báo hoàn tiền
    INSERT INTO NOTIFICATION (MaTBao, MaKH, TieuDe, NoiDung, SoTienHoan, NgayGui)
    VALUES (
      'NT' || TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
      rec.MaKH,
      'Thông báo hoàn tiền',
      'Số tiền hoàn lại: ' || TO_CHAR(v_refund_amount) || ' VND',
      v_refund_amount,
      SYSDATE
    );
  END LOOP;
END;
/
--
-- -- Testcase 1: Thêm thành công
-- INSERT INTO SUBSCRIPTION (MaDK, MaKH, MaGoi, MaDV, NGAYDK, NgayHetHan, TinhTrang)
-- VALUES ('DK001', 'KH001', 'PK001', 'SV001', TO_DATE('2025-03-01', 'YYYY-MM-DD'), TO_DATE('2025-04-01', 'YYYY-MM-DD'), 'Active');
--
-- SELECT * FROM SUBSCRIPTION s;
-- SELECT * FROM NOTIFICATION n;
-- -- Thanh toán vào ngày 2025-03-05 (4 ngày sau khi đăng ký)
-- INSERT INTO PAYMENT (MaGD, MaKH, SoTien, PhuongThucTT, NgayTT)
-- VALUES ('PM001', 'KH001', 2000000, 'The', TO_DATE('2025-03-05', 'YYYY-MM-DD'));
--
-- -- Testcase 2: Khách thanh toán sau 7 ngày
-- INSERT INTO SUBSCRIPTION (MaDK, MaKH, MaGoi, MaDV, NGAYDK, NgayHetHan, TinhTrang)
-- VALUES ('DK002', 'KH002', 'PK002', 'SV002', TO_DATE('2025-03-05', 'YYYY-MM-DD'), TO_DATE('2025-08-05', 'YYYY-MM-DD'), 'Active');
--
-- -- Thanh toán vào ngày 2025-03-10 (10 ngày sau khi đăng ký)
-- INSERT INTO PAYMENT (MaGD, MaKH, SoTien, PhuongThucTT, NgayTT)
-- VALUES ('PM005', 'KH002', 1500000, 'Tien Mat', TO_DATE('2025-03-15', 'YYYY-MM-DD'));
--
-- SELECT * FROM PAYMENT p
-- DELETE FROM PAYMENT WHERE MAGD IN ('PM001','PM002');
--
-- -- Testcase 3: Nhiều khách hàng thanh toán cùng lúc
-- INSERT INTO PAYMENT (MaGD, MaKH, SoTien, PhuongThucTT, NgayTT)
-- VALUES
-- ('PM003', 'KH003', 2500000, 'Thẻ', TO_DATE('2025-03-05', 'YYYY-MM-DD')),
-- ('PM004', 'KH004', 1800000, 'Ví điện tử', TO_DATE('2025-03-06', 'YYYY-MM-DD'));
--Nếu khách hàng chưa gia hạn sau 3 ngày từ ngày hết hạn, tài khoản sẽ bị thông báo cảnh cáo
CREATE OR REPLACE TRIGGER trg_subscription_expiry
AFTER INSERT OR UPDATE ON SUBSCRIPTION
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    -- Kiểm tra nếu đã 3 ngày từ ngày hết hạn
    IF CURRENT_DATE - :NEW.NgayHetHan = 3 THEN
        -- Kiểm tra xem khách hàng có đăng ký mới chưa
        SELECT COUNT(*) INTO v_count
        FROM SUBSCRIPTION
        WHERE MaKH = :NEW.MaKH AND NGAYDK > :NEW.NgayHetHan;

        -- Nếu chưa có đăng ký mới, gửi thông báo
        IF v_count = 0 THEN
            INSERT INTO NOTIFICATION (MaKH, NoiDung, NgayGui)
            VALUES (:NEW.MaKH, 'Tài khoản của bạn chưa gia hạn sau 3 ngày, vui lòng kiểm tra!', CURRENT_DATE);
        END IF;
    END IF;
END;


--Lương PT (TongLuong) phải bằng LuongCoDinh + HoaHong  - TruLuong
CREATE OR REPLACE TRIGGER check_salary_calculation
BEFORE INSERT OR UPDATE ON SALARY
FOR EACH ROW
DECLARE
  v_calculated_salary DECIMAL(12,2);
BEGIN
  IF :NEW.MaPT IS NOT NULL THEN
    v_calculated_salary := :NEW.LuongCoDinh + NVL(:NEW.HoaHong, 0) - NVL(:NEW.TruLuong, 0);

    IF :NEW.TongLuong != v_calculated_salary THEN
      RAISE_APPLICATION_ERROR(-20001,'Tổng lương phải bằng Lương cố định + Hoa hồng - Trừ lương.');
    END IF;
  END IF;
END;

--Lương NV (TongLuong) phải bằng LuongCoDinh + Thuong  - TruLuong
CREATE OR REPLACE TRIGGER check_salary_nv_calculation
BEFORE INSERT OR UPDATE ON SALARY
FOR EACH ROW
DECLARE
  v_calculated_salary DECIMAL(12,2);
BEGIN
  IF :NEW.MaNV IS NOT NULL THEN
    v_calculated_salary := :NEW.LuongCoDinh + NVL(:NEW.Thuong, 0) - NVL(:NEW.TruLuong, 0);

    IF :NEW.TongLuong != v_calculated_salary THEN
      RAISE_APPLICATION_ERROR(-20001,'Tổng lương phải bằng Lương cố định + Thưởng - Trừ lương.');
    END IF;
  END IF;
END;


--Khách VIP chỉ có thể là thành viên của một hạng mức tại một thời điểm
CREATE OR REPLACE TRIGGER trg_check_unique_membership
BEFORE INSERT OR UPDATE ON MEMBERSHIP
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM MEMBERSHIP
    WHERE MaKH = :NEW.MaKH
    AND LOAIHV <> :NEW.LOAIHV;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Khách hàng đã có hạng mức thành viên khác!');
    END IF;
END;

--Nếu khách hàng hết hạn VIP, họ sẽ trở thành thành viên thường
CREATE OR REPLACE TRIGGER trg_update_membership_status
BEFORE INSERT OR UPDATE ON MEMBERSHIP
FOR EACH ROW
BEGIN
    IF :New.LOAIHV = 'VIP' AND :NEW.NgayKetThuc < CURRENT_DATE THEN
        :NEW.LoaiHV := 'Standard';
    END IF;
END;

--Khách loại VIP được miễn phí một số dịch vụ bổ sung
CREATE OR REPLACE TRIGGER trg_vip_free_service
BEFORE INSERT OR UPDATE ON SUBSCRIPTION
FOR EACH ROW
DECLARE
    v_LoaiHV VARCHAR2(20);
BEGIN
    -- Lấy loại hội viên (hạng) từ bảng MEMBERSHIP
    SELECT LoaiHV 
    INTO v_LoaiHV
    FROM MEMBERSHIP
    WHERE MaKH = :NEW.MaKH;

    -- Nếu khách hàng có hạng VIP thì cập nhật phí dịch vụ về 0
    IF v_LoaiHV = 'VIP' THEN
        UPDATE SERVICE
        SET GiaDV = 0
        WHERE MaDV = :NEW.MaDV;
    END IF;
END;


--Khách VIP có quyền tạm ngưng gói tập nhưng phải báo trước
CREATE OR REPLACE TRIGGER trg_vip_pause_membership
AFTER INSERT ON NOTIFICATION
FOR EACH ROW
DECLARE
    v_LoaiHV VARCHAR2(20);
BEGIN
    -- Lấy loại hội viên của khách từ bảng MEMBERSHIP
    SELECT LoaiHV INTO v_LoaiHV
    FROM MEMBERSHIP
    WHERE MaKH = :NEW.MaKH;

    -- Nếu là VIP và yêu cầu đúng nội dung thì cập nhật trạng thái gói tập
    IF v_LoaiHV = 'VIP' AND :NEW.NoiDung = 'Yêu cầu tạm ngừng' THEN
        UPDATE MEMBERSHIP
        SET TinhTrang = 'Pause'
        WHERE MaKH = :NEW.MaKH;
    END IF;
END;


--Không thể đặt lịch trong quá khứ
CREATE OR REPLACE TRIGGER trg_prevent_past_booking
BEFORE INSERT OR UPDATE ON BOOKING
FOR EACH ROW
BEGIN
    IF :NEW.NGAYDAT < TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(-20001, 'Không thể đặt lịch trong quá khứ!');
    END IF;
END;

--Không thể đặt lịch nếu gói tập đã hết hạn
CREATE OR REPLACE TRIGGER trg_prevent_booking_expired_package
BEFORE INSERT OR UPDATE ON BOOKING
FOR EACH ROW
DECLARE
    v_NgayHetHan DATE;
BEGIN
    -- Lấy ngày hết hạn gói tập của khách
    SELECT NgayHetHan 
    INTO v_NgayHetHan
    FROM SUBSCRIPTION
    WHERE MaKH = :NEW.MaKH;

    IF :NEW.NGAYDAT > v_NgayHetHan THEN
        RAISE_APPLICATION_ERROR(-20002, 'Không thể đặt lịch vì gói tập đã hết hạn!');
    END IF;
END;

--Nếu khách hàng hủy buổi tập trong vòng 1 giờ trước khi bắt đầu, hệ thống sẽ thông báo vi phạm(*)
CREATE OR REPLACE TRIGGER trg_late_cancel_notification
AFTER UPDATE OF TrangThai ON BOOKING
FOR EACH ROW
DECLARE
    v_ThoiGianConLai NUMBER;
BEGIN
    -- Tính số giờ còn lại giữa thời gian hiện tại và thời gian bắt đầu
    v_ThoiGianConLai := (EXTRACT(HOUR FROM (NEW.ThoiGianBatDau - SYSTIMESTAMP)) 
                        + EXTRACT(MINUTE FROM (NEW.ThoiGianBatDau - SYSTIMESTAMP)) / 60);

    -- Nếu trạng thái cập nhật là "Hủy" và còn dưới 1 giờ
    IF :NEW.TrangThai = 'Hủy' AND v_ThoiGianConLai <= 1 AND v_ThoiGianConLai > 0 THEN
        INSERT INTO NOTIFICATION (MaTBao, MaKH, TieuDe, NoiDung, NgayGui)
        VALUES (
            'NT' || TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),  -- Tạo mã thông báo
            :NEW.MaKH,
            'Thông báo vi phạm',
            'Vi phạm hủy muộn',
            SYSDATE
        );
    END IF;
END;

--Không thể đặt lịch nếu khu vực tập đã đạt giới hạn số lượng
CREATE OR REPLACE TRIGGER trg_check_workout_capacity
FOR INSERT OR UPDATE ON BOOKING
COMPOUND TRIGGER

    -- Khai báo kiểu dữ liệu để lưu danh sách các booking trong giao dịch
    TYPE t_booking_record IS RECORD (
        MaKhuVuc VARCHAR2(10),
        NgayDat DATE
    );

    TYPE t_booking_list IS TABLE OF t_booking_record;
    g_booking_list t_booking_list := t_booking_list();

    BEFORE EACH ROW IS
    BEGIN
        -- Lưu dữ liệu từng booking vào danh sách trước khi thực hiện lệnh INSERT/UPDATE
        g_booking_list.EXTEND;
        g_booking_list(g_booking_list.LAST).MaKhuVuc := :NEW.MaKhuVuc;
        g_booking_list(g_booking_list.LAST).NgayDat := :NEW.NgayDat;
    END BEFORE EACH ROW;

    AFTER STATEMENT IS
        v_MaxCapacity NUMBER;
        v_CurrentCount NUMBER;
    BEGIN
        FOR i IN 1 .. g_booking_list.COUNT LOOP
            -- Lấy sức chứa tối đa của khu vực tập
            SELECT SucChuaToiDa
            INTO v_MaxCapacity
            FROM WORKOUT_AREA
            WHERE MaKhuVuc = g_booking_list(i).MaKhuVuc;

            -- Đếm số lượng booking đã có tại khu vực và ngày đó
            SELECT COUNT(*)
            INTO v_CurrentCount
            FROM BOOKING
            WHERE MaKhuVuc = g_booking_list(i).MaKhuVuc
              AND NgayDat = g_booking_list(i).NgayDat;

            -- Nếu vượt quá giới hạn, báo lỗi
            IF v_CurrentCount > v_MaxCapacity THEN
                RAISE_APPLICATION_ERROR(-20003, 
                    'Không thể đặt lịch: Khu vực ' || g_booking_list(i).MaKhuVuc || 
                    ' đã đạt giới hạn vào ngày ' || 
                    TO_CHAR(g_booking_list(i).NgayDat, 'DD/MM/YYYY')
                );
            END IF;
        END LOOP;
    END AFTER STATEMENT;

END trg_check_workout_capacity;

--
-- -- Testcase 1: Success
-- DELETE FROM booking WHERE MaKhuVuc = 'KV001';
-- INSERT INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc)
-- VALUES ('BK101', 'KH001', 'PK001', 'SV001', TO_DATE('2025-04-10', 'YYYY-MM-DD'), 'Đã xác nhận', 'KV001');
--
-- SELECT * FROM BOOKING b
-- -- Testcase 2: KV001 đạt giới hạn
-- BEGIN
--     FOR i IN 1..30 LOOP
--         INSERT INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc)
--         VALUES ('BK' || (100 + i), 'KH001', 'PK001', 'SV001', TO_DATE('2025-04-10', 'YYYY-MM-DD'), 'Đã xác nhận', 'KV001');
--     END LOOP;
-- END;
-- -- Thử đặt booking thứ 31
-- INSERT INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc)
-- VALUES ('BK131', 'KH002', 'PK001', 'SV001', TO_DATE('2025-04-10', 'YYYY-MM-DD'), 'Chờ xử lý', 'KV001');


--Chỉ khách hàng có gói tập phù hợp mới có thể đặt lịch tại khu vực đó
ALTER TRIGGER TRG_CHECK_BOOKING_SUBSCRIPTION DISABLE;
CREATE OR REPLACE TRIGGER trg_check_booking_subscription
FOR INSERT OR UPDATE ON BOOKING
COMPOUND TRIGGER

  TYPE BookingData IS RECORD (
    MaKH BOOKING.MaKH%TYPE,
    MaGoi BOOKING.MaGoi%TYPE,
    MaKhuVuc BOOKING.MaKhuVuc%TYPE
  );
  TYPE BookingList IS TABLE OF BookingData;
  booking_list BookingList := BookingList();

  BEFORE EACH ROW IS
  BEGIN
    -- Lưu dữ liệu của từng dòng vào bảng tạm để kiểm tra sau
    booking_list.EXTEND;
    booking_list(booking_list.LAST) := BookingData(:NEW.MaKH, :NEW.MaGoi, :NEW.MaKhuVuc);
  END BEFORE EACH ROW;

  AFTER STATEMENT IS
  BEGIN
    -- Kiểm tra toàn bộ các dòng sau khi INSERT/UPDATE hoàn tất
    FOR i IN 1 .. booking_list.COUNT LOOP
      DECLARE
        v_count NUMBER;
      BEGIN
        -- Kiểm tra xem khách hàng có gói tập hợp lệ không
        SELECT COUNT(*)
        INTO v_count
        FROM SUBSCRIPTION S
        WHERE S.MaKH = booking_list(i).MaKH
          AND S.MaGoi = booking_list(i).MaGoi  -- Phải trùng với gói họ đặt
          AND S.TinhTrang = 'Active'  -- Chỉ chấp nhận gói đang hoạt động
          AND S.MaGoi IN (SELECT MaGoi FROM WORKOUT_AREA WHERE MaKhuVuc = booking_list(i).MaKhuVuc);

        -- Nếu không có gói hợp lệ, chặn toàn bộ giao dịch
        IF v_count = 0 THEN
          RAISE_APPLICATION_ERROR(-20002, 'Khách hàng không có gói tập hợp lệ để đặt lịch tại khu vực này.');
        END IF;
      END;
    END LOOP;
  END AFTER STATEMENT;

END trg_check_booking_subscription;
/

--
-- --Test case 1: Success
--
-- INSERT INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc)
-- VALUES ('BK101', 'KH001', 'PK001', 'SV001', TO_DATE('2025-04-10', 'YYYY-MM-DD'), 'Đã xác nhận', 'KV001');
--
-- SELECT * FROM BOOKING b
--
--
-- --Test case 2: Khách có gói tập không phù hợp
-- INSERT INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc)
-- VALUES ('BK102', 'KH003', 'PK001', 'SV001', TO_DATE('2025-03-10', 'YYYY-MM-DD'), 'Chờ xử lý', 'KV001');
--
-- SELECT * FROM SUBSCRIPTION s
--
-- DELETE FROM BOOKING WHERE MABOOKING = 'BK102';
--
-- --Test case 3: Insert hàng loạt nhưng có khách không đki gói tập hợp lệ
-- INSERT ALL
--     INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc) VALUES ('BK104', 'KH001', 'PK001', 'SV001', TO_DATE('2025-03-10', 'YYYY-MM-DD'), 'Chờ xử lý', 'KV001')
--     INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc) VALUES ('BK105', 'KH003', 'PK002', 'SV002', TO_DATE('2025-03-11', 'YYYY-MM-DD'), 'Chờ xử lý', 'KV002')
--     INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc) VALUES ('BK106', 'KH005', 'PK003', 'SV003', TO_DATE('2025-03-12', 'YYYY-MM-DD'), 'Chờ xử lý', 'KV003')
-- SELECT * FROM DUAL;
--
-- INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
-- VALUES ('KH005', 'Phạm Thị E', TO_DATE('1998-03-22', 'YYYY-MM-DD'), 'Nữ', 'ptd@gmail.com', '0909555666', 'Hải Phòng', 'REF013');
--
-- SELECT * FROM CUSTOMER c;
--
-- --Test case 4: Insert hàng loạt thành công
-- INSERT ALL
--     INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc)
--     VALUES ('BK201', 'KH001', 'PK001', 'SV001', TO_DATE('2025-03-12', 'YYYY-MM-DD'), 'Chờ xử lý', 'KV001')
--
--     INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc)
--     VALUES ('BK202', 'KH002', 'PK002', 'SV002', TO_DATE('2025-03-13', 'YYYY-MM-DD'), 'Chờ xử lý', 'KV002')
--
--     INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc)
--     VALUES ('BK203', 'KH003', 'PK003', 'SV003', TO_DATE('2025-03-14', 'YYYY-MM-DD'), 'Chờ xử lý', 'KV003')
--
--     INTO BOOKING (MaBooking, MaKH, MaGoi, MaDV, NgayDat, TrangThai, MaKhuVuc)
--     VALUES ('BK204', 'KH004', 'PK004', 'SV004', TO_DATE('2025-03-15', 'YYYY-MM-DD'), 'Chờ xử lý', 'KV004')
-- SELECT * FROM DUAL;
--
-- SELECT * FROM BOOKING b;
-- begin
-- FOR i IN 1..4 LOOP
-- DELETE FROM BOOKING WHERE
-- MABOOKING = 'BK20' || i;
-- END LOOP;
-- END;