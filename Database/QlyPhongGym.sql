--Tạo Schema
alter session set "_ORACLE_SCRIPT"=true;
CREATE USER QLYPHONGGYM IDENTIFIED BY pass123;
GRANT CONNECT, RESOURCE, DBA TO QLYPHONGGYM;


--Sử dụng Schema
ALTER SESSION SET CURRENT_SCHEMA = QLYPHONGGYM;
-- Bảng KHACHHANG
CREATE TABLE KHACHHANG (
    MaKH VARCHAR2(10) PRIMARY KEY,
    HoTen VARCHAR2(50) NOT NULL,
    NgaySinh DATE,
    GioiTinh VARCHAR2(10),
    Email VARCHAR2(50) NOT NULL,
    SoDienThoai VARCHAR2(15),
    DiaChi VARCHAR2(100),
    ReferralCode VARCHAR2(20) UNIQUE
);

-- Create NHANVIEN table
CREATE TABLE NHANVIEN (
    MaNV VARCHAR2(10) PRIMARY KEY,
    TenNV VARCHAR2(50) NOT NULL,
    NgaySinh DATE,
    GioiTinh VARCHAR2(10),
    Email VARCHAR2(30) NOT NULL,
    NgayVaoLam DATE,
    LoaiNV VARCHAR2(10) CHECK (LoaiNV IN ('Trainer', 'LeTan', 'QuanLy', 'PhongTap'))
);
select * from NHANVIEN;

INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('NV001', 'NHANVIEN001', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Nam', 'nhanvien001@example.com', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'LeTan');
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('QL001', 'QUANLY001', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Nam', 'quanly001@example.com', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'QuanLy');
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT001', 'TRAINER001', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Nam', 'trainer001@example.com', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'Trainer');


-- Create KHIEUNAI table
CREATE TABLE KHIEUNAI (
    MaKN VARCHAR2(10) PRIMARY KEY,
    MaKH VARCHAR2(10),
    NoiDung VARCHAR2(500),
    CONSTRAINT FK_KHIEUNAI_KHACHHANG FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH)
);

-- Create BOMON table
CREATE TABLE BOMON (
    MaBM VARCHAR2(10) PRIMARY KEY,
    TenBM VARCHAR2(20) NOT NULL
);

INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM01', 'Gym Fitness');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM02', 'Yoga');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM03', 'Zumba');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM04', 'Crossfit');


-- Create DICHVU table
CREATE TABLE DICHVU (
    MaDV VARCHAR2(10) PRIMARY KEY,
    TenDV VARCHAR2(50) NOT NULL,
    LoaiDV VARCHAR2(10) CHECK (LoaiDV IN ('TuDo', 'Lop', 'PT')),
    ThoiHan NUMBER,
    DonGia NUMBER,
    MaBM VARCHAR2(10),
    CONSTRAINT FK_DICHVU_BOMON FOREIGN KEY (MaBM) REFERENCES BOMON(MaBM)
);
select * from BOMON;
select * from DICHVU;
-- Dịch vụ tự do (khách tự tập)
INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM)
VALUES ('DV01', 'Tập Gym tự do - 1 tháng', 'TuDo', 30, 500000, 'BM01');

INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM)
VALUES ('DV02', 'Tập Gym tự do - 3 tháng', 'TuDo', 90, 1350000, 'BM01');

-- Dịch vụ lớp học
INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM)
VALUES ('DV03', 'Lớp Yoga', 'Lop', 30, 600000, 'BM02');

INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM)
VALUES ('DV04', 'Lớp Zumba', 'Lop', 30, 550000, 'BM03');

INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM)
VALUES ('DV05', 'Lớp Crossfit nâng cao', 'Lop', 30, 800000, 'BM04');

-- Dịch vụ huấn luyện viên cá nhân (PT)
INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM)
VALUES ('DV06', 'PT cá nhân - 10 buổi', 'PT', 10, 1500000, 'BM01');

INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM)
VALUES ('DV07', 'PT Yoga chuyên sâu', 'PT', 8, 1600000, 'BM02');


-- Create KHUVUC table
CREATE TABLE KHUVUC (
    MaKV VARCHAR2(10) PRIMARY KEY,
    TenKhuVuc VARCHAR2(20) NOT NULL,
    SucChuaToiDa NUMBER,
    TrangThai VARCHAR2(10) CHECK (TrangThai IN ('HoatDong', 'BaoTri')),
    TinhTrang VARCHAR2(10) CHECK (TinhTrang IN ('ChuaDay', 'DaDay')),
    MaBM VARCHAR2(10),
    CONSTRAINT FK_KHUVUC_BOMON FOREIGN KEY (MaBM) REFERENCES BOMON(MaBM)
);

-- Create CT_CHECKIN_OUT table
CREATE TABLE CT_CHECKIN_OUT (
    MaKH VARCHAR2(10),
    MaKV VARCHAR2(10),
    TG_IN DATE,
    TG_OUT DATE,
    CONSTRAINT PK_CT_CHECKIN_OUT PRIMARY KEY (MaKH, MaKV),
    CONSTRAINT FK_CHECKIN_OUT_KHACHHANG FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH),
    CONSTRAINT FK_CHECKIN_OUT_KHUVUC FOREIGN KEY (MaKV) REFERENCES KHUVUC(MaKV)
);

-- Create LOAITHIETBI table
CREATE TABLE LOAITHIETBI (
    MaLoaiTB VARCHAR2(10) PRIMARY KEY,
    TenLoaiTB VARCHAR2(20) NOT NULL,
    GiaNhap NUMBER,
    SL_Nhap NUMBER CHECK (SL_Nhap >= 0),
    SL_Kho NUMBER CHECK (SL_Kho >= 0),
    MucDich VARCHAR2(10) CHECK (MucDich IN ('ChoThue', 'LapDat'))
);

-- Create THIETBI table
CREATE TABLE THIETBI (
    MaTB VARCHAR2(10) PRIMARY KEY,
    TenTB VARCHAR2(20) NOT NULL,
    DVT VARCHAR2(10) CHECK (DVT IN ('Doi', 'Chiec', 'May')) NOT NULL,
    TinhTrang VARCHAR2(20) CHECK (TinhTrang IN ('Hu', 'DangBaoTri', 'DangSuDung')),
    DonGia NUMBER,
    MoTa VARCHAR2(100),
    MaLoaiTB VARCHAR2(10),
    MaKV VARCHAR2(10),
    CONSTRAINT FK_THIETBI_LOAITHIETBI FOREIGN KEY (MaLoaiTB) REFERENCES LOAITHIETBI(MaLoaiTB),
    CONSTRAINT FK_THIETBI_KHUVUC FOREIGN KEY (MaKV) REFERENCES KHUVUC(MaKV)
);

-- Create CT_CHUYENMON table
CREATE TABLE CT_CHUYENMON (
    MaNV VARCHAR2(10),
    MaBM VARCHAR2(10),
    GhiChu VARCHAR2(50),
    CONSTRAINT PK_CT_CHUYENMON PRIMARY KEY (MaNV, MaBM),
    CONSTRAINT FK_CHUYENMON_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT FK_CHUYENMON_BOMON FOREIGN KEY (MaBM) REFERENCES BOMON(MaBM)
);

-- Create LOP table
CREATE TABLE LOP (
    MaLop VARCHAR2(10) PRIMARY KEY,
    TenLop VARCHAR2(30) NOT NULL,
    MoTa VARCHAR2(100),
    SL_ToiDa NUMBER,
    TinhTrang VARCHAR2(10) CHECK (TinhTrang IN ('ChuaDay', 'DaDay')),
    NgayBD DATE,
    GhiChu VARCHAR2(50),
    MaBM VARCHAR2(10),
    MaNV VARCHAR2(10),
    CONSTRAINT FK_LOP_BOMON FOREIGN KEY (MaBM) REFERENCES BOMON(MaBM),
    CONSTRAINT FK_LOP_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV)
);
select * from BOMON;
select * from DICHVU;
select * from LOP;
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('L01', 'Lớp Sáng Gym', 'Lớp luyện tập buổi sáng cho người mới bắt đầu', 25, 'ChuaDay', TO_DATE('2025-06-01', 'YYYY-MM-DD'), 'Đăng ký sớm', 'BM01', 'PT001');

INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('L02', 'Yoga Cơ Bản', 'Lớp Yoga dành cho người mới bắt đầu', 20, 'DaDay', TO_DATE('2025-05-15', 'YYYY-MM-DD'), 'Lớp đã đầy học viên', 'BM02', 'PT001');

INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('L03', 'Zumba Cuối Tuần', 'Lớp nhảy Zumba vào cuối tuần', 30, 'ChuaDay', TO_DATE('2025-06-10', 'YYYY-MM-DD'), '', 'BM03', 'PT001');

INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('L04', 'Crossfit Nâng Cao', 'Dành cho học viên đã có nền tảng thể lực', 15, 'ChuaDay', TO_DATE('2025-07-01', 'YYYY-MM-DD'), 'Yêu cầu kiểm tra đầu vào', 'BM04', 'PT001');


-- Create VOUCHER table
CREATE TABLE VOUCHER (
    MaVC VARCHAR2(10) PRIMARY KEY,
    TenVC VARCHAR2(30) NOT NULL,
    GiaTri NUMBER NOT NULL,
    ThoiHan NUMBER
);

-- Create CHUONGTRINHUUDAI table
CREATE TABLE CHUONGTRINHUUDAI (
    MaCT VARCHAR2(10) PRIMARY KEY,
    TenCT VARCHAR2(50) NOT NULL,
    MoTa VARCHAR2(100),
    NgayBD DATE,
    NgayKT DATE,
    MaVC VARCHAR2(10),
    CONSTRAINT FK_CHUONGTRINHUUDAI_VOUCHER FOREIGN KEY (MaVC) REFERENCES VOUCHER(MaVC)
);

-- Create CT_THAMGIA table
CREATE TABLE CT_THAMGIA (
    MaKH VARCHAR2(10),
    MaCT VARCHAR2(10),
    NgayHT DATE,
    CONSTRAINT PK_CT_THAMGIA PRIMARY KEY (MaKH, MaCT),
    CONSTRAINT FK_THAMGIA_KHACHHANG FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH),
    CONSTRAINT FK_THAMGIA_CHUONGTRINH FOREIGN KEY (MaCT) REFERENCES CHUONGTRINHUUDAI(MaCT)
);


-- Create HOADON table
CREATE TABLE HOADON (
    MaHD VARCHAR2(10) PRIMARY KEY,
    TongTien NUMBER DEFAULT 0,
    NgayLap DATE,
    TrangThai VARCHAR2(50) CHECK (TrangThai IN ('ChuaThanhToan', 'DaThanhToan')),
    NgayTT DATE,
    MaKH VARCHAR2(10),
    CONSTRAINT FK_HOADON_KHACHHANG FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH)
);
ALTER TABLE HOADON MODIFY NgayLap DATE DEFAULT SYSDATE;

-- Create KH_VC table
CREATE TABLE KH_VC (
    MaCap VARCHAR2(10) PRIMARY KEY,
    NgayNhan DATE,
    NgayHetHan DATE,
    MaKH VARCHAR2(10),
    MaVC VARCHAR2(10),
    MaHD VARCHAR2(10),
    CONSTRAINT FK_KH_VC_KHACHHANG FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH),
    CONSTRAINT FK_KH_VC_VOUCHER FOREIGN KEY (MaVC) REFERENCES VOUCHER(MaVC),
    CONSTRAINT FK_KH_VC_HOADON FOREIGN KEY (MaHD) REFERENCES HOADON(MaHD)
);

-- Create CT_DKDV table
CREATE TABLE CT_DKDV (
    MaCTDK VARCHAR2(10) PRIMARY KEY,
    NgayBD DATE,
    NgayKT DATE,
    MaHD VARCHAR2(10),
    MaDV VARCHAR2(10),
    MaLop VARCHAR2(10),
    MaNV VARCHAR2(10),
    CONSTRAINT FK_CT_DKDV_HOADON FOREIGN KEY (MaHD) REFERENCES HOADON(MaHD),
    CONSTRAINT FK_CT_DKDV_DICHVU FOREIGN KEY (MaDV) REFERENCES DICHVU(MaDV),
    CONSTRAINT FK_CT_DKDV_LOP FOREIGN KEY (MaLop) REFERENCES LOP(MaLop),
    CONSTRAINT FK_CT_DKDV_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV)
);

-- Create CT_THUEDC table
CREATE TABLE CT_THUEDC (
    MaHD VARCHAR2(10),
    MaTB VARCHAR2(10),
    SL NUMBER,
    ThoiGianThue DATE,
    ThoiGianTra DATE,
    ThanhTien NUMBER,
    CONSTRAINT PK_CT_THUEDC PRIMARY KEY (MaHD, MaTB),
    CONSTRAINT FK_THUEDC_HOADON FOREIGN KEY (MaHD) REFERENCES HOADON(MaHD),
    CONSTRAINT FK_THUEDC_THIETBI FOREIGN KEY (MaTB) REFERENCES THIETBI(MaTB)
);
SELECT constraint_name, table_name, search_condition
FROM all_constraints
WHERE constraint_name = 'SYS_C008498';

-- Create CATAP table
CREATE TABLE CATAP (
    MaCa VARCHAR2(10) PRIMARY KEY,
    TenCa VARCHAR2(10),
    MoTa VARCHAR2(50)
);

-- Create LICHTAP table
CREATE TABLE LICHTAP (
    MaLT VARCHAR2(10) PRIMARY KEY,
    LoaiLich VARCHAR2(10) CHECK (LoaiLich IN ('Lop', 'PT')),
    NgayTap DATE,
    TrangThai VARCHAR2(20) CHECK (TrangThai IN ('Sap dien ra', 'Hoan thanh', 'Huy', 'Hoan')),
    Ca VARCHAR2(10),
    MaKH VARCHAR2(10),
    MaNV VARCHAR2(10),
    MaLop VARCHAR2(10),
    CONSTRAINT FK_LICHTAP_CATAP FOREIGN KEY (Ca) REFERENCES CATAP(MaCa),
    CONSTRAINT FK_LICHTAP_KHACHHANG FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH),
    CONSTRAINT FK_LICHTAP_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT FK_LICHTAP_LOP FOREIGN KEY (MaLop) REFERENCES LOP(MaLop)
);

-- Create LICHLAMVIEC table
CREATE TABLE LICHLAMVIEC (
    MaLLV VARCHAR2(10) PRIMARY KEY,
    NgayLam DATE,
    DiemDanh VARCHAR2(10) CHECK (DiemDanh IN ('DaLam', 'ChuaLam', 'Vang')),
    Ca VARCHAR2(10),
    MaNV VARCHAR2(10),
    CONSTRAINT FK_LICHLAMVIEC_CATAP FOREIGN KEY (Ca) REFERENCES CATAP(MaCa),
    CONSTRAINT FK_LICHLAMVIEC_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV)
);

-- Create LUONG table
CREATE TABLE LUONG (
    MaLuong VARCHAR2(10) PRIMARY KEY,
    LuongCung NUMBER NOT NULL,
    HoaHong NUMBER,
    LuongThuong NUMBER,
    Phat NUMBER,
    TongLuong NUMBER,
    NgayNhan DATE,
    MaNV VARCHAR2(10),
    CONSTRAINT FK_LUONG_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV)
);
-- Create PHIEUBAOTRI table
CREATE TABLE PHIEUBAOTRI (
    MaPBT VARCHAR2(10) PRIMARY KEY,
    NgayLap DATE,
    NgayBaoTri DATE,
    MaNV VARCHAR2(10),
    MaTB VARCHAR2(10),
    CONSTRAINT FK_PHIEUBAOTRI_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT FK_PHIEUBAOTRI_THIETBI FOREIGN KEY (MaTB) REFERENCES THIETBI(MaTB)
);


-- 1. Tạo bảng ROLE_GROUP
CREATE TABLE ROLE_GROUP (
    ROLE_GROUP_ID NUMBER(10) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME_ROLE_GROUP VARCHAR2(100) NOT NULL,
    CREATED_AT DATE DEFAULT SYSDATE,
    UPDATED_AT DATE DEFAULT SYSDATE,
    IS_DELETED NUMBER(1) DEFAULT 0 CHECK (IS_DELETED IN (0,1))
);
insert into ROLE_GROUP(ROLE_GROUP_ID,name_role_group,IS_DELETED)
values (4,'TRAINER',0);
select * from ACCOUNT;
select * from KHACHHANG;
select * from ROLE_GROUP;
delete from ACCOUNT where ACCOUNT_ID = 7;
delete from KHACHHANG where MAKH = 'KH005';
-- 2. Tạo bảng ACCOUNT
CREATE TABLE ACCOUNT (
    ACCOUNT_ID    NUMBER(10)  GENERATED BY DEFAULT AS IDENTITY primary key,
    ROLE_GROUP_ID NUMBER(10),
    MaKH VARCHAR2(10),
    MaNV VARCHAR2(10),
    USERNAME      VARCHAR2(50)  NOT NULL UNIQUE,
    PASSWORD_HASH VARCHAR2(200) NOT NULL,
    STATUS        VARCHAR2(20)  DEFAULT 'ACTIVE',
    CREATED_AT    DATE          DEFAULT SYSDATE,
    UPDATED_AT    DATE          DEFAULT SYSDATE,
    IS_DELETED    NUMBER(1)     DEFAULT 0 CHECK (IS_DELETED IN (0, 1)),
    CONSTRAINT FK_ACCOUNT_ROLE_GROUP FOREIGN KEY (ROLE_GROUP_ID) REFERENCES ROLE_GROUP(ROLE_GROUP_ID),
    CONSTRAINT FK_ACCOUNT_KHACHHANG FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH),
    CONSTRAINT FK_ACCOUNT_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT CHK_ONE_REFERENCE CHECK (
        (MaKH IS NOT NULL AND MaNV IS NULL) OR
        (MaKH IS NULL AND MaNV IS NOT NULL) OR
        (MaKH IS NULL AND MaNV IS NULL)
    )
);
-- Tài khoản cho nhân viên mã NV001 (pass: 111)
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (2, 'NV001', 'nhanvien001', '$2a$12$6ohMVeo1CUR93RcjPNXGDO6akRsdfVJsLBqq87BxJzcLtAijYvFvS','ACTIVE',0);

-- Tài khoản cho nhân viên mã QL001 (pass: 111)
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (1, 'QL001', 'quanly001', '$2a$12$KYhzg7VUhsCfQVhdyCRmX.BdaphD97R5G9zVRD49T.lzX7coO0hnG','ACTIVE',0);

-- Tài khoản cho nhân viên mã PT001 (pass: 111)
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (4, 'PT001', 'trainer001', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK','ACTIVE',0);

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

-- 5. Tạo bảng ROLE_GROUP_ASSIGN_ROLE
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
-- 6. Tạo bảng ACCOUNT_ASSIGN_ROLE_GROUP
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

-- 7. Tạo bảng ACCOUNT_ASSIGN_ROLE
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

-- 8. Tạo bảng ACCOUNT_TOKEN
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