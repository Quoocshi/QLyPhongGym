﻿-- CUSTOMER
INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
VALUES ('KH001', 'Nguyễn Văn A', TO_DATE('1990-05-15', 'YYYY-MM-DD'), 'Nam', 'nva@gmail.com', '0909123456', 'Hà Nội', 'REF123');

INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
VALUES ('KH002', 'Trần Thị B', TO_DATE('1995-07-20', 'YYYY-MM-DD'), 'Nữ', 'ttb@gmail.com', '0909345678', 'TPHCM', 'REF456');

INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
VALUES ('KH003', 'Lê Minh C', TO_DATE('1992-09-12', 'YYYY-MM-DD'), 'Nam', 'lmc@gmail.com', '0909888777', 'Đà Nẵng', 'REF789');

INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
VALUES ('KH004', 'Phạm Thị D', TO_DATE('1998-03-22', 'YYYY-MM-DD'), 'Nữ', 'ptd@gmail.com', '0909555666', 'Hải Phòng', 'REF012');

INSERT INTO CUSTOMER (MaKH, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, ReferralCode)
VALUES ('KH005', 'Phạm Thị E', TO_DATE('1998-03-22', 'YYYY-MM-DD'), 'Nữ', 'ptd@gmail.com', '0909555666', 'Hải Phòng', 'REF013');

SELECT * FROM CUSTOMER c

TRUNCATE TABLE customer;
-- PACKAGE
INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan)
VALUES ('PK001', 'Gói Gym 1 tháng', 'Gym', 500000, 30);

INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan)
VALUES ('PK002', 'Gói Yoga 3 tháng', 'Yoga', 1200000, 90);

INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan)
VALUES ('PK003', 'Gói CrossFit 6 tháng', 'Combo', 3000000, 180);

INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan)
VALUES ('PK004', 'Gói Boxing 1 năm', 'Cardio', 5000000, 365);

SELECT * FROM PACKAGE p;
TRUNCATE TABLE PACKAGE;
-- WORKOUT_AREA
INSERT INTO WORKOUT_AREA (MaKhuVuc, TenKhuVuc, SucChuaToiDa)
VALUES ('KV001', 'Phòng Gym 1', 30);

INSERT INTO WORKOUT_AREA (MaKhuVuc, TenKhuVuc, SucChuaToiDa)
VALUES ('KV002', 'Phòng Yoga', 20);

INSERT INTO WORKOUT_AREA (MaKhuVuc, TenKhuVuc, SucChuaToiDa)
VALUES ('KV003', 'Phòng CrossFit', 25);

INSERT INTO WORKOUT_AREA (MaKhuVuc, TenKhuVuc, SucChuaToiDa)
VALUES ('KV004', 'Phòng Boxing', 15);

SELECT * FROM WORKOUT_AREA wa;
TRUNCATE TABLE WORKOUT_AREA;


-- SCHEDULE
INSERT INTO SCHEDULE (MaLich, MaKH, MaGoi, NgayTap, MaPT, MaCaTap)
VALUES ('SC001', 'KH001', 'PK001', TO_DATE('2025-03-10', 'YYYY-MM-DD'), 'PT001', 'CA001');

INSERT INTO SCHEDULE (MaLich, MaKH, MaGoi, NgayTap, MaPT, MaCaTap)
VALUES ('SC002', 'KH002', 'PK002', TO_DATE('2025-03-15', 'YYYY-MM-DD'), 'PT002', 'CA002');

INSERT INTO SCHEDULE (MaLich, MaKH, MaGoi, NgayTap, MaPT, MaCaTap)
VALUES ('SC003', 'KH003', 'PK003', TO_DATE('2025-03-18', 'YYYY-MM-DD'), 'PT003', 'CA003');

INSERT INTO SCHEDULE (MaLich, MaKH, MaGoi, NgayTap, MaPT, MaCaTap)
VALUES ('SC004', 'KH004', 'PK004', TO_DATE('2025-03-20', 'YYYY-MM-DD'), 'PT004', 'CA004');

-- PAYMENT
INSERT INTO PAYMENT (MaGD, MaKH, SoTien, PhuongThucTT, NgayTT)
VALUES ('GD001', 'KH001', 500000, 'The', TO_DATE('2025-03-05', 'YYYY-MM-DD'));

INSERT INTO PAYMENT (MaGD, MaKH, SoTien, PhuongThucTT, NgayTT)
VALUES ('GD002', 'KH002', 1200000, 'Vi Dien Tu', TO_DATE('2025-03-06', 'YYYY-MM-DD'));

INSERT INTO PAYMENT (MaGD, MaKH, SoTien, PhuongThucTT, NgayTT)
VALUES ('GD003', 'KH003', 3000000, 'Tien Mat', TO_DATE('2025-03-07', 'YYYY-MM-DD'));

INSERT INTO PAYMENT (MaGD, MaKH, SoTien, PhuongThucTT, NgayTT)
VALUES ('GD004', 'KH004', 5000000, 'Tien Mat', TO_DATE('2025-03-08', 'YYYY-MM-DD'));


SELECT * FROM PAYMENT p;
TRUNCATE TABLE PAYMENT;


-- MEMBERSHIP
INSERT INTO MEMBERSHIP (MaHV, MaKH, NgayBatDau, NgayKetThuc, TinhTrang)
VALUES ('MB001', 'KH001', TO_DATE('2025-03-01', 'YYYY-MM-DD'), TO_DATE('2025-06-01', 'YYYY-MM-DD'), 'Active');

INSERT INTO MEMBERSHIP (MaHV, MaKH, NgayBatDau, NgayKetThuc, TinhTrang)
VALUES ('MB002', 'KH002', TO_DATE('2025-03-01', 'YYYY-MM-DD'), TO_DATE('2025-09-01', 'YYYY-MM-DD'), 'Active');

INSERT INTO MEMBERSHIP (MaHV, MaKH, NgayBatDau, NgayKetThuc, TinhTrang)
VALUES ('MB003', 'KH003', TO_DATE('2025-03-01', 'YYYY-MM-DD'), TO_DATE('2025-12-01', 'YYYY-MM-DD'), 'Active');

INSERT INTO MEMBERSHIP (MaHV, MaKH, NgayBatDau, NgayKetThuc, TinhTrang)
VALUES ('MB004', 'KH004', TO_DATE('2025-03-01', 'YYYY-MM-DD'), TO_DATE('2026-03-01', 'YYYY-MM-DD'), 'Active');


SELECT * FROM MEMBERSHIP m;
TRUNCATE TABLE MEMBERSHIP;
-- ATTENDANCE
INSERT INTO ATTENDANCE (MaDiemDanh, MaKH, NgayDiemDanh, TrangThai)
VALUES ('AT001', 'KH001', TO_DATE('2025-03-08', 'YYYY-MM-DD'), 'Đã điểm danh');

INSERT INTO ATTENDANCE (MaDiemDanh, MaKH, NgayDiemDanh, TrangThai)
VALUES ('AT002', 'KH002', TO_DATE('2025-03-09', 'YYYY-MM-DD'), 'Vắng mặt');

INSERT INTO ATTENDANCE (MaDiemDanh, MaKH, NgayDiemDanh, TrangThai)
VALUES ('AT003', 'KH003', TO_DATE('2025-03-10', 'YYYY-MM-DD'), 'Đã điểm danh');

INSERT INTO ATTENDANCE (MaDiemDanh, MaKH, NgayDiemDanh, TrangThai)
VALUES ('AT004', 'KH004', TO_DATE('2025-03-11', 'YYYY-MM-DD'), 'Vắng mặt');

SELECT * FROM ATTENDANCE a;
TRUNCATE TABLE ATTENDANCE;

--PT
INSERT INTO PT (MaPT, HOTENPT, SODIENTHOAIPT, EMAILPT, ChuyenMon)
VALUES ('PT001', 'Nguyễn Văn A', '0987654321', 'nguyenvana@gmail.com', 'Gym');

INSERT INTO PT (MaPT, HOTENPT, SODIENTHOAIPT, EMAILPT, ChuyenMon)
VALUES ('PT002', 'Trần Thị B', '0977654321', 'tranthib@gmail.com', 'Yoga');

INSERT INTO PT (MaPT, HOTENPT, SODIENTHOAIPT, EMAILPT, ChuyenMon)
VALUES ('PT003', 'Lê Văn C', '0966654321', 'levanc@gmail.com', 'CrossFit');

INSERT INTO PT (MaPT, HOTENPT, SODIENTHOAIPT, EMAILPT, ChuyenMon)
VALUES ('PT004', 'Hoàng Thị D', '0955654321', 'hoangthid@gmail.com', 'Boxing');

SELECT * FROM PT p;
TRUNCATE TABLE PT;

-- Bảng PT_ATTENDANCE 
INSERT INTO PT_ATTENDANCE (MaDDPT, MaPT, NgayDiemDanh, TrangThai)
VALUES ('DD001', 'PT001', TO_DATE('2025-03-01', 'YYYY-MM-DD'), 'Có mặt');

INSERT INTO PT_ATTENDANCE (MaDDPT, MaPT, NgayDiemDanh, TrangThai)
VALUES ('DD002', 'PT002', TO_DATE('2025-03-01', 'YYYY-MM-DD'), 'Có mặt');

INSERT INTO PT_ATTENDANCE (MaDDPT, MaPT, NgayDiemDanh, TrangThai)
VALUES ('DD003', 'PT003', TO_DATE('2025-03-01', 'YYYY-MM-DD'), 'Nghỉ phép');

INSERT INTO PT_ATTENDANCE (MaDDPT, MaPT, NgayDiemDanh, TrangThai)
VALUES ('DD004', 'PT004', TO_DATE('2025-03-01', 'YYYY-MM-DD'), 'Có mặt');

SELECT * FROM PT_ATTENDANCE pa;
TRUNCATE TABLE PT_ATTENDANCE;

-- Bảng STAFF
INSERT INTO STAFF (MaNV, HoTenNV, SoDienThoaiNV, EmailNV, ViTri, Luong)
VALUES ('NV001', 'Đặng Thị E', '0987654321', 'e.dang@example.com', 'Lễ tân', 8000000);

INSERT INTO STAFF (MaNV, HoTenNV, SoDienThoaiNV, EmailNV, ViTri, Luong)
VALUES ('NV002', 'Hoàng Văn F', '0976543210', 'f.hoang@example.com', 'Quản lý', 22000000);

INSERT INTO STAFF (MaNV, HoTenNV, SoDienThoaiNV, EmailNV, ViTri, Luong)
VALUES ('NV003', 'Lý Hồng G', '0965432109', 'g.ly@example.com', 'Nhân viên vệ sinh', 7000000);

INSERT INTO STAFF (MaNV, HoTenNV, SoDienThoaiNV, EmailNV, ViTri, Luong)
VALUES ('NV004', 'Trần Quốc H', '0954321098', 'h.tran@example.com', 'Bảo vệ', 7500000);

SELECT * FROM STAFF s;
TRUNCATE TABLE STAFF;

-- Bảng STAFF_ATTENDANCE
INSERT INTO STAFF_ATTENDANCE (MaDDNV, MaNV, NgayDiemDanh, TrangThai)
VALUES ('DDNV001', 'NV001', TO_DATE('2025-03-01', 'YYYY-MM-DD'), 'Có mặt');

INSERT INTO STAFF_ATTENDANCE (MaDDNV, MaNV, NgayDiemDanh, TrangThai)
VALUES ('DDNV002', 'NV002', TO_DATE('2025-03-01', 'YYYY-MM-DD'), 'Vắng mặt');

INSERT INTO STAFF_ATTENDANCE (MaDDNV, MaNV, NgayDiemDanh, TrangThai)
VALUES ('DDNV003', 'NV003', TO_DATE('2025-03-01', 'YYYY-MM-DD'), 'Có mặt');

INSERT INTO STAFF_ATTENDANCE (MaDDNV, MaNV, NgayDiemDanh, TrangThai)
VALUES ('DDNV004', 'NV004', TO_DATE('2025-03-01', 'YYYY-MM-DD'), 'Nghỉ phép');

SELECT * FROM STAFF_ATTENDANCE sa;
TRUNCATE TABLE STAFF_ATTENDANCE;

-- Bảng SHIFT
INSERT INTO SHIFT (MaCaTap, MaKhuVuc, GIOBATDAU, GIOKETTHUC)
VALUES ('CA001', 'KV001', TO_DATE('06:00', 'HH24:MI'), TO_DATE('07:30', 'HH24:MI'));

INSERT INTO SHIFT (MaCaTap, MaKhuVuc, GIOBATDAU, GIOKETTHUC)
VALUES ('CA002', 'KV002', TO_DATE('08:00', 'HH24:MI'), TO_DATE('09:30', 'HH24:MI'));

INSERT INTO SHIFT (MaCaTap, MaKhuVuc, GIOBATDAU, GIOKETTHUC)
VALUES ('CA003', 'KV003', TO_DATE('17:00', 'HH24:MI'), TO_DATE('18:30', 'HH24:MI'));

INSERT INTO SHIFT (MaCaTap, MaKhuVuc, GIOBATDAU, GIOKETTHUC)
VALUES ('CA004', 'KV004', TO_DATE('19:00', 'HH24:MI'), TO_DATE('20:30', 'HH24:MI'));

SELECT * FROM SHIFT s;
TRUNCATE TABLE SHIFT;

-- Bảng SERVICE
INSERT INTO SERVICE (MADV, TENDV, MOTADV, GIADV)
VALUES ('SV001', 'Xông hơi', 'Dịch vụ xông hơi thư giãn sau khi tập luyện', 100000);

INSERT INTO SERVICE (MADV, TENDV, MOTADV, GIADV)
VALUES ('SV002', 'Massage cơ', 'Massage giúp thư giãn và giảm căng cơ', 200000);

INSERT INTO SERVICE (MADV, TENDV, MOTADV, GIADV)
VALUES ('SV003', 'Tư vấn dinh dưỡng', 'Hướng dẫn chế độ ăn uống phù hợp với mục tiêu tập luyện', 150000);

INSERT INTO SERVICE (MADV, TENDV, MOTADV, GIADV)
VALUES ('SV004', 'Hướng dẫn cá nhân', 'Buổi tập riêng với huấn luyện viên chuyên nghiệp', 500000);

INSERT INTO SERVICE (MADV, TENDV, MOTADV, GIADV)
VALUES ('SV005', 'Gửi đồ', 'Dịch vụ tủ gửi đồ cá nhân trong phòng tập', 50000);

-- Bảng SUBSCRIPTION
INSERT INTO SUBSCRIPTION (MaDK, MaKH, MaGoi, MaDV, NGAYDK, NgayHetHan, TinhTrang)
VALUES ('DK001', 'KH001', 'PK001', 'SV001', TO_DATE('2025-03-01', 'YYYY-MM-DD'), TO_DATE('2025-04-01', 'YYYY-MM-DD'), 'Active');

INSERT INTO SUBSCRIPTION (MaDK, MaKH, MaGoi, MaDV, NGAYDK, NgayHetHan, TinhTrang)
VALUES ('DK002', 'KH002', 'PK002', 'SV002', TO_DATE('2025-03-05', 'YYYY-MM-DD'), TO_DATE('2025-08-05', 'YYYY-MM-DD'), 'Active');

INSERT INTO SUBSCRIPTION (MaDK, MaKH, MaGoi, MaDV, NGAYDK, NgayHetHan, TinhTrang)
VALUES ('DK003', 'KH003', 'PK003', 'SV003', TO_DATE('2025-03-10', 'YYYY-MM-DD'), TO_DATE('2025-09-10', 'YYYY-MM-DD'), 'Active');

INSERT INTO SUBSCRIPTION (MaDK, MaKH, MaGoi, MaDV, NGAYDK, NgayHetHan, TinhTrang)
VALUES ('DK004', 'KH004', 'PK004', 'SV004', TO_DATE('2025-03-15', 'YYYY-MM-DD'), TO_DATE('2026-03-15', 'YYYY-MM-DD'), 'Active');

SELECT * FROM SUBSCRIPTION s;

-- Bảng PACKAGE
INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan) 
VALUES ('PK001', 'Gói Gym 1 tháng', 'Gym', 500000, 30);

INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan) 
VALUES ('PK002', 'Gói Yoga 3 tháng', 'Yoga', 1200000, 90);

INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan) 
VALUES ('PK003', 'Gói CrossFit 6 tháng', 'Combo', 3000000, 180);

INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan) 
VALUES ('PK004', 'Gói Boxing 1 năm', 'Cardio', 5000000, 365);

INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan) 
VALUES ('PK005', 'Gói Bơi lội 1 tháng', 'Cardio', 600000, 30);

INSERT INTO PACKAGE (MaGoi, TenGoi, LoaiGoi, GiaTien, ThoiHan) 
VALUES ('PK006', 'Gói Pilates 3 tháng', 'Yoga', 1500000, 90);

SELECT * FROM PACKAGE p