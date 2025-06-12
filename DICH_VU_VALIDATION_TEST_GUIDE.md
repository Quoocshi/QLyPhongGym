# HƯỚNG DẪN TEST TÍNH NĂNG VALIDATION DỊCH VỤ

## 📋 Mô tả tính năng

**Quy tắc Business**: 

1. **Mỗi bộ môn chỉ được chọn một loại dịch vụ** (Tự Do HOẶC Lớp/PT)
   - ❌ **Nếu đã có Lớp/PT** của bộ môn X → **không được đăng ký TuDo** của bộ môn X
   - ❌ **Nếu đã có TuDo** của bộ môn X → **không được đăng ký Lớp/PT** của bộ môn X

2. **Mỗi bộ môn chỉ được có tối đa 1 dịch vụ PT**
   - ❌ **Nếu đã có PT** của bộ môn X → **không được đăng ký PT khác** của bộ môn X
   - ✅ **Có thể đăng ký nhiều Lớp** của cùng bộ môn (nếu cần)

- ✅ **Có thể xem** dịch vụ nhưng **không được đăng ký** khi vi phạm quy tắc

## 🧪 Các trường hợp test

### **Test Case 1: Đăng ký TuDo sau khi có Lớp**
1. Đăng ký dịch vụ **YOGA Lop 30N** (chọn lớp)
2. Thử đăng ký dịch vụ **YOGA TuDo 30N**  
3. **Kết quả mong đợi**: ❌ Hiển thị lỗi: *"Không thể đăng ký dịch vụ Tự Do YOGA vì bạn đã có dịch vụ Lớp của YOGA trong giỏ hàng"*

### **Test Case 2: Đăng ký TuDo sau khi có PT**
1. Đăng ký dịch vụ **GYM PT 30N** (chọn trainer)
2. Thử đăng ký dịch vụ **GYM TuDo 30N**
3. **Kết quả mong đợi**: ❌ Hiển thị lỗi: *"Không thể đăng ký dịch vụ Tự Do GYM vì bạn đã có dịch vụ Personal Trainer của GYM trong giỏ hàng"*

### **Test Case 3: Đăng ký Lớp sau khi có TuDo**
1. Đăng ký dịch vụ **ZUMBA TuDo 30N**
2. Thử đăng ký dịch vụ **ZUMBA Lop 30N** (chọn lớp)
3. **Kết quả mong đợi**: ❌ Hiển thị lỗi: *"Không thể đăng ký dịch vụ Lớp ZUMBA vì bạn đã có dịch vụ Tự Do của ZUMBA trong giỏ hàng"*

### **Test Case 4: Đăng ký PT sau khi có TuDo**
1. Đăng ký dịch vụ **CARDIO TuDo 30N**
2. Thử đăng ký dịch vụ **CARDIO PT 30N** (chọn trainer)
3. **Kết quả mong đợi**: ❌ Hiển thị lỗi: *"Không thể đăng ký dịch vụ Personal Trainer CARDIO vì bạn đã có dịch vụ Tự Do của CARDIO trong giỏ hàng"*

### **Test Case 5: Đăng ký khác bộ môn (Hợp lệ)**
1. Đăng ký dịch vụ **GYM TuDo 30N**
2. Đăng ký dịch vụ **YOGA Lop 30N** (chọn lớp)
3. **Kết quả mong đợi**: ✅ Thành công vì khác bộ môn

### **Test Case 6: Đăng ký 2 PT cùng bộ môn (Vi phạm quy tắc mới)**
1. Đăng ký dịch vụ **GYM PT 7N** (chọn trainer A)
2. Thử đăng ký dịch vụ **GYM PT 30N** (chọn trainer B)
3. **Kết quả mong đợi**: ❌ Hiển thị lỗi: *"Không thể đăng ký thêm dịch vụ Personal Trainer GYM vì bạn đã có dịch vụ PT khác của GYM trong giỏ hàng"*

### **Test Case 7: Đăng ký PT khác bộ môn (Hợp lệ)**
1. Đăng ký dịch vụ **GYM PT 30N** (chọn trainer)
2. Đăng ký dịch vụ **CARDIO PT 30N** (chọn trainer)
3. **Kết quả mong đợi**: ✅ Thành công vì khác bộ môn

### **Test Case 8: Đăng ký cùng loại dịch vụ TuDo (Hợp lệ)**
1. Đăng ký dịch vụ **GYM TuDo 7N**
2. Đăng ký dịch vụ **GYM TuDo 30N**
3. **Kết quả mong đợi**: ✅ Thành công vì cùng loại TuDo

### **Test Case 9: Đăng ký nhiều Lớp cùng bộ môn (Hợp lệ)**
1. Đăng ký dịch vụ **YOGA Lop 7N** (chọn lớp A)
2. Đăng ký dịch vụ **YOGA Lop 30N** (chọn lớp B)
3. **Kết quả mong đợi**: ✅ Thành công vì được phép nhiều Lớp cùng bộ môn

## 🔧 Cách test trên hệ thống

### **Bước 1: Đăng nhập**
- Truy cập: `/login`
- Đăng nhập với tài khoản USER

### **Bước 2: Vào trang đăng ký dịch vụ**
- Truy cập: `/dich-vu-gym/dang-kydv`
- Chọn bộ môn để test

### **Bước 3: Thực hiện test case**
- Đăng ký dịch vụ đầu tiên
- Kiểm tra giỏ hàng hiển thị đúng
- Thử đăng ký dịch vụ thứ hai
- Kiểm tra thông báo lỗi

### **Bước 4: Kiểm tra Server Log**
Quan sát console để thấy:
```
=== KIỂM TRA VALIDATION DỊCH VỤ ===
Mã DV muốn đăng ký: DV09
Cart services raw: 'DV13'
Danh sách DV trong giỏ: [DV13]
❌ Validation TuDo failed: ❌ Không thể đăng ký dịch vụ Tự Do YOGA...
```

## 🎯 Điểm cần kiểm tra

### **Frontend JavaScript**
- [x] Gọi API validation trước khi đăng ký
- [x] Hiển thị thông báo lỗi đúng cách
- [x] Không cho phép thêm vào giỏ hàng khi validation fail
- [x] Session storage hoạt động đúng

### **Backend Java**
- [x] API `/api/dichvu-validation/kiem-tra` hoạt động
- [x] Logic kiểm tra TuDo vs Lop/PT đúng
- [x] Logic kiểm tra Lop/PT vs TuDo đúng  
- [x] Xử lý exception và error message

### **Database**
- [x] Lấy thông tin bộ môn từ dịch vụ đúng
- [x] So sánh mã bộ môn chính xác
- [x] Enum LoaiDichVu hoạt động đúng

## 🚀 Thông báo lỗi mẫu

### **Conflict TuDo vs Lop/PT:**
```
❌ Không thể đăng ký dịch vụ Tự Do YOGA vì bạn đã có dịch vụ Lớp của YOGA trong giỏ hàng. Mỗi bộ môn chỉ được chọn một loại dịch vụ (Tự Do HOẶC Lớp/PT).

❌ Không thể đăng ký dịch vụ Personal Trainer GYM vì bạn đã có dịch vụ Tự Do của GYM trong giỏ hàng. Mỗi bộ môn chỉ được chọn một loại dịch vụ (Tự Do HOẶC Lớp/PT).
```

### **Conflict PT Duplicate:**
```
❌ Không thể đăng ký thêm dịch vụ Personal Trainer GYM vì bạn đã có dịch vụ PT khác của GYM trong giỏ hàng. Mỗi bộ môn chỉ được chọn một dịch vụ Personal Trainer.

❌ Không thể đăng ký thêm dịch vụ Personal Trainer CARDIO vì bạn đã có dịch vụ PT khác của CARDIO trong giỏ hàng. Mỗi bộ môn chỉ được chọn một dịch vụ Personal Trainer.
```

## 📂 Files đã tạo/sửa

### **Java Backend**
- `DichVuValidationService.java` - Service validation logic
- `DichVuValidationController.java` - REST API endpoint

### **JavaScript Frontend** 
- `dangkydv.js` - Thêm validation cho TuDo
- `chonlop.html` - Thêm validation cho Lớp
- `chonpt.html` - Thêm validation cho PT

### **Các thay đổi chính**
1. Kiểm tra validation trước khi đăng ký
2. Hiển thị thông báo lỗi thân thiện
3. Không cho phép conflict giữa TuDo và Lop/PT cùng bộ môn
4. Logging chi tiết để debug

---

**✅ Tính năng hoàn thành và sẵn sàng test!** 