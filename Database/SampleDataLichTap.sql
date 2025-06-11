-- Script để thêm dữ liệu mẫu cho bảng LICHTAP
-- Thêm lịch tập cho các lớp học

-- Thêm dữ liệu LICHTAP cho các lớp học
-- MaKH = NULL vì đây là lịch lớp, không phải lịch PT 1-1

-- YOGA Classes (BM02 - KV02)
-- LOP01: Yoga Cơ Bản Sáng - PT003
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT001', 'Lop', '246', 'CA01', 'LOP01', NULL, 'PT003', 'KV02', 'Dang mo');

-- LOP02: Yoga Nâng Cao Tối - PT003  
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT002', 'Lop', '357', 'CA06', 'LOP02', NULL, 'PT003', 'KV02', 'Dang mo');

-- LOP07: Yoga Sức Mạnh - PT006
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT003', 'Lop', '24', 'CA02', 'LOP07', NULL, 'PT006', 'KV02', 'Dang mo');

-- LOP08: Yoga Hồi Phục - PT006
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT004', 'Lop', '46', 'CA05', 'LOP08', NULL, 'PT006', 'KV02', 'Dang mo');

-- LOP09: Yoga Flow Buổi Sáng - PT006
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT005', 'Lop', '135', 'CA01', 'LOP09', NULL, 'PT006', 'KV02', 'Dang mo');

-- ZUMBA Classes (BM03 - KV03)
-- LOP03: Zumba Fitness Cơ Bản - PT004
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT006', 'Lop', '246', 'CA04', 'LOP03', NULL, 'PT004', 'KV03', 'Dang mo');

-- LOP04: Zumba Fitness Nâng Cao - PT004
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT007', 'Lop', '357', 'CA05', 'LOP04', NULL, 'PT004', 'KV03', 'Dang mo');

-- LOP10: Zumba Buổi Trưa - PT007
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT008', 'Lop', '235', 'CA04', 'LOP10', NULL, 'PT007', 'KV03', 'Dang mo');

-- LOP11: Zumba Cuối Tuần - PT007 (chỉ thứ 7)
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT009', 'Lop', '7', 'CA02', 'LOP11', NULL, 'PT007', 'KV03', 'Dang mo');

-- BƠI Classes (BM05 - KV05)
-- LOP05: Bơi Sải Cơ Bản - PT005
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT010', 'Lop', '24', 'CA03', 'LOP05', NULL, 'PT005', 'KV05', 'Dang mo');

-- LOP06: Lớp Bơi Trẻ Em - PT005 (cuối tuần)
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT011', 'Lop', '67', 'CA03', 'LOP06', NULL, 'PT005', 'KV05', 'Dang mo');

-- LOP12: Bơi Ếch Tối - PT008
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT012', 'Lop', '357', 'CA06', 'LOP12', NULL, 'PT008', 'KV05', 'Dang mo');

-- Thêm một số lịch tập bổ sung cho đa dạng
-- Yoga buổi tối cho PT003
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT013', 'Lop', '13', 'CA06', 'LOP01', NULL, 'PT003', 'KV02', 'Dang mo');

-- Zumba buổi sáng cho PT004
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT014', 'Lop', '57', 'CA02', 'LOP03', NULL, 'PT004', 'KV03', 'Dang mo');

-- Bơi buổi sáng cho PT008
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT015', 'Lop', '246', 'CA01', 'LOP12', NULL, 'PT008', 'KV05', 'Dang mo');

-- Yoga chiều cho PT006
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT016', 'Lop', '25', 'CA04', 'LOP07', NULL, 'PT006', 'KV02', 'Dang mo');

-- Zumba tối cho PT007
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT017', 'Lop', '14', 'CA06', 'LOP10', NULL, 'PT007', 'KV03', 'Dang mo');

-- Bơi chiều cho PT005
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT018', 'Lop', '35', 'CA05', 'LOP05', NULL, 'PT005', 'KV05', 'Dang mo');

COMMIT; 