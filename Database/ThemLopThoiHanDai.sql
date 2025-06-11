-- Thêm các lớp YOGA có thời hạn dài phù hợp với dịch vụ 365 ngày

-- Lớp YOGA thời hạn 365 ngày do PT003 phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP13', 'Yoga Toàn Diện 365 Ngày', 'Khóa học yoga toàn diện kéo dài 1 năm với nhiều mức độ khác nhau.', 20, 'ChuaDay', DATE '2025-06-16', DATE '2026-06-16', 'Khóa học dài hạn', 'BM02', 'PT003');

INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP14', 'Yoga Chuyên Nghiệp 365 Ngày', 'Lớp yoga chuyên nghiệp cho những ai muốn trở thành HLV.', 15, 'ChuaDay', DATE '2025-06-17', DATE '2026-06-17', 'Yêu cầu có kinh nghiệm', 'BM02', 'PT006');

-- Lớp YOGA thời hạn 180 ngày
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP15', 'Yoga Nâng Cao 180 Ngày', 'Khóa học nâng cao trong 6 tháng.', 18, 'ChuaDay', DATE '2025-06-20', DATE '2025-12-17', 'Trung cấp', 'BM02', 'PT003');

INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, NgayKT, GhiChu, MaBM, MaNV)
VALUES ('LOP16', 'Yoga Thiền 180 Ngày', 'Kết hợp yoga và thiền định trong 6 tháng.', 12, 'ChuaDay', DATE '2025-06-22', DATE '2025-12-19', 'Tĩnh tâm', 'BM02', 'PT006');

-- Thêm lịch tập cho các lớp mới
-- LOP13: Yoga Toàn Diện 365 Ngày
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT019', 'Lop', '246', 'CA01', 'LOP13', NULL, 'PT003', 'KV02', 'Dang mo');

INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT020', 'Lop', '357', 'CA06', 'LOP13', NULL, 'PT003', 'KV02', 'Dang mo');

-- LOP14: Yoga Chuyên Nghiệp 365 Ngày
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT021', 'Lop', '135', 'CA02', 'LOP14', NULL, 'PT006', 'KV02', 'Dang mo');

INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT022', 'Lop', '24', 'CA05', 'LOP14', NULL, 'PT006', 'KV02', 'Dang mo');

-- LOP15: Yoga Nâng Cao 180 Ngày
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT023', 'Lop', '246', 'CA03', 'LOP15', NULL, 'PT003', 'KV02', 'Dang mo');

-- LOP16: Yoga Thiền 180 Ngày
INSERT INTO LICHTAP (MaLT, LoaiLich, Thu, Ca, MaLop, MaKH, MaNV, MaKV, TrangThai)
VALUES ('LT024', 'Lop', '135', 'CA04', 'LOP16', NULL, 'PT006', 'KV02', 'Dang mo');

COMMIT; 