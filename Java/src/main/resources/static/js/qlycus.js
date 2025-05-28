// // Mở popup
// document.addEventListener("DOMContentLoaded", function() {
//     // Popup Thêm
//     var addBtn = document.querySelector('.add-btn');
//     var addModal = document.getElementById('addCustomerModal');
//     var closeAddBtn = document.getElementById('closeAddModal');
//     if (addBtn && addModal && closeAddBtn) {
//         addBtn.onclick = function() { addModal.style.display = 'flex'; };
//         closeAddBtn.onclick = function() { addModal.style.display = 'none'; };
//     }

//     // Popup Sửa
//     var editBtn = document.querySelector('.edit-btn');
//     var editModal = document.getElementById('editCustomerModal');
//     var closeEditBtn = document.getElementById('closeEditModal');
//     if (editBtn && editModal && closeEditBtn) {
//         editBtn.onclick = function() {
//             // Lấy dòng được tick
//             var checked = document.querySelectorAll('.row-checkbox:checked');
//             if (checked.length !== 1) {
//                 alert('Vui lòng chọn đúng 1 khách hàng để sửa!');
//                 return;
//             }
//             var row = checked[0].closest('tr');
//             // Lấy dữ liệu từ dòng
//             document.getElementById('editMaKH').value = row.cells[1].innerText;
//             document.getElementById('editHoTen').value = row.cells[2].innerText;
//             document.getElementById('editNgaySinh').value = row.cells[3].innerText;
//             document.getElementById('editGioiTinh').value = row.cells[4].innerText;
//             document.getElementById('editEmail').value = row.cells[5].innerText;
//             document.getElementById('editSDT').value = row.cells[6].innerText;
//             document.getElementById('editDiaChi').value = row.cells[7].innerText;
//             editModal.style.display = 'flex';
//         };
//         closeEditBtn.onclick = function() { editModal.style.display = 'none'; };
//     }

//     // Đóng popup khi click ra ngoài
//     window.onclick = function(event) {
//         if (addModal && event.target == addModal) addModal.style.display = 'none';
//         if (editModal && event.target == editModal) editModal.style.display = 'none';
//     };

//     // Xóa khách hàng
//     var deleteBtn = document.querySelector('.delete-btn');
//     if (deleteBtn) {
//         deleteBtn.onclick = function() {
//             var checked = document.querySelectorAll('.row-checkbox:checked');
//             if (checked.length === 0) {
//                 alert('Vui lòng chọn khách hàng để xóa!');
//                 return;
//             }
//             checked.forEach(function(checkbox) {
//                 checkbox.closest('tr').remove();
//             });
//         };
//     }

//     // Chọn tất cả
//     var selectAll = document.getElementById('selectAll');
//     if (selectAll) {
//         selectAll.onclick = function() {
//             var checkboxes = document.querySelectorAll('.row-checkbox');
//             checkboxes.forEach(cb => cb.checked = selectAll.checked);
//         };
//     }
// });

document.addEventListener("DOMContentLoaded", function () {
    const editModal = document.getElementById("editCustomerModal");
    const closeEditModal = document.getElementById("closeEditModal");
    const editForm = document.getElementById("editForm");

    // Sửa khách hàng
    document.querySelectorAll(".edit-btn").forEach(button => {
        button.addEventListener("click", () => {
            const row = button.closest("tr");
            const cells = row.querySelectorAll("td");

            document.getElementById("editMaKH").value = cells[1].innerText.trim();
            document.getElementById("editHoTen").value = cells[2].innerText.trim();
            document.getElementById("editNgaySinh").value = cells[3].innerText.trim();
            document.getElementById("editGioiTinh").value = cells[4].innerText.trim();
            document.getElementById("editEmail").value = cells[5].innerText.trim();
            document.getElementById("editSDT").value = cells[6].innerText.trim();
            document.getElementById("editDiaChi").value = cells[7].innerText.trim();

            editForm.action = `/quan-ly-khach-hang/cap-nhat-thong-tin-khach-hang/${cells[1].innerText.trim()}`;
            editModal.style.display = "flex";
        });
    });

    closeEditModal.addEventListener("click", () => {
        editModal.style.display = "none";
    });

    window.addEventListener("click", (event) => {
        if (event.target === editModal) {
            editModal.style.display = "none";
        }
    });

    // Xóa khách hàng
    const deleteBtn = document.querySelector(".delete-btn");
    deleteBtn?.addEventListener("click", () => {
        const checked = document.querySelectorAll(".row-checkbox:checked");
        if (checked.length === 0) {
            alert("Vui lòng chọn khách hàng để xóa!");
            return;
        }
        checked.forEach(cb => cb.closest("tr").remove());
    });

    // Chọn tất cả
    const selectAll = document.getElementById("selectAll");
    selectAll?.addEventListener("click", () => {
        const checkboxes = document.querySelectorAll(".row-checkbox");
        checkboxes.forEach(cb => cb.checked = selectAll.checked);
    });
});
