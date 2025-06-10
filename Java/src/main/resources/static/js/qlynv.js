// Quản lý nhân viên - JavaScript functions (Backend search only)

// Xác nhận xóa nhân viên
function confirmDelete(maNV, tenNV) {
    const modal = document.getElementById('deleteModal');
    if (modal) {
        document.getElementById('deleteMaNV').value = maNV;
        document.getElementById('deleteEmployeeName').textContent = tenNV;
        modal.style.display = 'block';
    }
}

// Đóng modal xác nhận xóa
function closeDeleteModal() {
    const modal = document.getElementById('deleteModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Đóng modal khi click bên ngoài
window.onclick = function(event) {
    const modal = document.getElementById('deleteModal');
    if (modal && event.target === modal) {
        closeDeleteModal();
    }
}

// Khởi tạo khi trang được load
document.addEventListener('DOMContentLoaded', function() {
    // Tự động ẩn thông báo sau 5 giây
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.opacity = '0';
            setTimeout(function() {
                alert.style.display = 'none';
            }, 300);
        }, 5000);
    });
    
    console.log('Trang quản lý nhân viên đã sẵn sàng - Backend search mode');
}); 