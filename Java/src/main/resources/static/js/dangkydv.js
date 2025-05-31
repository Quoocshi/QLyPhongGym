// Mảng lưu trữ các dịch vụ đã đăng ký
let registeredServices = [];

// Mapping tên dịch vụ sang mã dịch vụ
const serviceCodeMapping = {
    'GYM': 'GYM',
    'YOGA': 'YOGA', 
    'ZUMBA': 'ZUMBA',
    'CARDIO': 'CARDIO',
    'BƠI': 'BƠI',
    'GYM PT': 'GYMPT'
};

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.service-card').forEach(card => {
        card.addEventListener('click', function(e) {
            e.preventDefault();

            // Lấy thông tin thẻ từ data attributes và DOM
            const maDV = this.dataset.maDv;
            const img = this.querySelector('img').src;
            const title = this.querySelector('.service-title').innerText;
            const desc = this.querySelector('.service-desc').innerHTML;
            const price = this.querySelector('.service-price').innerText;

            // Kiểm tra nếu là dịch vụ "Tự do" (GYM, CARDIO, BƠI)
            if (desc.includes('Tự do')) {
                // Ẩn các slider
                document.querySelectorAll('.slider-container').forEach(s => s.style.display = 'none');

                // Hiển thị modal chi tiết ở giữa màn hình
                const modal = document.getElementById('service-detail-modal');
                modal.innerHTML = `
                    <div class="service-detail-card">
                        <img src="${img}" style="width:100%; height:100%; object-fit:cover; border-radius:20px;">
                        <div class="service-detail-content">
                            <h2>${title}</h2>
                            <div>${desc}</div>
                            <div>${price}</div>
                        </div>
                        <a href="#" onclick="registerService('${title}', '${desc}', '${maDV}')" class="register-btn">ĐĂNG KÝ</a>
                        <button onclick="closeModal()" class="back-btn">Quay lại</button>
                    </div>
                `;
                modal.style.display = 'block';

                // Thêm sự kiện click ra ngoài để đóng modal
                modal.addEventListener('click', function(e) {
                    if (e.target === modal) {
                        closeModal();
                    }
                });
            } else {
                // Đối với các dịch vụ khác (Lớp, PT), chỉ phóng to thẻ và hiện nút đăng ký
                // Bỏ trạng thái active ở các thẻ khác
                document.querySelectorAll('.service-card').forEach(c => {
                    c.classList.remove('active');
                    let detail = c.querySelector('.service-detail-inline');
                    if (detail) detail.remove();
                });

                // Thêm trạng thái active cho thẻ được chọn
                this.classList.add('active');

                // Thêm phần chi tiết và nút đăng ký vào thẻ
                const detailDiv = document.createElement('div');
                detailDiv.className = 'service-detail-inline';
                detailDiv.innerHTML = `
                    <a href="#" onclick="registerService('${title}', '${desc}', '${maDV}')" class="register-btn">ĐĂNG KÝ</a>
                `;
                this.appendChild(detailDiv);
            }
        });
    });
});

// Hàm đăng ký dịch vụ (cập nhật để sử dụng maDV)
function registerService(title, desc, maDV) {
    // Lấy hình thức tập luyện từ mô tả
    const trainingType = desc.match(/Hình thức tập luyện: ([^<]+)/)?.[1] || 'Không xác định';
    
    // Lấy giá tiền từ mô tả
    const priceMatch = desc.match(/Giá tiền: ([\d,.]+) VNĐ/);
    const price = priceMatch ? parseInt(priceMatch[1].replace(/[,.]/g, '')) : 0;
    
    // Kiểm tra xem dịch vụ đã được đăng ký chưa
    const existingService = registeredServices.find(service => service.code === maDV);
    if (existingService) {
        alert('Dịch vụ này đã được đăng ký!');
        return;
    }

    // Thêm dịch vụ vào danh sách đã đăng ký
    registeredServices.push({
        name: title,
        code: maDV,
        trainingType: trainingType,
        price: price
    });

    // Cập nhật hiển thị thanh dịch vụ đã đăng ký
    updateRegisteredServicesDisplay();
    updateTotalAmount();
    
    // Hiển thị thanh dịch vụ đã đăng ký
    document.getElementById('registered-services-bar').style.display = 'block';

    // Đóng modal nếu đang mở
    closeModal();

    alert(`Đã đăng ký thành công dịch vụ ${title}!`);
}

// Hàm cập nhật hiển thị danh sách dịch vụ đã đăng ký
function updateRegisteredServicesDisplay() {
    const registeredList = document.getElementById('registered-list');
    registeredList.innerHTML = '';

    registeredServices.forEach(service => {
        const serviceItem = document.createElement('div');
        serviceItem.className = 'registered-item';
        serviceItem.innerHTML = `
            <span class="registered-item-name">${service.name}</span>
            <span class="registered-item-type">${service.trainingType}</span>
        `;
        registeredList.appendChild(serviceItem);
    });
}

// Hàm toggle hiển thị/ẩn danh sách dịch vụ đã đăng ký
function toggleRegisteredServices() {
    const registeredList = document.getElementById('registered-list');
    if (registeredList.style.display === 'none') {
        registeredList.style.display = 'flex';
    } else {
        registeredList.style.display = 'none';
    }
}

// Hàm đóng modal và quay lại
function closeModal() {
    const modal = document.getElementById('service-detail-modal');
    modal.style.display = 'none';
    document.querySelectorAll('.slider-container').forEach(s => s.style.display = 'flex');
}

// Hàm cập nhật tổng tiền
function updateTotalAmount() {
    const total = registeredServices.reduce((sum, service) => sum + service.price, 0);
    const paymentBtn = document.querySelector('.payment-btn');
    
    // Disable nút thanh toán nếu không có dịch vụ nào
    if (registeredServices.length === 0) {
        paymentBtn.disabled = true;
        paymentBtn.textContent = 'THANH TOÁN';
    } else {
        paymentBtn.disabled = false;
        paymentBtn.textContent = `THANH TOÁN (${registeredServices.length})`;
    }
}

// Hàm xử lý thanh toán
function processPayment() {
    if (registeredServices.length === 0) {
        alert('Chưa có dịch vụ nào để thanh toán!');
        return;
    }
    
    console.log('Processing payment for services:', registeredServices);
    
    // Lấy form và container cho mã dịch vụ
    const form = document.getElementById('payment-form');
    const dsMaDVContainer = document.getElementById('dsMaDV-inputs');
    
    // Xóa các input cũ
    dsMaDVContainer.innerHTML = '';
    
    // Thêm input cho từng mã dịch vụ
    registeredServices.forEach(service => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'dsMaDV';
        input.value = service.code;
        dsMaDVContainer.appendChild(input);
        console.log('Added service code to form:', service.code);
    });
    
    // Log form data trước khi submit
    const formData = new FormData(form);
    console.log('Form data being submitted:');
    for (let [key, value] of formData.entries()) {
        console.log(`${key}: ${value}`);
    }
    
    // Submit form
    console.log('Submitting form to:', form.action);
    form.submit();
}

// Hàm scroll cho slider
function scrollRow(rowId, direction) {
    const row = document.getElementById(rowId);
    const cardWidth = 400 + 32; // width + gap
    const scrollAmount = cardWidth * direction;
    
    row.scrollBy({
        left: scrollAmount,
        behavior: 'smooth'
    });
} 