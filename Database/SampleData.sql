INSERT INTO KHACHHANG (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode) VALUES
('KH005', 'Nguyễn Văn Nam', TO_DATE('1990-05-15', 'YYYY-MM-DD'), 'Nam', 'nam.nguyen@gmail.com', '0905123456', '123 Lý Thường Kiệt, Hà Nội', 'REFNAM90');
INSERT INTO KHACHHANG (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode) VALUES
('KH002', 'Lê Thị Hồng', TO_DATE('1992-08-22', 'YYYY-MM-DD'), 'Nữ', 'hong.le@gmail.com', '0934789654', '45 Nguyễn Huệ, TP.HCM', 'REFHONG92');
INSERT INTO KHACHHANG (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode) VALUES
('KH003', 'John Smith', TO_DATE('1985-03-10', 'YYYY-MM-DD'), 'Male', 'john.smith@example.com', '+14155552671', '25 Wall Street, New York, USA', 'REFJOHN85');
INSERT INTO KHACHHANG (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode) VALUES
('KH004', 'Anna Müller', TO_DATE('1994-12-01', 'YYYY-MM-DD'), 'Female', 'anna.mueller@example.de', '+4917612345678', 'Berlin, Germany', 'REFANNA94');



INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('NV001', 'NHANVIEN001', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Nam', 'nhanvien001@example.com', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'LeTan');
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('QL001', 'QUANLY001', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Nam', 'quanly001@example.com', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'QuanLy');
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT001', 'TRAINER001', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Nam', 'trainer001@example.com', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'Trainer');

INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM01', 'Gym Fitness');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM02', 'Yoga');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM03', 'Zumba');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM04', 'Cardio');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM05', 'Boi'); 

INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM) VALUES 
('GYM',    'GYM',     'TuDo', 6, 6999999, 'BM01');
INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM) VALUES 
('YOGA',   'YOGA',    'Lop',  6, 6999999, 'BM02');
INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM) VALUES 
('ZUMBA',  'ZUMBA',   'Lop',  6, 6999999, 'BM03');
INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM) VALUES 
('CARDIO', 'CARDIO',  'TuDo', 6, 6999999, 'BM04');
INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM) VALUES 
('BƠI',    'BƠI',     'TuDo', 6, 6999999, 'BM05');
INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM) VALUES 
('GYMPT',  'GYM PT',  'PT',   6, 6999999, 'BM01');


INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('L01', 'Lớp Sáng Gym', 'Lớp luyện tập buổi sáng cho người mới bắt đầu', 25, 'ChuaDay', TO_DATE('2025-06-01', 'YYYY-MM-DD'), 'Đăng ký sớm', 'BM01', 'PT001');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('L02', 'Yoga Cơ Bản', 'Lớp Yoga dành cho người mới bắt đầu', 20, 'DaDay', TO_DATE('2025-05-15', 'YYYY-MM-DD'), 'Lớp đã đầy học viên', 'BM02', 'PT001');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('L03', 'Zumba Cuối Tuần', 'Lớp nhảy Zumba vào cuối tuần', 30, 'ChuaDay', TO_DATE('2025-06-10', 'YYYY-MM-DD'), '', 'BM03', 'PT001');

insert into ROLE_GROUP(name_role_group) values ('ADMIN');
insert into ROLE_GROUP(name_role_group) values ('STAFF');
insert into ROLE_GROUP(name_role_group) values ('USER');
insert into ROLE_GROUP(name_role_group) values ('TRAINER');


-- Tài khoản cho nhân viên mã NV001 (pass: 111)
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (2, 'NV001', 'nhanvien001', '$2a$12$6ohMVeo1CUR93RcjPNXGDO6akRsdfVJsLBqq87BxJzcLtAijYvFvS','ACTIVE',0);

-- Tài khoản cho quản lý mã QL001 (pass: 111)
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (1, 'QL001', 'quanly001', '$2a$12$KYhzg7VUhsCfQVhdyCRmX.BdaphD97R5G9zVRD49T.lzX7coO0hnG','ACTIVE',0);

-- Tài khoản cho trainer mã PT001 (pass: 111)
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (4, 'PT001', 'trainer001', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK','ACTIVE',0);

select * from NHANVIEN;
select * from LOP;
select * from BOMON;
select * from DICHVU;
select * from ROLE_GROUP;
select * from ACCOUNT;
select * from HOADON;
select  * from KHACHHANG;

BEGIN
   FOR t IN (SELECT table_name FROM user_tables) LOOP
      BEGIN
         EXECUTE IMMEDIATE 'DROP TABLE "' || t.table_name || '" CASCADE CONSTRAINTS';
      EXCEPTION
         WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Lỗi khi xoá bảng: ' || t.table_name || ' - ' || SQLERRM);
      END;
   END LOOP;
END;
/
