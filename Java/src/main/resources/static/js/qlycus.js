// Mở popup
document.addEventListener("DOMContentLoaded", function() {
    // Popup Thêm
    var addBtn = document.querySelector('.add-btn');
    var addModal = document.getElementById('addCustomerModal');
    var closeAddBtn = document.getElementById('closeAddModal');
    if (addBtn && addModal && closeAddBtn) {
        addBtn.onclick = function() { addModal.style.display = 'flex'; };
        closeAddBtn.onclick = function() { addModal.style.display = 'none'; };
    }

    // Popup Sửa
    var editBtn = document.querySelector('.edit-btn');
    var editModal = document.getElementById('editCustomerModal');
    var closeEditBtn = document.getElementById('closeEditModal');
    if (editBtn && editModal && closeEditBtn) {
        editBtn.onclick = function() {
            // Lấy dòng được tick
            var checked = document.querySelectorAll('.row-checkbox:checked');
            if (checked.length !== 1) {
                alert('Vui lòng chọn đúng 1 khách hàng để sửa!');
                return;
            }
            var row = checked[0].closest('tr');
            // Lấy dữ liệu từ dòng
            document.getElementById('editMaKH').value = row.cells[1].innerText;
            document.getElementById('editHoTen').value = row.cells[2].innerText;
            document.getElementById('editNgaySinh').value = row.cells[3].innerText;
            document.getElementById('editGioiTinh').value = row.cells[4].innerText;
            document.getElementById('editEmail').value = row.cells[5].innerText;
            document.getElementById('editSDT').value = row.cells[6].innerText;
            document.getElementById('editDiaChi').value = row.cells[7].innerText;
            editModal.style.display = 'flex';
        };
        closeEditBtn.onclick = function() { editModal.style.display = 'none'; };
    }

    // Đóng popup khi click ra ngoài
    window.onclick = function(event) {
        if (addModal && event.target == addModal) addModal.style.display = 'none';
        if (editModal && event.target == editModal) editModal.style.display = 'none';
    };

    // Xóa khách hàng
    var deleteBtn = document.querySelector('.delete-btn');
    if (deleteBtn) {
        deleteBtn.onclick = function() {
            var checked = document.querySelectorAll('.row-checkbox:checked');
            if (checked.length === 0) {
                alert('Vui lòng chọn khách hàng để xóa!');
                return;
            }
            checked.forEach(function(checkbox) {
                checkbox.closest('tr').remove();
            });
        };
    }

    // Chọn tất cả
    var selectAll = document.getElementById('selectAll');
    if (selectAll) {
        selectAll.onclick = function() {
            var checkboxes = document.querySelectorAll('.row-checkbox');
            checkboxes.forEach(cb => cb.checked = selectAll.checked);
        };
    }
});
