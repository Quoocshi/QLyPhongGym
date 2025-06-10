insert into ROLE_GROUP(name_role_group) values ('ADMIN');
insert into ROLE_GROUP(name_role_group) values ('STAFF');
insert into ROLE_GROUP(name_role_group) values ('USER');
insert into ROLE_GROUP(name_role_group) values ('TRAINER');

INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('NV001', 'NHANVIEN001', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Nam', 'nhanvien001@example.com', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'LeTan');
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('QL001', 'QUANLY001', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Nam', 'quanly001@example.com', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'QuanLy');
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT001', 'TRAINER001', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Nam', 'trainer001@example.com', TO_DATE('2020-01-01', 'YYYY-MM-DD'), 'Trainer');


-- Thêm Huấn luyện viên đứng lớp (Trainers)
INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT002', 'Trần Minh Tuấn', TO_DATE('1992-08-10', 'YYYY-MM-DD'), 'Nam', 'tuantm.trainer@email.com', TO_DATE('2021-03-01', 'YYYY-MM-DD'), 'Trainer'); -- Trainer cho GYM/CROSSFIT
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (4, 'PT002', 'trainer002', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK','ACTIVE',0);

INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT003', 'Lê Thị Bảo Châu', TO_DATE('1995-11-25', 'YYYY-MM-DD'), 'Nu', 'chaulb.trainer@email.com', TO_DATE('2022-06-10', 'YYYY-MM-DD'), 'Trainer'); -- Trainer cho YOGA
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (4, 'PT003', 'trainer003', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK','ACTIVE',0);

INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT004', 'Phạm Hồng Hạnh', TO_DATE('1998-02-14', 'YYYY-MM-DD'), 'Nu', 'hanhph.trainer@email.com', TO_DATE('2023-01-20', 'YYYY-MM-DD'), 'Trainer'); -- Trainer cho ZUMBA
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (4, 'PT004', 'trainer004', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK','ACTIVE',0);

INSERT INTO NHANVIEN (MaNV, TenNV, NgaySinh, GioiTinh, Email, NgayVaoLam, LoaiNV)
VALUES ('PT005', 'Hoàng Quốc Việt', TO_DATE('1994-07-30', 'YYYY-MM-DD'), 'Nam', 'viethq.trainer@email.com', TO_DATE('2022-09-01', 'YYYY-MM-DD'), 'Trainer'); -- Trainer cho BƠI
INSERT INTO ACCOUNT (ROLE_GROUP_ID, MaNV, USERNAME, PASSWORD_HASH,STATUS,IS_DELETED)
VALUES (4, 'PT005', 'trainer005', '$2a$12$nul1RU9h4Q80aD2iBTwbt.YUKH3U8QspOvqcv066Wi2AvVMsjVqDK','ACTIVE',0);
-- Thêm Bộ môn
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM01', 'Gym Fitness');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM02', 'Yoga');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM03', 'Zumba');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM04', 'Cardio');
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM05', 'Boi'); 
INSERT INTO BOMON (MaBM, TenBM) VALUES ('BM06', 'Crossfit'); 

-- Thêm Chuyên môn cho các Trainers
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu)
VALUES ('PT003', 'BM02', 'HLV Yoga');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu)
VALUES ('PT004', 'BM03', 'HLV Zumba');
INSERT INTO CT_CHUYENMON (MaNV, MaBM, GhiChu)
VALUES ('PT005', 'BM05', 'HLV Bơi');

-- Lớp YOGA (BM02) do HLV 'NV03' (Lê Thị Bảo Châu) phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('LOP01', 'Yoga Cơ Bản Sáng', 'Lớp cho người mới bắt đầu, tập trung vào các tư thế và nhịp thở.', 20, 'ChuaDay', TO_DATE('2025-06-16', 'YYYY-MM-DD'), 'Vui lòng mang thảm tập', 'BM02', 'PT003');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('LOP02', 'Yoga Nâng Cao Tối', 'Dành cho hội viên đã có kinh nghiệm, khám phá các tư thế phức tạp.', 15, 'ChuaDay', TO_DATE('2025-06-17', 'YYYY-MM-DD'), NULL, 'BM02', 'PT003');

-- Lớp ZUMBA (BM03) do HLV 'NV04' (Phạm Hồng Hạnh) phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('LOP03', 'Zumba Fitness Cơ Bản', 'Đốt cháy calo cùng những điệu nhảy Latin vui nhộn.', 25, 'ChuaDay', TO_DATE('2025-06-16', 'YYYY-MM-DD'), 'Mang giày thể thao phù hợp', 'BM03', 'PT004');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('LOP04', 'Zumba Fitness Nâng Cao', 'Đốt cháy calo cùng những điệu nhảy Latin vui nhộn.', 25, 'ChuaDay', TO_DATE('2025-06-16', 'YYYY-MM-DD'), 'Mang giày thể thao phù hợp', 'BM03', 'PT004');

-- Lớp BƠI (BM05) do HLV 'NV05' (Hoàng Quốc Việt) phụ trách
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('LOP05', 'Bơi Sải Cơ Bản', 'Lớp học kỹ thuật bơi sải dành cho người lớn.', 10, 'ChuaDay', TO_DATE('2025-06-18', 'YYYY-MM-DD'), 'Yêu cầu biết bơi cơ bản', 'BM05', 'PT005');
INSERT INTO LOP (MaLop, TenLop, MoTa, SL_ToiDa, TinhTrang, NgayBD, GhiChu, MaBM, MaNV)
VALUES ('LOP06', 'Lớp Bơi Trẻ Em', 'Dạy bơi cho trẻ từ 6-12 tuổi, an toàn và hiệu quả.', 12, 'ChuaDay', TO_DATE('2025-06-21', 'YYYY-MM-DD'), 'Học vào cuối tuần', 'BM05', 'PT005');


-- Them Dich vu
-- BM01 – GYM (TuDo, PT)
INSERT INTO DICHVU VALUES ('DV01', 'GYM TuDo 7N', 'TuDo', 7, 300000, 'BM01');
INSERT INTO DICHVU VALUES ('DV02', 'GYM TuDo 30N', 'TuDo', 30, 1000000, 'BM01');
INSERT INTO DICHVU VALUES ('DV03', 'GYM TuDo 90N', 'TuDo', 90, 2700000, 'BM01');
INSERT INTO DICHVU VALUES ('DV04', 'GYM TuDo 365N', 'TuDo', 365, 9000000, 'BM01');
INSERT INTO DICHVU VALUES ('DV05', 'GYM PT 7N', 'PT', 7, 800000, 'BM01');
INSERT INTO DICHVU VALUES ('DV06', 'GYM PT 30N', 'PT', 30, 2500000, 'BM01');
INSERT INTO DICHVU VALUES ('DV07', 'GYM PT 90N', 'PT', 90, 6500000, 'BM01');
INSERT INTO DICHVU VALUES ('DV08', 'GYM PT 365N', 'PT', 365, 20000000, 'BM01');

-- BM02 – YOGA (TuDo, Lop)
INSERT INTO DICHVU VALUES ('DV09', 'YOGA TuDo 7N', 'TuDo', 7, 300000, 'BM02');
INSERT INTO DICHVU VALUES ('DV10', 'YOGA TuDo 30N', 'TuDo', 30, 1000000, 'BM02');
INSERT INTO DICHVU VALUES ('DV11', 'YOGA TuDo 90N', 'TuDo', 90, 2700000, 'BM02');
INSERT INTO DICHVU VALUES ('DV12', 'YOGA TuDo 365N', 'TuDo', 365, 9000000, 'BM02');
INSERT INTO DICHVU VALUES ('DV13', 'YOGA Lop 7N', 'Lop', 7, 400000, 'BM02');
INSERT INTO DICHVU VALUES ('DV14', 'YOGA Lop 30N', 'Lop', 30, 1300000, 'BM02');
INSERT INTO DICHVU VALUES ('DV15', 'YOGA Lop 90N', 'Lop', 90, 3500000, 'BM02');
INSERT INTO DICHVU VALUES ('DV16', 'YOGA Lop 365N', 'Lop', 365, 10000000, 'BM02');

-- BM03 – ZUMBA (TuDo, Lop)
INSERT INTO DICHVU VALUES ('DV17', 'ZUMBA TuDo 7N', 'TuDo', 7, 300000, 'BM03');
INSERT INTO DICHVU VALUES ('DV18', 'ZUMBA TuDo 30N', 'TuDo', 30, 1000000, 'BM03');
INSERT INTO DICHVU VALUES ('DV19', 'ZUMBA TuDo 90N', 'TuDo', 90, 2700000, 'BM03');
INSERT INTO DICHVU VALUES ('DV20', 'ZUMBA TuDo 365N', 'TuDo', 365, 9000000, 'BM03');
INSERT INTO DICHVU VALUES ('DV21', 'ZUMBA Lop 7N', 'Lop', 7, 400000, 'BM03');
INSERT INTO DICHVU VALUES ('DV22', 'ZUMBA Lop 30N', 'Lop', 30, 1300000, 'BM03');
INSERT INTO DICHVU VALUES ('DV23', 'ZUMBA Lop 90N', 'Lop', 90, 3500000, 'BM03');
INSERT INTO DICHVU VALUES ('DV24', 'ZUMBA Lop 365N', 'Lop', 365, 10000000, 'BM03');

-- BM04 – CARDIO (TuDo, PT)
INSERT INTO DICHVU VALUES ('DV25', 'CARDIO TuDo 7N', 'TuDo', 7, 300000, 'BM04');
INSERT INTO DICHVU VALUES ('DV26', 'CARDIO TuDo 30N', 'TuDo', 30, 1000000, 'BM04');
INSERT INTO DICHVU VALUES ('DV27', 'CARDIO TuDo 90N', 'TuDo', 90, 2700000, 'BM04');
INSERT INTO DICHVU VALUES ('DV28', 'CARDIO TuDo 365N', 'TuDo', 365, 9000000, 'BM04');
INSERT INTO DICHVU VALUES ('DV29', 'CARDIO PT 7N', 'PT', 7, 800000, 'BM04');
INSERT INTO DICHVU VALUES ('DV30', 'CARDIO PT 30N', 'PT', 30, 2500000, 'BM04');
INSERT INTO DICHVU VALUES ('DV31', 'CARDIO PT 90N', 'PT', 90, 6500000, 'BM04');
INSERT INTO DICHVU VALUES ('DV32', 'CARDIO PT 365N', 'PT', 365, 20000000, 'BM04');

-- BM05 – BƠI (TuDo, Lop)
INSERT INTO DICHVU VALUES ('DV33', 'BOI TuDo 7N', 'TuDo', 7, 300000, 'BM05');
INSERT INTO DICHVU VALUES ('DV34', 'BOI TuDo 30N', 'TuDo', 30, 1000000, 'BM05');
INSERT INTO DICHVU VALUES ('DV35', 'BOI TuDo 90N', 'TuDo', 90, 2700000, 'BM05');
INSERT INTO DICHVU VALUES ('DV36', 'BOI TuDo 365N', 'TuDo', 365, 9000000, 'BM05');
INSERT INTO DICHVU VALUES ('DV37', 'BOI Lop 7N', 'Lop', 7, 400000, 'BM05');
INSERT INTO DICHVU VALUES ('DV38', 'BOI Lop 30N', 'Lop', 30, 1300000, 'BM05');
INSERT INTO DICHVU VALUES ('DV39', 'BOI Lop 90N', 'Lop', 90, 3500000, 'BM05');
INSERT INTO DICHVU VALUES ('DV40', 'BOI Lop 365N', 'Lop', 365, 10000000, 'BM05');

-- BM06 - CROSSFIT (TuDo, PT)
INSERT INTO DICHVU VALUES ('DV41', 'CROSSFIT TuDo 7N', 'TuDo', 7, 300000, 'BM06');
INSERT INTO DICHVU VALUES ('DV42', 'CROSSFIT TuDo 30N', 'TuDo', 30, 1000000, 'BM06');
INSERT INTO DICHVU VALUES ('DV43', 'CROSSFIT TuDo 90N', 'TuDo', 90, 2700000, 'BM06');
INSERT INTO DICHVU VALUES ('DV44', 'CROSSFIT TuDo 365N', 'TuDo', 365, 9000000, 'BM06');
INSERT INTO DICHVU VALUES ('DV45', 'CROSSFIT PT 7N', 'PT', 7, 800000, 'BM06');
INSERT INTO DICHVU VALUES ('DV46', 'CROSSFIT PT 30N', 'PT', 30, 2500000, 'BM06');
INSERT INTO DICHVU VALUES ('DV47', 'CROSSFIT PT 90N', 'PT', 90, 6500000, 'BM06');
INSERT INTO DICHVU VALUES ('DV48', 'CROSSFIT PT 365N', 'PT', 365, 20000000, 'BM06');



--===============================================================Mẫu Đăng Nhập===========================================================================================

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
select * from CT_DKDV;


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
