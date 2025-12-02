-- ===============================
-- 1️⃣ Thêm ROLE_GROUP
-- ===============================
INSERT INTO ROLE_GROUP (name_role_group) VALUES ('ADMIN');
INSERT INTO ROLE_GROUP (name_role_group) VALUES ('STAFF');
INSERT INTO ROLE_GROUP (name_role_group) VALUES ('USER');
INSERT INTO ROLE_GROUP (name_role_group) VALUES ('TRAINER');

-- ===============================
-- 2️⃣ Thêm nhân viên mẫu (NHANVIEN)
-- ===============================
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('NV001', 'NHANVIEN001', '1990-01-01'::DATE, 'Nam', 'nhanvien001@example.com', '2020-01-01'::DATE, 'LeTan');

INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('QL001', 'QUANLY001', '1990-01-01'::DATE, 'Nam', 'quanly001@example.com', '2020-01-01'::DATE, 'QuanLy');

INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT001', 'TRAINER001', '1990-01-01'::DATE, 'Nam', 'trainer001@example.com', '2020-01-01'::DATE, 'Trainer');

-- ===============================
-- 3️⃣ Thêm ca tập (CATAP)
-- ===============================
INSERT INTO CATAP (MaCa, TenCa, MoTa) VALUES ('CA01', 'Sáng 1', '06:00 - 07:30');
INSERT INTO CATAP (MaCa, TenCa, MoTa) VALUES ('CA02', 'Sáng 2', '08:00 - 09:30');
INSERT INTO CATAP (MaCa, TenCa, MoTa) VALUES ('CA03', 'Sáng 3', '10:00 - 11:30');
INSERT INTO CATAP (MaCa, TenCa, MoTa) VALUES ('CA04', 'Chiều 1', '14:00 - 15:30');
INSERT INTO CATAP (MaCa, TenCa, MoTa) VALUES ('CA05', 'Chiều 2', '16:00 - 17:30');
INSERT INTO CATAP (MaCa, TenCa, MoTa) VALUES ('CA06', 'Tối', '18:00 - 19:30');

-- ===============================
-- 4️⃣ Thêm huấn luyện viên (TRAINERS)
-- ===============================
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT002', 'Trần Minh Tuấn', '1992-08-10'::DATE, 'Nam', 'tuantm.trainer@email.com', '2021-03-01'::DATE, 'Trainer');

INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT002', 'trainer002', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);


INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT003', 'Lê Thị Bảo Châu', '1995-11-25'::DATE, 'Nu', 'chaulb.trainer@email.com', '2022-06-10'::DATE, 'Trainer');

INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT003', 'trainer003', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);


INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT006', 'Nguyễn Thanh Tâm', '1992-05-15'::DATE, 'Nu', 'tamnt.trainer@email.com', '2024-01-10'::DATE, 'Trainer');

INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT006', 'trainer006', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);


INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT004', 'Phạm Hồng Hạnh', '1998-02-14'::DATE, 'Nu', 'hanhph.trainer@email.com', '2023-01-20'::DATE, 'Trainer');

INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT004', 'trainer004', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);


INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT007', 'Trần Hoàng Dũng', '1991-09-10'::DATE, 'Nam', 'dungh.trainer@email.com', '2023-12-01'::DATE, 'Trainer');

INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT007', 'trainer007', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);


INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT005', 'Hoàng Quốc Việt', '1994-07-30'::DATE, 'Nam', 'viethq.trainer@email.com', '2022-09-01'::DATE, 'Trainer');

INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT005', 'trainer005', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);

INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT008', 'Đỗ Minh Trí', '1993-03-20'::DATE, 'Nam', 'tridm.trainer@email.com', '2023-11-05'::DATE, 'Trainer');

INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT008', 'trainer008', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);


-- Thêm PT 
-- PT009: GYM
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT009', 'Nguyễn Văn Cường', TO_DATE('1991-05-12', 'YYYY-MM-DD'), 'Nam', 'cuongnv.gym@email.com', TO_DATE('2022-01-15', 'YYYY-MM-DD'), 'Trainer');
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT009', 'trainer009', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);
-- PT0010: GYM
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT010', 'Lê Thị Mai', TO_DATE('1990-07-22', 'YYYY-MM-DD'), 'Nữ', 'mailt.gym@email.com', TO_DATE('2022-03-10', 'YYYY-MM-DD'), 'Trainer');
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT010', 'trainer010', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);
-- PT011: GYM
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT011', 'Phạm Hữu Hùng', TO_DATE('1988-11-03', 'YYYY-MM-DD'), 'Nam', 'hungph.gym@email.com', TO_DATE('2021-09-01', 'YYYY-MM-DD'), 'Trainer');
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT011', 'trainer011', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);
-- PT012: Cardio
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT012', 'Nguyễn Thị Hoa', TO_DATE('1993-06-25', 'YYYY-MM-DD'), 'Nữ', 'hoant.cardio@email.com', TO_DATE('2022-05-01', 'YYYY-MM-DD'), 'Trainer');
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT012', 'trainer012', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);
-- PT013: Cardio
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT013', 'Trần Quốc Long', TO_DATE('1990-02-28', 'YYYY-MM-DD'), 'Nam', 'longtq.cardio@email.com', TO_DATE('2021-11-15', 'YYYY-MM-DD'), 'Trainer');
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT013', 'trainer013', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);
-- PT014: Cardio
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT014', 'Lâm Thị Châu', TO_DATE('1992-09-19', 'YYYY-MM-DD'), 'Nữ', 'chaul.cardio@email.com', TO_DATE('2023-01-05', 'YYYY-MM-DD'), 'Trainer');
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT014', 'trainer014', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);
-- PT015: CrossFit
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT015', 'Lê Văn Nam', TO_DATE('1987-04-10', 'YYYY-MM-DD'), 'Nam', 'namlv.crossfit@email.com', TO_DATE('2021-07-01', 'YYYY-MM-DD'), 'Trainer');
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT015', 'trainer015', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);
-- PT016: CrossFit
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT016', 'Đặng Thị Lan', TO_DATE('1994-12-01', 'YYYY-MM-DD'), 'Nữ', 'landt.crossfit@email.com', TO_DATE('2022-04-20', 'YYYY-MM-DD'), 'Trainer');
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT016', 'trainer016', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);
-- PT017: CrossFit
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT017', 'Phạm Minh Khoa', TO_DATE('1989-03-18', 'YYYY-MM-DD'), 'Nam', 'khoapm.crossfit@email.com', TO_DATE('2023-02-01', 'YYYY-MM-DD'), 'Trainer');
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH, STATUS, IS_DELETED)
VALUES (4, 'PT017', 'trainer017', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK', 'ACTIVE', 0);

-- Thêm Bộ môn
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM01', 'Gym Fitness');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM02', 'Yoga');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM03', 'Zumba');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM04', 'Cardio');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM05', 'Boi'); 
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM06', 'Crossfit'); 

-- Thêm Khu vực
INSERT INTO KHUVUC (MaKV, TenKhuVuc, SucChuaToiDa, TrangThai, TinhTrang, MaBM)
VALUES ('KV01', 'Phòng Gym', 40, 'HoatDong', 'ChuaDay', 'BM01');
INSERT INTO KHUVUC (MaKV, TenKhuVuc, SucChuaToiDa, TrangThai, TinhTrang, MaBM)
VALUES ('KV02', 'Phòng Yoga', 25, 'HoatDong', 'ChuaDay', 'BM02');
INSERT INTO KHUVUC (MaKV, TenKhuVuc, SucChuaToiDa, TrangThai, TinhTrang, MaBM)
VALUES ('KV03', 'Phòng Zumba', 30, 'HoatDong', 'ChuaDay', 'BM03');
INSERT INTO KHUVUC (MaKV, TenKhuVuc, SucChuaToiDa, TrangThai, TinhTrang, MaBM)
VALUES ('KV04', 'Khu Cardio', 20, 'HoatDong', 'ChuaDay', 'BM04');
INSERT INTO KHUVUC (MaKV, TenKhuVuc, SucChuaToiDa, TrangThai, TinhTrang, MaBM)
VALUES ('KV05', 'Hồ Bơi', 15, 'HoatDong', 'ChuaDay', 'BM05');
INSERT INTO KHUVUC (MaKV, TenKhuVuc, SucChuaToiDa, TrangThai, TinhTrang, MaBM)
VALUES ('KV06', 'Sân Crossfit', 20, 'HoatDong', 'ChuaDay', 'BM06');

-- Thêm Chuyên môn cho các Trainers
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu)
VALUES ('PT003', 'BM02', 'HLV Yoga');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu)
VALUES ('PT006', 'BM02', 'HLV Yoga');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu)
VALUES ('PT004', 'BM03', 'HLV Zumba');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu)
VALUES ('PT007', 'BM03', 'HLV Zumba');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu)
VALUES ('PT005', 'BM05', 'HLV Bơi');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu)
VALUES ('PT008', 'BM05', 'HLV Bơi');

-- Thêm Chuyên môn cho các PT
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu) VALUES ('PT009', 'BM01', 'HLV Gym');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu) VALUES ('PT010', 'BM01', 'HLV Gym');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu) VALUES ('PT011', 'BM01', 'HLV Gym');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu) VALUES ('PT012', 'BM04', 'HLV Cardio');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu) VALUES ('PT013', 'BM04', 'HLV Cardio');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu) VALUES ('PT014', 'BM04', 'HLV Cardio');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu) VALUES ('PT015', 'BM06', 'HLV CrossFit');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu) VALUES ('PT016', 'BM06', 'HLV CrossFit');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu) VALUES ('PT017', 'BM06', 'HLV CrossFit');

-- Lớp YOGA (BM02) do PT003 phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP01', 'Yoga Cơ Bản Sáng', 'Lớp cho người mới bắt đầu, tập trung vào các tư thế và nhịp thở.', 20, 'ChuaDay', DATE '2025-06-16', DATE '2025-07-16', 'Vui lòng mang thảm tập', 'BM02', 'PT003');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP02', 'Yoga Nâng Cao Tối', 'Dành cho hội viên đã có kinh nghiệm, khám phá các tư thế phức tạp.', 15, 'ChuaDay', DATE '2025-06-17', DATE '2025-08-15', NULL, 'BM02', 'PT003');
-- PT006 phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP07', 'Yoga Sức Mạnh', 'Tăng cường cơ bắp với các bài yoga năng lượng cao.', 18, 'ChuaDay', DATE '2025-06-20', DATE '2025-09-18', NULL, 'BM02', 'PT006');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP08', 'Yoga Hồi Phục', 'Phục hồi cơ thể sau luyện tập nặng hoặc căng thẳng.', 12, 'ChuaDay', DATE '2025-06-22', DATE '2025-07-22', NULL, 'BM02', 'PT006');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP09', 'Yoga Flow Buổi Sáng', 'Dòng chảy nhẹ nhàng giúp bạn khởi đầu ngày mới.', 15, 'ChuaDay', DATE '2025-06-23', DATE '2025-09-21', NULL, 'BM02', 'PT006');

-- Lớp ZUMBA (BM03) do PT004 phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP03', 'Zumba Fitness Cơ Bản', 'Đốt cháy calo cùng những điệu nhảy Latin vui nhộn.', 25, 'ChuaDay', DATE '2025-06-16', DATE '2025-07-16', 'Mang giày thể thao phù hợp', 'BM03', 'PT004');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP04', 'Zumba Fitness Nâng Cao', 'Đốt cháy calo cùng những điệu nhảy Latin vui nhộn.', 25, 'ChuaDay', DATE '2025-06-16', DATE '2025-08-14', 'Mang giày thể thao phù hợp', 'BM03', 'PT004');
-- PT007 phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP10', 'Zumba Buổi Trưa', 'Tập luyện giữa ngày để tiếp thêm năng lượng.', 20, 'ChuaDay', DATE '2025-06-20', DATE '2025-07-20', NULL, 'BM03', 'PT007');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP11', 'Zumba Cuối Tuần', 'Tập luyện vui vẻ vào cuối tuần cùng âm nhạc sôi động.', 30, 'ChuaDay', DATE '2025-06-22', DATE '2025-09-20', 'Có tổ chức vào sáng thứ Bảy', 'BM03', 'PT007');

-- Lớp BƠI (BM05) do PT005 phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP05', 'Bơi Sải Cơ Bản', 'Lớp học kỹ thuật bơi sải dành cho người lớn.', 10, 'ChuaDay', DATE '2025-06-18', DATE '2025-07-18', 'Yêu cầu biết bơi cơ bản', 'BM05', 'PT005');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP06', 'Lớp Bơi Trẻ Em', 'Dạy bơi cho trẻ từ 6-12 tuổi, an toàn và hiệu quả.', 12, 'ChuaDay', DATE '2025-06-21', DATE '2025-07-21', 'Học vào cuối tuần', 'BM05', 'PT005');
-- PT008 phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP12', 'Bơi Ếch Tối', 'Học kỹ thuật bơi ếch cơ bản vào buổi tối.', 10, 'ChuaDay', DATE '2025-06-24', DATE '2025-07-24', 'Mang theo kính bơi', 'BM05', 'PT008');

-- Thêm Lịch tập cho các lớp

-- YOGA
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai) VALUES
('LT001', 'Lop', '246', 'CA01', 'LOP01', NULL, 'PT003', 'KV02', 'Dang mo'),
('LT013', 'Lop', '13',  'CA06', 'LOP01', NULL, 'PT003', 'KV02', 'Dang mo'),
('LT002', 'Lop', '357', 'CA06', 'LOP02', NULL, 'PT003', 'KV02', 'Dang mo'),
('LT003', 'Lop', '24',  'CA02', 'LOP07', NULL, 'PT006', 'KV02', 'Dang mo'),
('LT016', 'Lop', '25',  'CA04', 'LOP07', NULL, 'PT006', 'KV02', 'Dang mo'),
('LT004', 'Lop', '46',  'CA05', 'LOP08', NULL, 'PT006', 'KV02', 'Dang mo'),
('LT005', 'Lop', '135', 'CA01', 'LOP09', NULL, 'PT006', 'KV02', 'Dang mo'),
('LT019', 'Lop', '246', 'CA01', 'LOP07', NULL, 'PT003', 'KV02', 'Dang mo'),
('LT020', 'Lop', '357', 'CA06', 'LOP07', NULL, 'PT003', 'KV02', 'Dang mo'),
('LT021', 'Lop', '135', 'CA02', 'LOP08', NULL, 'PT006', 'KV02', 'Dang mo'),
('LT022', 'Lop', '24',  'CA05', 'LOP07', NULL, 'PT006', 'KV02', 'Dang mo'),
('LT023', 'Lop', '246', 'CA03', 'LOP02', NULL, 'PT003', 'KV02', 'Dang mo'),
('LT024', 'Lop', '135', 'CA04', 'LOP09', NULL, 'PT006', 'KV02', 'Dang mo');

-- ZUMBA
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai) VALUES
('LT006', 'Lop', '246', 'CA04', 'LOP03', NULL, 'PT004', 'KV03', 'Dang mo'),
('LT014', 'Lop', '57',  'CA02', 'LOP03', NULL, 'PT004', 'KV03', 'Dang mo'),
('LT007', 'Lop', '357', 'CA05', 'LOP04', NULL, 'PT004', 'KV03', 'Dang mo'),
('LT008', 'Lop', '235', 'CA04', 'LOP10', NULL, 'PT007', 'KV03', 'Dang mo'),
('LT017', 'Lop', '14',  'CA06', 'LOP10', NULL, 'PT007', 'KV03', 'Dang mo'),
('LT009', 'Lop', '7',   'CA02', 'LOP11', NULL, 'PT007', 'KV03', 'Dang mo');

-- BƠI
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai) VALUES
('LT010', 'Lop', '24',  'CA03', 'LOP05', NULL, 'PT005', 'KV05', 'Dang mo'),
('LT018', 'Lop', '35',  'CA05', 'LOP05', NULL, 'PT005', 'KV05', 'Dang mo'),
('LT011', 'Lop', '67',  'CA03', 'LOP06', NULL, 'PT005', 'KV05', 'Dang mo'),
('LT012', 'Lop', '357', 'CA06', 'LOP12', NULL, 'PT008', 'KV05', 'Dang mo'),
('LT015', 'Lop', '246', 'CA01', 'LOP12', NULL, 'PT008', 'KV05', 'Dang mo');

-- Thêm dịch vụ
INSERT INTO DICHVU (MaDV, TenDV, LoaiDV, ThoiHan, DonGia, MaBM, Version) VALUES
-- BM01 – GYM (TuDo, PT)
('DV01', 'GYM TuDo 7N', 'TuDo', 7, 300000, 'BM01', 0),
('DV02', 'GYM TuDo 30N', 'TuDo', 30, 1000000, 'BM01', 0),
('DV03', 'GYM TuDo 90N', 'TuDo', 90, 2700000, 'BM01', 0),
('DV04', 'GYM TuDo 365N', 'TuDo', 365, 9000000, 'BM01', 0),
('DV05', 'GYM PT 7N', 'PT', 7, 800000, 'BM01', 0),
('DV06', 'GYM PT 30N', 'PT', 30, 2500000, 'BM01', 0),
('DV07', 'GYM PT 90N', 'PT', 90, 6500000, 'BM01', 0),
('DV08', 'GYM PT 365N', 'PT', 365, 20000000, 'BM01', 0),

-- BM02 – YOGA (TuDo, Lop)
('DV09', 'YOGA TuDo 7N', 'TuDo', 7, 300000, 'BM02', 0),
('DV10', 'YOGA TuDo 30N', 'TuDo', 30, 1000000, 'BM02', 0),
('DV11', 'YOGA TuDo 90N', 'TuDo', 90, 2700000, 'BM02', 0),
('DV12', 'YOGA TuDo 365N', 'TuDo', 365, 9000000, 'BM02', 0),
('DV13', 'YOGA Lop 7N', 'Lop', 7, 400000, 'BM02', 0),
('DV14', 'YOGA Lop 30N', 'Lop', 30, 1300000, 'BM02', 0),
('DV15', 'YOGA Lop 90N', 'Lop', 90, 3500000, 'BM02', 0),
('DV16', 'YOGA Lop 365N', 'Lop', 365, 10000000, 'BM02', 0),

-- BM03 – ZUMBA (TuDo, Lop)
('DV17', 'ZUMBA TuDo 7N', 'TuDo', 7, 300000, 'BM03', 0),
('DV18', 'ZUMBA TuDo 30N', 'TuDo', 30, 1000000, 'BM03', 0),
('DV19', 'ZUMBA TuDo 90N', 'TuDo', 90, 2700000, 'BM03', 0),
('DV20', 'ZUMBA TuDo 365N', 'TuDo', 365, 9000000, 'BM03', 0),
('DV21', 'ZUMBA Lop 7N', 'Lop', 7, 400000, 'BM03', 0),
('DV22', 'ZUMBA Lop 30N', 'Lop', 30, 1300000, 'BM03', 0),
('DV23', 'ZUMBA Lop 90N', 'Lop', 90, 3500000, 'BM03', 0),
('DV24', 'ZUMBA Lop 365N', 'Lop', 365, 10000000, 'BM03', 0),

-- BM04 – CARDIO (TuDo, PT)
('DV25', 'CARDIO TuDo 7N', 'TuDo', 7, 300000, 'BM04', 0),
('DV26', 'CARDIO TuDo 30N', 'TuDo', 30, 1000000, 'BM04', 0),
('DV27', 'CARDIO TuDo 90N', 'TuDo', 90, 2700000, 'BM04', 0),
('DV28', 'CARDIO TuDo 365N', 'TuDo', 365, 9000000, 'BM04', 0),
('DV29', 'CARDIO PT 7N', 'PT', 7, 800000, 'BM04', 0),
('DV30', 'CARDIO PT 30N', 'PT', 30, 2500000, 'BM04', 0),
('DV31', 'CARDIO PT 90N', 'PT', 90, 6500000, 'BM04', 0),
('DV32', 'CARDIO PT 365N', 'PT', 365, 20000000, 'BM04', 0),

-- BM05 – BƠI (TuDo, Lop)
('DV33', 'BOI TuDo 7N', 'TuDo', 7, 300000, 'BM05', 0),
('DV34', 'BOI TuDo 30N', 'TuDo', 30, 1000000, 'BM05', 0),
('DV35', 'BOI TuDo 90N', 'TuDo', 90, 2700000, 'BM05', 0),
('DV36', 'BOI TuDo 365N', 'TuDo', 365, 9000000, 'BM05', 0),
('DV37', 'BOI Lop 7N', 'Lop', 7, 400000, 'BM05', 0),
('DV38', 'BOI Lop 30N', 'Lop', 30, 1300000, 'BM05', 0),
('DV39', 'BOI Lop 90N', 'Lop', 90, 3500000, 'BM05', 0),
('DV40', 'BOI Lop 365N', 'Lop', 365, 10000000, 'BM05', 0),

-- BM06 – CROSSFIT (TuDo, PT)
('DV41', 'CROSSFIT TuDo 7N', 'TuDo', 7, 300000, 'BM06', 0),
('DV42', 'CROSSFIT TuDo 30N', 'TuDo', 30, 1000000, 'BM06', 0),
('DV43', 'CROSSFIT TuDo 90N', 'TuDo', 90, 2700000, 'BM06', 0),
('DV44', 'CROSSFIT TuDo 365N', 'TuDo', 365, 9000000, 'BM06', 0),
('DV45', 'CROSSFIT PT 7N', 'PT', 7, 800000, 'BM06', 0),
('DV46', 'CROSSFIT PT 30N', 'PT', 30, 2500000, 'BM06', 0),
('DV47', 'CROSSFIT PT 90N', 'PT', 90, 6500000, 'BM06', 0),
('DV48', 'CROSSFIT PT 365N', 'PT', 365, 20000000, 'BM06', 0);

--===============================================================Mẫu Đăng Nhập===========================================================================================

-- Tài khoản cho nhân viên mã NV001 (pass: 111)
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED) VALUES
(2, 'NV001', 'nhanvien001', '$2a$12$6ohMVeo1CUR93RcjPNXGDO6akRsdfVJsLBqq87BxJzcLtAijYvFvS','ACTIVE',0),
(1, 'QL001', 'quanly001', '$2a$12$KYhzg7VUhsCfQVhdyCRmX.BdaphD97R5G9zVRD49T.lzX7coO0hnG','ACTIVE',0),
(4, 'PT001', 'trainer001', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK','ACTIVE',0);


select * from ROLE_GROUP;
select * from NHANVIEN;
select * from LOP;
select * from BOMON;
select * from ROLE_GROUP;
select * from ACCOUNT;
select * from HOADON;
select * from KHACHHANG;
select * from CT_DKDV;
SELECT * FROM LICHTAP;
SELECT * FROM dichvu;
select * from ct_chuyenmon;
select * from khuvuc;
select * from ct_dkdv;
--Delete all tables
DO
$$
DECLARE
    r RECORD;
BEGIN
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
        RAISE NOTICE 'Đã xoá bảng: %', r.tablename;
    END LOOP;
END
$$;

