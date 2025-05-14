--Tạo Schema
alter session set "_ORACLE_SCRIPT"=true;
CREATE USER QLYPHONGGYM IDENTIFIED BY pass123;
GRANT CONNECT, RESOURCE, DBA TO QLYPHONGGYM;


--Sử dụng Schema
ALTER SESSION SET CURRENT_SCHEMA = QLYPHONGGYM;

-- Bảng CUSTOMER
CREATE TABLE CUSTOMER (
    MaKH VARCHAR2(10) PRIMARY KEY,
    HoTen VARCHAR2(50) NOT NULL,
    NgaySinh DATE,
    GioiTinh VARCHAR2(10),
    Email VARCHAR2(50) NOT NULL,
    SoDienThoai VARCHAR2(15),
    DiaChi VARCHAR2(100),
    ReferralCode VARCHAR2(20) UNIQUE
);
select * from CUSTOMER;
INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
VALUES ('KH001', 'Nguyen Van A', TO_DATE('1990-05-15', 'YYYY-MM-DD'), 'Nam', 'nguyenvana@example.com', '0987654321', '123 Nguyen Trai, Hanoi', 'REF001');

INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
VALUES ('KH002', 'Tran Thi B', TO_DATE('1992-08-20', 'YYYY-MM-DD'), 'Nu', 'tranthib@example.com', '0912345678', '456 Le Loi, HCMC', 'REF002');

-- Bảng PACKAGE
CREATE TABLE PACKAGE (
    MaGoi VARCHAR2(10) PRIMARY KEY,
    TenGoi VARCHAR2(50) NOT NULL,
    LoaiGoi VARCHAR2(20) CHECK (LoaiGoi IN ('Cardio', 'Gym', 'Yoga', 'Zumba', 'Bơi')),
    GiaTien NUMBER(10,2) NOT NULL,
    ThoiHan NUMBER(3) NOT NULL
);

CREATE TABLE WORKOUT_AREA (
    MaKhuVuc VARCHAR2(10) PRIMARY KEY,
    TenKhuVuc VARCHAR2(50) NOT NULL,
    SucChuaToiDa NUMBER(3) NOT NULL
);

-- Bảng SCHEDULE
CREATE TABLE SCHEDULE (
    MaLich VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    MaGoi VARCHAR2(10),
    MaPT VARCHAR2(10),
    NgayTap DATE NOT NULL,
    MaCaTap VARCHAR2(10),
    CONSTRAINT FK_SCHEDULE_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH),
    CONSTRAINT FK_SCHEDULE_PACKAGE FOREIGN KEY (MaGoi) REFERENCES PACKAGE(MaGoi),
    CONSTRAINT FK_SCHEDULE_PT FOREIGN KEY (MaPT) REFERENCES PT(MaPT),
    CONSTRAINT FK_SCHEDULE_SHIFT FOREIGN KEY (MaCaTap) REFERENCES SHIFT(MaCaTap)
);

-- Bảng PT
CREATE TABLE PT (
    MaPT VARCHAR2(10) PRIMARY KEY,
    HoTenPT VARCHAR2(50) NOT NULL,
    ChuyenMon VARCHAR2(50),
    SoDienThoaiPT VARCHAR2(15) UNIQUE,
    EmailPT VARCHAR2(50) UNIQUE
);

-- Bảng STAFF
CREATE TABLE STAFF (
    MaNV VARCHAR2(10) PRIMARY KEY,
    HoTenNV VARCHAR2(50) NOT NULL,
    ViTri VARCHAR2(30) NOT NULL,
    SoDienThoaiNV VARCHAR2(15) UNIQUE,
    EmailNV VARCHAR2(50) UNIQUE,
    LichLamViec VARCHAR2(50),
    Ca VARCHAR2(10) CHECK (Ca IN ('Sáng', 'Tối'))
);

-- Bảng PAYMENT
CREATE TABLE PAYMENT (
    MaGD VARCHAR2(15) PRIMARY KEY,
    MaKH VARCHAR2(10),
    SoTien NUMBER(10,2) NOT NULL,
    PhuongThucTT VARCHAR2(20) CHECK (PhuongThucTT IN ('Tiền Mặt', 'Thẻ', 'Ví Điện Tử')),
    NgayTT DATE NOT NULL,
    MaGoi VARCHAR2(10),
    MaDV VARCHAR2(10),
    CONSTRAINT FK_PAYMENT_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH),
    CONSTRAINT FK_PAYMENT_PACKAGE FOREIGN KEY (MaGoi) REFERENCES PACKAGE(MaGoi),
    CONSTRAINT FK_PAYMENT_SERVICE FOREIGN KEY (MaDV) REFERENCES SERVICE(MaDV)
);

-- Bảng REWARD (Chương trình ưu đãi & tích điểm)
CREATE TABLE REWARD (
    MaUuDai VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    DiemTichLuy NUMBER(5) NOT NULL,
    MoTaUuDai VARCHAR2(100) NOT NULL,
    CONSTRAINT FK_REWARD_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH)
);

-- Bảng ATTENDANCE (Theo dõi điểm danh)
CREATE TABLE ATTENDANCE (
    MaDDKH VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    NgayDiemDanh DATE NOT NULL,
    TGDiemDanh TIMESTAMP NOT NULL,
    TrangThai VARCHAR2(20) CHECK (TrangThai IN ('Đã điểm danh', 'Vắng mặt')),
    CONSTRAINT FK_ATTENDANCE_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH)
);

-- Bảng EQUIPMENT (Thiết bị tập luyện)
CREATE TABLE EQUIPMENT (
    MaTB VARCHAR2(10) PRIMARY KEY,
    TenTB VARCHAR2(50) NOT NULL,
    TinhTrang VARCHAR2(20) CHECK (TinhTrang IN ('Tốt', 'Cần bảo trì', 'Hỏng')),
    MaKhuVuc VARCHAR2(10),
    CONSTRAINT FK_EQUIPMENT_WORKOUT_AREA FOREIGN KEY (MaKhuVuc) REFERENCES WORKOUT_AREA(MaKhuVuc)
);

-- Bảng CLASS (Lớp học nhóm)
CREATE TABLE CLASS (
    MaLop VARCHAR2(10) PRIMARY KEY,
    TenLop VARCHAR2(50) NOT NULL,
    MaPT VARCHAR2(10),
    SoLuongToiDa NUMBER(3) NOT NULL,
    CONSTRAINT FK_CLASS_PT FOREIGN KEY (MaPT) REFERENCES PT(MaPT)
);

-- Bảng SERVICE (Dịch vụ bổ sung)
CREATE TABLE SERVICE (
    MaDV VARCHAR2(10) PRIMARY KEY,
    TenDV VARCHAR2(50) NOT NULL,
    GiaDV NUMBER(10,2) NOT NULL,
    MoTaDV VARCHAR2(100)
);

-- Bảng SUBSCRIPTION (Đăng ký dịch vụ)
CREATE TABLE SUBSCRIPTION (
    MaDK VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    MaDV VARCHAR2(10),
    NgayDK DATE NOT NULL,
    NgayHetHan DATE NOT NULL,
    TinhTrang VARCHAR2(20) CHECK (TinhTrang IN ('Active', 'Expired')),
    MaGoi VARCHAR2(10),
    CONSTRAINT FK_SUBSCRIPTION_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH),
    CONSTRAINT FK_SUBSCRIPTION_SERVICE FOREIGN KEY (MaDV) REFERENCES SERVICE(MaDV),
    CONSTRAINT FK_SUBSCRIPTION_PACKAGE FOREIGN KEY (MaGoi) REFERENCES PACKAGE(MaGoi)
);

-- Bảng MEMBERSHIP (Hội viên VIP)
CREATE TABLE MEMBERSHIP (
    MaHV VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    LoaiHV VARCHAR2(20) CHECK (LoaiHV IN ('Standard', 'Silver', 'Gold', 'Platinum')),
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE,
    TinhTrang VARCHAR2(20) CHECK (TinhTrang IN ('Active', 'Pause')),
    CONSTRAINT FK_MEMBERSHIP_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH)
);

-- Bảng DISCOUNT (Mã giảm giá)
CREATE TABLE DISCOUNT (
    MaGiamGia VARCHAR2(10) PRIMARY KEY,
    TenMa VARCHAR2(50) NOT NULL,
    PhanTram NUMBER(3) CHECK (PhanTram BETWEEN 0 AND 100),
    HanSuDung DATE NOT NULL,
    DieuKien VARCHAR2(100)
);

-- Bảng BOOKING (Đặt lịch dịch vụ)
CREATE TABLE BOOKING (
    MaBooking VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    MaDV VARCHAR2(10),
    NgayDat DATE NOT NULL,
    TrangThai VARCHAR2(20) CHECK (TrangThai IN ('Đã xác nhận', 'Chờ xử lý', 'Đã hủy')),
    MaGoi VARCHAR2(10),
    ThoiGianHuy TIMESTAMP,
    CONSTRAINT FK_BOOKING_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH),
    CONSTRAINT FK_BOOKING_SERVICE FOREIGN KEY (MaDV) REFERENCES SERVICE(MaDV),
    CONSTRAINT FK_BOOKING_PACKAGE FOREIGN KEY (MaGoi) REFERENCES PACKAGE(MaGoi)
);

-- Bảng NOTIFICATION (Thông báo đến khách hàng)
CREATE TABLE NOTIFICATION (
    MaTBao VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    TieuDe VARCHAR2(30) NOT NULL,
    NoiDung VARCHAR2(255) NOT NULL,
    NgayGui DATE NOT NULL,
    SoTienHoan DECIMAL(12,2),
    CONSTRAINT FK_NOTIFICATION_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH)
);

-- Bảng FEEDBACK (Phản hồi của khách hàng)
CREATE TABLE FEEDBACK (
    MaFB VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    NoiDung VARCHAR2(255) NOT NULL,
    NgayGui DATE NOT NULL,
    DanhGia NUMBER(1) CHECK (DanhGia BETWEEN 1 AND 5),
    CONSTRAINT FK_FEEDBACK_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH)
);

-- Bảng MAINTENANCE (Bảo trì thiết bị)
CREATE TABLE MAINTENANCE (
    MaBT VARCHAR2(10) PRIMARY KEY,
    MaTB VARCHAR2(10),
    NgayBaoTri DATE NOT NULL,
    TrangThaiBT VARCHAR2(20) CHECK (TrangThaiBT IN ('Đang xử lý', 'Hoàn thành')),
    MoTaBT VARCHAR2(255),
    MaNV VARCHAR2(10),
    CONSTRAINT FK_MAINTENANCE_EQUIPMENT FOREIGN KEY (MaTB) REFERENCES EQUIPMENT(MaTB),
    CONSTRAINT FK_MAINTENANCE_STAFF FOREIGN KEY (MaNV) REFERENCES STAFF(MaNV)
);

-- Bảng PROMOTION (Chương trình khuyến mãi thêm buổi tập)
CREATE TABLE PROMOTION (
    MaKM VARCHAR2(10) PRIMARY KEY,
    TenKM VARCHAR2(50) NOT NULL,
    MoTaKM NUMBER(2) NOT NULL,  -- Số ngày được tặng thêm
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL
);

-- Bảng PT_ATTENDANCE (Điểm danh huấn luyện viên cá nhân)
CREATE TABLE PT_ATTENDANCE (
    MaDDPT VARCHAR2(10) PRIMARY KEY,
    MaPT VARCHAR2(10),
    NgayDiemDanh DATE NOT NULL,
    TrangThai VARCHAR2(20) CHECK (TrangThai IN ('Có mặt', 'Vắng mặt', 'Nghỉ phép')),
    GhiChu VARCHAR2(100),
    CONSTRAINT FK_PT_ATTENDANCE_PT FOREIGN KEY (MaPT) REFERENCES PT(MaPT)
);

-- Bảng STAFF_ATTENDANCE (Điểm danh nhân viên)
CREATE TABLE STAFF_ATTENDANCE (
    MaDDNV VARCHAR2(10) PRIMARY KEY,
    MaNV VARCHAR2(10),
    NgayDiemDanh DATE NOT NULL,
    TrangThai VARCHAR2(20) CHECK (TrangThai IN ('Có mặt', 'Vắng mặt', 'Nghỉ phép')),
    GhiChu VARCHAR2(100),
    CONSTRAINT FK_STAFF_ATTENDANCE FOREIGN KEY (MaNV) REFERENCES STAFF(MaNV)
);

-- Bảng CHALLENGE (Chương trình thử thách)
CREATE TABLE CHALLENGE (
    MaChallenge VARCHAR2(10) PRIMARY KEY,
    TenChallenge VARCHAR2(50) NOT NULL,
    MoTa VARCHAR2(1000) NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    DiemThuong NUMBER(5) NOT NULL
);

-- Bảng CHALLENGE_PARTICIPATION (Tham gia thử thách)
CREATE TABLE CHALLENGE_PARTICIPATION (
    MaThamGia VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    MaChallenge VARCHAR2(10),
    TinhTrang VARCHAR2(20) CHECK (TinhTrang IN ('Đang tham gia', 'Hoàn thành', 'Không hoàn thành')),
    NgayHoanThanh DATE,
    CONSTRAINT FK_CHALLENGE_PARTICIPATION_CUSTOMER FOREIGN KEY (MaKH) REFERENCES CUSTOMER(MaKH),
    CONSTRAINT FK_CHALLENGE_PARTICIPATION_CHALLENGE FOREIGN KEY (MaChallenge) REFERENCES CHALLENGE(MaChallenge)
);

-- Bảng SALARY (Quản lý lương nhân viên & PT)
CREATE TABLE SALARY (
    MaLuong VARCHAR2(10) PRIMARY KEY,
    MaNV VARCHAR2(10),
    MaPT VARCHAR2(10),
    ThoiGianNhanLuong DATE NOT NULL,
    LuongCoDinh DECIMAL(12,2) NOT NULL,
    HoaHong DECIMAL(12,2) DEFAULT 0,  -- Chỉ áp dụng cho PT
    Thuong DECIMAL(12,2) DEFAULT 0,    -- Thưởng nếu đạt KPI
    TruLuong DECIMAL(12,2) DEFAULT 0,  -- Trừ lương nếu vi phạm
    TongLuong DECIMAL(12,2) NOT NULL,  -- Tổng lương thực nhận
    CONSTRAINT FK_SALARY_STAFF FOREIGN KEY (MaNV) REFERENCES STAFF(MaNV),
    CONSTRAINT FK_SALARY_PT FOREIGN KEY (MaPT) REFERENCES PT(MaPT)
);

-- Bảng SHIFT (Quản lý ca tập luyện)
CREATE TABLE SHIFT (
    MaCaTap VARCHAR2(10) PRIMARY KEY,
    GioBatDau VARCHAR2(5) CHECK (REGEXP_LIKE(GioBatDau, '^\d{2}:\d{2}$')),
    GioKetThuc VARCHAR2(5) CHECK (REGEXP_LIKE(GioKetThuc, '^\d{2}:\d{2}$')),
    MaKhuVuc VARCHAR2(10),
    CONSTRAINT FK_SHIFT_WORKOUT_AREA FOREIGN KEY (MaKhuVuc) REFERENCES WORKOUT_AREA(MaKhuVuc)
);


--------------------------------------------------------------Các Bảng Dùng Cho Login Và Phân Quyền----------------------------------------------------------------------------------------------
select * from ACCOUNT;
select * from CUSTOMER;
select * from ACCOUNT_ASSIGN_ROLE_GROUP;
select * from ROLE_GROUP;
insert into ROLE_GROUP(ROLE_GROUP_ID,name_role_group, created_at, updated_at, is_deleted)
values (3,'STAFF',current_timestamp,current_timestamp,0);
insert into ACCOUNT(ROLE_GROUP_ID,FULL_NAME,EMAIL,username, password_hash, status, created_at, updated_at, is_deleted)
values (2,'staff1','staff1@gmail.com','staff1','$2a$12$PHzj0FdE5dnJez2yWuDcGuO8IkwDLukSapOovJc6uUqtRmnPtWktC','ACTIVE',current_timestamp,current_timestamp,0);

insert into ACCOUNT(ROLE_GROUP_ID,FULL_NAME,EMAIL,username, password_hash, status, created_at, updated_at, is_deleted)
values (1,'adminuser','adminuser@gmail.com','adminuser','$2a$12$8BXSYs2S3HAQWOYAQ5eSzuVXLyOp7dQ86orVyfTgqG/pamE8dJU4q','ACTIVE',current_timestamp,current_timestamp,0);


--pass cho admin là 456
--pass cho staff là 000

-- 2. Tạo bảng ACCOUNT
CREATE TABLE ACCOUNT (
    ACCOUNT_ID    NUMBER(10)  GENERATED BY DEFAULT AS IDENTITY primary key,
    ROLE_GROUP_ID NUMBER(10),
    FULL_NAME     VARCHAR2(100) NOT NULL,
    EMAIL         VARCHAR2(150) NOT NULL UNIQUE,
    USERNAME      VARCHAR2(50)  NOT NULL UNIQUE,
    PASSWORD_HASH VARCHAR2(200) NOT NULL,
    STATUS        VARCHAR2(20)  DEFAULT 'ACTIVE',
    CREATED_AT    DATE          DEFAULT SYSDATE,
    UPDATED_AT    DATE          DEFAULT SYSDATE,
    IS_DELETED    NUMBER(1)     DEFAULT 0 CHECK (IS_DELETED IN (0, 1)),
    CONSTRAINT FK_ACCOUNT_ROLE_GROUP FOREIGN KEY (ROLE_GROUP_ID) REFERENCES ROLE_GROUP(ROLE_GROUP_ID)
);

/
select * from ROLE_GROUP;


-- 3. Tạo bảng FUNCTION
CREATE TABLE FUNCTION (
    FUNCTION_ID   NUMBER(10) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME_FUNCTION VARCHAR2(100) NOT NULL,
    CREATED_AT    DATE DEFAULT SYSDATE,
    UPDATED_AT    DATE DEFAULT SYSDATE,
    IS_DELETED    NUMBER(1) DEFAULT 0 CHECK (IS_DELETED IN (0,1))
);

-- 4. Tạo bảng ROLE
CREATE TABLE ROLE (
    ROLE_ID     NUMBER(10) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    FUNCTION_ID NUMBER(10) NOT NULL,
    ADD_PERM    NUMBER(1) DEFAULT 0 CHECK (ADD_PERM IN (0,1)),
    EDIT_PERM   NUMBER(1) DEFAULT 0 CHECK (EDIT_PERM IN (0,1)),
    DELETE_PERM NUMBER(1) DEFAULT 0 CHECK (DELETE_PERM IN (0,1)),
    DOWNLOAD_PERM NUMBER(1) DEFAULT 0 CHECK (DOWNLOAD_PERM IN (0,1)),
    VIEW_PERM   NUMBER(1) DEFAULT 0 CHECK (VIEW_PERM IN (0,1)),
    CREATED_AT  DATE DEFAULT SYSDATE,
    UPDATED_AT  DATE DEFAULT SYSDATE,
    IS_DELETED  NUMBER(1) DEFAULT 0 CHECK (IS_DELETED IN (0,1)),
    CONSTRAINT FK_ROLE_FUNCTION FOREIGN KEY (FUNCTION_ID) REFERENCES FUNCTION(FUNCTION_ID)
);

-- 5. Tạo bảng ROLE_GROUP
CREATE TABLE ROLE_GROUP (
    ROLE_GROUP_ID NUMBER(10) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME_ROLE_GROUP VARCHAR2(100) NOT NULL,
    CREATED_AT DATE DEFAULT SYSDATE,
    UPDATED_AT DATE DEFAULT SYSDATE,
    IS_DELETED NUMBER(1) DEFAULT 0 CHECK (IS_DELETED IN (0,1))
);

-- 6. Tạo bảng ROLE_GROUP_ASSIGN_ROLE
CREATE TABLE ROLE_GROUP_ASSIGN_ROLE (
    ROLE_GROUP_ID NUMBER(10) NOT NULL,
    ROLE_ID       NUMBER(10) NOT NULL,
    CREATED_AT    DATE DEFAULT SYSDATE,
    UPDATED_AT    DATE DEFAULT SYSDATE,
    IS_DELETED    NUMBER(1) DEFAULT 0 CHECK (IS_DELETED IN (0,1)),
    CONSTRAINT PK_ROLE_GROUP_ASSIGN_ROLE PRIMARY KEY (ROLE_GROUP_ID, ROLE_ID),
    CONSTRAINT FK_ROLE_GROUP FOREIGN KEY (ROLE_GROUP_ID) REFERENCES ROLE_GROUP(ROLE_GROUP_ID),
    CONSTRAINT FK_ROLE FOREIGN KEY (ROLE_ID) REFERENCES ROLE(ROLE_ID)
);
-- 7. Tạo bảng ACCOUNT_ASSIGN_ROLE_GROUP
CREATE TABLE ACCOUNT_ASSIGN_ROLE_GROUP (
    ACCOUNT_ID    NUMBER(10) NOT NULL,
    ROLE_GROUP_ID NUMBER(10) NOT NULL,
    CREATED_AT    DATE DEFAULT SYSDATE,
    UPDATED_AT    DATE DEFAULT SYSDATE,
    IS_DELETED    NUMBER(1) DEFAULT 0 CHECK (IS_DELETED IN (0,1)),
    CONSTRAINT PK_ACCOUNT_ASSIGN_ROLE_GROUP PRIMARY KEY (ACCOUNT_ID, ROLE_GROUP_ID),
    CONSTRAINT FK_ACCOUNT FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID),
    CONSTRAINT FK_ROLE_GROUP_ASSIGN FOREIGN KEY (ROLE_GROUP_ID) REFERENCES ROLE_GROUP(ROLE_GROUP_ID)
);

-- 8. Tạo bảng ACCOUNT_ASSIGN_ROLE
CREATE TABLE ACCOUNT_ASSIGN_ROLE (
    ACCOUNT_ID  NUMBER(10) NOT NULL,
    ROLE_ID     NUMBER(10) NOT NULL,
    CREATED_AT  DATE DEFAULT SYSDATE,
    UPDATED_AT  DATE DEFAULT SYSDATE,
    IS_DELETED  NUMBER(1) DEFAULT 0 CHECK (IS_DELETED IN (0,1)),
    CONSTRAINT PK_ACCOUNT_ASSIGN_ROLE PRIMARY KEY (ACCOUNT_ID, ROLE_ID),
    CONSTRAINT FK_ACCOUNT_ROLE FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID),
    CONSTRAINT FK_ROLE_ASSIGN FOREIGN KEY (ROLE_ID) REFERENCES ROLE(ROLE_ID)
);

-- 9. Tạo bảng ACCOUNT_TOKEN
CREATE TABLE ACCOUNT_TOKEN (
    TOKEN_ID      NUMBER(20) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ACCOUNT_ID    NUMBER(10) NOT NULL,
    TOKEN_VALUE   VARCHAR2(500) UNIQUE NOT NULL,
    EXPIRES_AT    DATE NOT NULL,
    ISSUED_AT     DATE DEFAULT SYSDATE,
    IS_REVOKED    CHAR(1) DEFAULT 'N' CHECK (IS_REVOKED IN ('Y', 'N')),
    CONSTRAINT FK_TOKEN_ACCOUNT FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID)
);
--------------------------------------------------------------TRIGGER-----------------------------------------------------------------------------------


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

--Nếu khách hàng chưa thanh toán gói tập cũ, họ không thể đăng ký gói mới.
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


--Mỗi khách hàng chỉ có thể đăng ký một gói tập đang hoạt động tại một thời điểm.
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


--Nếu khách hàng hủy buổi tập trong vòng 1 giờ trước khi bắt đầu, hệ thống sẽ thông báo vi phạm
CREATE OR REPLACE TRIGGER trg_late_cancellation_violation
FOR UPDATE ON BOOKING
COMPOUND TRIGGER
    -- Biến toàn cục dùng chung cho tất cả các dòng trong giao dịch
    TYPE Violation_Record IS RECORD (
        MaKH    BOOKING.MaKH%TYPE,
        NoiDung NOTIFICATION.NoiDung%TYPE,
        NgayGui TIMESTAMP
    );
    TYPE Violation_Table IS TABLE OF Violation_Record;
    v_Violations Violation_Table := Violation_Table();

    v_CurrentTime TIMESTAMP;  -- Lưu giá trị thời gian hiện tại chỉ 1 lần

BEFORE STATEMENT IS
BEGIN
    -- Lấy thời gian hiện tại một lần thay vì gọi lại trong mỗi row
    v_CurrentTime := SYSDATE;
END BEFORE STATEMENT;

AFTER EACH ROW IS
BEGIN
    -- Kiểm tra nếu trạng thái mới là 'Hủy' và thời gian còn lại <= 1 giờ
    IF :NEW.TrangThai = 'Đã Hủy' AND (:NEW.NGAYDAT - v_CurrentTime) * 24 <= 1 THEN
        -- Lưu thông tin vào bộ nhớ thay vì INSERT trực tiếp (tránh nhiều lần ghi)
        v_Violations.EXTEND;
        v_Violations(v_Violations.LAST) := Violation_Record(:NEW.MaKH, 'Vi phạm hủy muộn', v_CurrentTime);
    END IF;
END AFTER EACH ROW;

AFTER STATEMENT IS
BEGIN
    -- Chèn tất cả thông báo vi phạm vào NOTIFICATION sau khi xử lý xong
    FORALL i IN 1 .. v_Violations.COUNT
        INSERT INTO NOTIFICATION (MaKH, NoiDung, NgayGui)
        VALUES (v_Violations(i).MaKH, v_Violations(i).NoiDung, v_Violations(i).NgayGui);
END AFTER STATEMENT;
END;



------------------------------------------------------------------------------FUNCTION------------------------------------------------------------------------------------------------

-- Tìm mã giảm giá có phần trăm cao nhất
CREATE OR REPLACE FUNCTION GetBestDiscount
RETURN NUMBER IS 
		v_MaxDiscount NUMBER;
BEGIN
		SELECT MAX(PhanTram) INTO v_MaxDiscount 
		FROM DISCOUNT 
		WHERE HanSuDung >= SYSDATE;
		RETURN v_MaxDisCount;
		
		EXCEPTION WHEN NO_DATA_FOUND THEN
		RETURN 0;
END;


-- Tính tổng doanh thu trong một ngày
CREATE OR REPLACE FUNCTION GetDailyRevenue(p_Date DATE)
RETURN NUMBER IS 
		v_TotalRevenue NUMBER;
BEGIN
		SELECT COALESCE(SUM(SoTien), 0) INTO v_TotalRevenue
		FROM PAYMENT 
		WHERE TRUNC(NgayTT) = TRUNC(p_Date);
		
		RETURN v_TotalRevenue;
END;


-- Lấy ngày check-in gần nhất của khách hàng
CREATE OR REPLACE FUNCTION GetLastCheckInDate (p_MaKH VARCHAR2)
RETURN DATE IS 
		v_LastCheckIn DATE;
BEGIN
		SELECT MAX(NgayDiemDanh) INTO v_LastCheckIn
		FROM ATTENDANCE 
		WHERE MaKH = p_MaKH;
		
		RETURN v_LastCheckIn;
		
		EXCEPTION WHEN NO_DATA_FOUND THEN 
		RETURN NULL;
END;


-- Kiểm tra thông tin gói tập
CREATE OR REPLACE FUNCTION BookPackage(p_MaGoi VARCHAR2)
RETURN VARCHAR2
IS
  v_info VARCHAR2(200);
BEGIN
  SELECT 'Gói: ' || P.TENGOI || ', Giá: ' || TO_CHAR(P.GIATIEN) || ', Thời hạn: ' || TO_CHAR(P.THOIHAN) || ' tháng' 
  INTO V_INFO
  FROM PACKAGE p
  WHERE P.MAGOI  = P_MAGOI;
  RETURN V_INFO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
  RETURN 'Không tìm thấy gói tập';
END;


-- Lấy hồ sơ khách hàng
CREATE OR REPLACE FUNCTION GetCustomerProfile(p_maKH VARCHAR2)
RETURN VARCHAR2
IS
  v_profile VARCHAR2(200);
BEGIN
    SELECT 'Họ tên: '||HoTen ||', Ngày sinh: '|| TO_CHAR(NgaySinh)||'. Giới tính: ' || GioiTinh ||', Email:  '|| Email
    ||', Số điện thoại: '||SoDienThoai||', Địa chỉ'|| DiaChi
    INTO v_profile
    FROM CUSTOMER
    WHERE MaKH = p_maKH;
  RETURN v_profile;
END;


-- Kiểm tra điểm tích lũy
CREATE OR REPLACE FUNCTION GetCustomerPoint (p_KhachHang NVARCHAR2)
RETURN NUMBER
IS
  v_point NUMBER;
BEGIN
  SELECT NVL(SUM(R.DIEMTICHLUY),0)
  INTO V_POINT
  FROM REWARD r
  WHERE r.MAKH = P_KHACHHANG;
  RETURN V_POINT;
END;



-- Tính phần trăm số buổi tập khách tham gia so với tổng buổi tập có lịch
CREATE OR REPLACE FUNCTION GetAttendancePercentage(p_maKH VARCHAR2)
RETURN NUMBER
IS
  v_attended NUMBER;
  v_total NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_attended
  FROM ATTENDANCE
  WHERE MaKH = p_maKH AND TrangThai = 'Đã điểm danh';
  
  SELECT COUNT(*) INTO v_total
  FROM SCHEDULE
  WHERE MaKH = p_maKH;
  
  IF v_total = 0 THEN
    RETURN 0;
  ELSE
    RETURN ROUND((v_attended / v_total) * 100, 2);
  END IF;
END;


-- Xem Thiết bị cần bảo trì
CREATE OR REPLACE FUNCTION Get_Maintenance_Equipment(
    p_MaKhuVuc VARCHAR2
) RETURN NUMBER AS
    v_Count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_Count
    FROM EQUIPMENT
    WHERE MaKhuVuc = p_MaKhuVuc
    AND TinhTrang = 'Hư hỏng';

    RETURN v_Count;
END;
/


-----------------------------------------------------------------------------------PROCEDURE--------------------------------------------------------------------------------------------


-- Hiển thị danh sách gói tập của khách hàng
CREATE OR REPLACE PROCEDURE ShowCustomerPackages(
  p_maKH IN VARCHAR2,
  p_cursor OUT SYS_REFCURSOR
)
IS
BEGIN
  OPEN p_cursor FOR
    SELECT p.MaGoi, p.TenGoi, p.LoaiGoi, p.GiaTien, p.ThoiHan, s.NgayDK, s.TinhTrang
    FROM SUBSCRIPTION s
    JOIN PACKAGE p ON s.MaGoi = p.MaGoi
    WHERE s.MaKH = p_maKH;
END;
/


-- Sửa thông tin gói tập
CREATE OR REPLACE PROCEDURE UpdatePackageInfo(
  p_maGoi IN VARCHAR2,
  p_tenGoi IN VARCHAR2,
  p_loaiGoi IN VARCHAR2,
  p_giaTien IN NUMBER,
  p_thoiHan IN NUMBER
)
IS
BEGIN
  UPDATE PACKAGE
  SET TenGoi = p_tenGoi,
      LoaiGoi = p_loaiGoi,
      GiaTien = p_giaTien,
      ThoiHan = p_thoiHan
  WHERE MaGoi = p_maGoi;
  
  IF SQL%ROWCOUNT = 0 THEN
    DBMS_OUTPUT.PUT_LINE('Không tìm thấy gói tập có mã: ' || p_maGoi);
  ELSE
    DBMS_OUTPUT.PUT_LINE('Đã cập nhật gói tập: ' || p_maGoi);
  END IF;
  
  COMMIT;
END;


-- Thêm nhân viên
CREATE OR REPLACE PROCEDURE AddStaff (
		p_MaNv IN VARCHAR2,
		p_HoTenNV IN VARCHAR2,
		p_ViTri IN VARCHAR2,
		p_LichLamViec IN VARCHAR2,
	  p_SoDienThoaiNV IN VARCHAR2, 
	  p_EmailNV IN VARCHAR2,
	  p_Luong IN NUMBER,
	  p_Ca IN VARCHAR2
) IS
BEGIN
		INSERT INTO STAFF(MaNV, HoTenNV, ViTri, LichLamViec, SoDienThoaiNV, EmailNV, Luong, Ca)
		VALUES (p_MaNv, p_HoTenNV, p_ViTri, p_LichLamViec, p_SoDienThoaiNV, p_EmailNV, p_Luong, p_Ca);
		
		COMMIT;
		DBMS_OUTPUT.PUT_LINE('Thêm nhân viên thành công: ' || p_HoTenNV);
		
EXCEPTION 
		WHEN OTHERS THEN
				 ROLLBACK;
				 DBMS_OUTPUT.PUT_LINE('Lỗi khi thêm nhân viên: ' || SQLERRM);
END;	



-- Thêm khách hàng
CREATE OR REPLACE PROCEDURE AddCustomer (
		p_MaKH IN VARCHAR2,
	  p_HoTen IN VARCHAR2,
	  p_NgaySinh IN DATE,
	  p_GioiTinh IN VARCHAR2,
	  p_Email IN VARCHAR2,
	  p_SoDienThoai IN VARCHAR2,
	  p_DiaChi IN VARCHAR2,
	  p_ReferralCode IN VARCHAR2
) IS
BEGIN
		INSERT INTO CUSTOMER(MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
		VALUES (p_MaKH, p_HoTen, p_NgaySinh, p_GioiTinh, p_Email, p_SoDienThoai, p_DiaChi, p_ReferralCode);
		
		COMMIT;
		DBMS_OUTPUT.PUT_LINE('Thêm khách hàng thành công: ' || p_HoTen);
		
EXCEPTION 
		WHEN OTHERS THEN
				 ROLLBACK;
				 DBMS_OUTPUT.PUT_LINE('Lỗi khi thêm khách hàng: ' || SQLERRM);
END;	



-- Thêm gói tập 
CREATE OR REPLACE PROCEDURE AddPackage (
		p_MaGoi IN VARCHAR2,
	  p_TenGoi IN VARCHAR2,
	  p_LoaiGoi IN VARCHAR2,
	  p_GiaTien IN NUMBER,
	  p_ThoiHan IN NUMBER
) IS
BEGIN
		INSERT INTO PACKAGE(MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan)
		VALUES (p_MaGoi, p_TenGoi, p_LoaiGoi, p_GiaTien, p_ThoiHan);
		
		COMMIT;
		DBMS_OUTPUT.PUT_LINE('Thêm gói tập thành công: ' || p_TenGoi);
		
EXCEPTION 
		WHEN OTHERS THEN
				 ROLLBACK;
				 DBMS_OUTPUT.PUT_LINE('Lỗi khi thêm gói tập: ' || SQLERRM);
END;						


-- Cập nhật thông tin khách hàng
CREATE OR REPLACE PROCEDURE UpdateCustomerInfor (
		p_MaKH IN VARCHAR2,
	  p_HoTen IN VARCHAR2,
	  p_NgaySinh IN DATE,
	  p_GioiTinh IN VARCHAR2,
	  p_Email IN VARCHAR2,
	  p_SoDienThoai IN VARCHAR2,
	  p_DiaChi IN VARCHAR2,
	  p_ReferralCode IN VARCHAR2
) IS
BEGIN
		UPDATE CUSTOMER
		SET HoTen = p_HoTen,
				NgaySinh = p_NgaySinh,
				GioiTinh = p_GioiTinh,
				Email = p_Email,
				SoDienThoai = p_SoDienThoai,
				DiaChi = p_DiaChi,
				ReferralCode = p_ReferralCode
		WHERE MaKH = p_MaKH;
		
		IF SQL%ROWCOUNT = 0 THEN
			 DBMS_OUTPUT.PUT_LINE('Không tìm thấy khách hàng: ' || p_MaKH);
		ELSE
			 COMMIT;
			 DBMS_OUTPUT.PUT_LINE('Cập nhật thông tin khách hàng thành công: ' || p_HoTen);
		END IF;
		
EXCEPTION 
		WHEN OTHERS THEN
				 ROLLBACK;
				 DBMS_OUTPUT.PUT_LINE('Lỗi khi cập nhật khách hàng: ' || SQLERRM);
END;		