#!/bin/bash

# Script để thêm CSRF token vào tất cả forms POST

echo "🔒 Đang thêm CSRF tokens vào tất cả forms..."

# Danh sách các files cần cập nhật
FILES=(
    "Java/src/main/resources/templates/Admin/NhanVien/update.html"
    "Java/src/main/resources/templates/Admin/DichVu/add.html"
    "Java/src/main/resources/templates/Admin/DichVu/update.html"
    "Java/src/main/resources/templates/Admin/DichVu/qlydv.html"
    "Java/src/main/resources/templates/Admin/BoMon/add.html"
    "Java/src/main/resources/templates/Admin/BoMon/update.html"
    "Java/src/main/resources/templates/Admin/BoMon/qlybm.html"
    "Java/src/main/resources/templates/Admin/Customer/add.html"
    "Java/src/main/resources/templates/Admin/Customer/update.html"
    "Java/src/main/resources/templates/Admin/Customer/list.html"
    "Java/src/main/resources/templates/Staff/DichVu/add.html"
    "Java/src/main/resources/templates/Staff/DichVu/update.html"
    "Java/src/main/resources/templates/Staff/DichVu/qlydv.html"
    "Java/src/main/resources/templates/Staff/Customer/qlycus.html"
    "Java/src/main/resources/templates/User/thanhtoan.html"
    "Java/src/main/resources/templates/User/dangkydv.html"
    "Java/src/main/resources/templates/User/chon-bo-mon.html"
)

# CSRF token to add
CSRF_TOKEN='<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />'

for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "📝 Đang cập nhật: $file"
        
        # Thêm CSRF token sau thẻ form mở bằng sed
        # Tìm dòng có <form và method="post", thêm CSRF token vào dòng tiếp theo
        sed -i '/th:action=.*method="post"/a\
                            '"$CSRF_TOKEN" "$file"
        
        # Xử lý trường hợp form có nhiều thuộc tính
        sed -i '/method="post".*th:action/a\
                            '"$CSRF_TOKEN" "$file"
                            
        echo "✅ Hoàn thành: $file"
    else
        echo "❌ Không tìm thấy file: $file"
    fi
done

echo ""
echo "🎉 Hoàn thành! Đã thêm CSRF tokens vào tất cả forms."
echo ""
echo "⚠️  LƯU Ý QUAN TRỌNG:"
echo "1. Kiểm tra manual từng file để đảm bảo CSRF token được thêm đúng vị trí"
echo "2. Test tất cả forms sau khi thêm CSRF"
echo "3. Các form AJAX cũng cần include CSRF token trong header"
echo ""
echo "🧪 Test CSRF protection:"
echo "1. Thử submit form không có CSRF token → Phải nhận 403 Forbidden"
echo "2. Thử submit form có CSRF token → Phải hoạt động bình thường"
echo "3. Test VNPay callback → Phải hoạt động OK (đã exclude)" 