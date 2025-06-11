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

// Hàm lưu dịch vụ đã đăng ký vào sessionStorage
function saveRegisteredServicesToSession() {
    sessionStorage.setItem('registeredServices', JSON.stringify(registeredServices));
}

// Hàm khôi phục dịch vụ đã đăng ký từ sessionStorage
function loadRegisteredServicesFromSession() {
    const saved = sessionStorage.getItem('registeredServices');
    if (saved) {
        registeredServices = JSON.parse(saved);
        if (registeredServices.length > 0) {
            updateRegisteredServicesDisplay();
            updateTotalAmount();
            showRegisteredServicesBar();
        }
    }
    
    // Xóa thông tin selectedService sau khi quay lại để tránh conflict
    sessionStorage.removeItem('selectedService');
}

// Hàm hiển thị khung dịch vụ đã đăng ký với animation
function showRegisteredServicesBar() {
    const bar = document.getElementById('registered-services-bar');
    if (bar) {
        bar.style.display = 'block';
        bar.classList.add('show');
        
        // Cuộn trang lên đầu để người dùng thấy khung
        setTimeout(() => {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        }, 100);
    }
}

// Hàm ẩn khung dịch vụ đã đăng ký
function hideRegisteredServicesBar() {
    const bar = document.getElementById('registered-services-bar');
    if (bar) {
        bar.style.display = 'none';
        bar.classList.remove('show');
    }
}

// Hàm xóa dịch vụ đã đăng ký khỏi sessionStorage
function clearRegisteredServicesSession() {
    sessionStorage.removeItem('registeredServices');
}

document.addEventListener('DOMContentLoaded', function() {
    // Khôi phục dịch vụ đã đăng ký từ session khi trang load
    loadRegisteredServicesFromSession();
    
    // Chỉ thêm event listener cho các service-card nếu chúng tồn tại (trang dịch vụ)
    const serviceCards = document.querySelectorAll('.service-card');
    if (serviceCards.length > 0) {
        serviceCards.forEach(card => {
            card.addEventListener('click', function(e) {
                e.preventDefault();

                // Lấy thông tin thẻ từ data attributes và DOM
                const maDV = this.dataset.maDv;
                const loaiDV = this.dataset.loaiDv; // Thêm này để kiểm tra trực tiếp
                const img = this.querySelector('img').src;
                const title = this.querySelector('.service-title').innerText;
                const desc = this.querySelector('.service-desc').innerHTML;
                const price = this.querySelector('.service-price').innerText;
                
                console.log('=== DEBUG click event ===');
                console.log('MaDV:', maDV);
                console.log('Title:', title);
                console.log('LoaiDV from data:', loaiDV);
                console.log('Desc full:', desc);

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
                    // Kiểm tra nếu là dịch vụ loại "Lop" thì chuyển hướng trực tiếp
                    if (loaiDV === 'Lop') {
                        console.log('Detected Lop service, redirecting...');
                        registerService(title, desc, maDV);
                        return;
                    }
                    
                    // Đối với các dịch vụ khác (PT), chỉ phóng to thẻ và hiện nút đăng ký
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
    }
});

// Hàm đăng ký dịch vụ (cập nhật để sử dụng maDV)
function registerService(title, desc, maDV) {
    console.log('=== DEBUG registerService ===');
    console.log('Title:', title);
    console.log('Desc:', desc);
    console.log('MaDV:', maDV);
    
    // Lấy hình thức tập luyện từ mô tả
    const trainingType = desc.match(/Hình thức tập luyện: ([^<]+)/)?.[1] || 'Không xác định';
    console.log('Training Type found:', trainingType);
    console.log('Is Lớp?', trainingType === 'Lớp');
    console.log('Training Type trim:', trainingType.trim());
    console.log('Is Lớp (trimmed)?', trainingType.trim() === 'Lớp');
    
    // Kiểm tra nếu là dịch vụ loại "Lớp" thì chuyển hướng đến trang chọn lớp
    // Kiểm tra cả text và có thể có nhiều cách khác nhau
    const isLopService = trainingType.trim() === 'Lớp' || 
                         trainingType.trim() === 'Lop' ||
                         desc.includes('Lớp') ||
                         desc.includes('Lop');
    
    console.log('Is Lop service?', isLopService);
    
    if (isLopService) {
        // Lấy accountId và các thông tin cần thiết
        const accountId = document.getElementById('accountId-input')?.value || 
                         new URLSearchParams(window.location.search).get('accountId');
        
        console.log('AccountId found:', accountId);
        console.log('Hidden input exists?', !!document.getElementById('accountId-input'));
        console.log('URL params:', new URLSearchParams(window.location.search).toString());
        
        if (accountId) {
            // Lấy mã bộ môn từ URL hiện tại
            const urlParams = new URLSearchParams(window.location.search);
            const maBM = urlParams.get('maBM');
            
            // Lưu thông tin dịch vụ vào sessionStorage để sử dụng trong trang chọn lớp
            sessionStorage.setItem('selectedService', JSON.stringify({
                title: title,
                desc: desc,
                maDV: maDV,
                accountId: accountId,
                maBM: maBM
            }));
            
            const redirectUrl = `/dich-vu-gym/chonlop?maDV=${maDV}&accountId=${accountId}`;
            window.location.href = redirectUrl;
            return;
        } else {
            alert('Không tìm thấy thông tin tài khoản. Vui lòng thử lại.');
            return;
        }
    }
    
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

    // Lưu vào sessionStorage
    saveRegisteredServicesToSession();

    // Cập nhật hiển thị thanh dịch vụ đã đăng ký
    updateRegisteredServicesDisplay();
    updateTotalAmount();
    
    // Hiển thị thanh dịch vụ đã đăng ký với animation
    showRegisteredServicesBar();

    // Đóng modal nếu đang mở
    closeModal();

    // Hiển thị thông báo thành công với style đẹp hơn
    showSuccessMessage(`Đã thêm dịch vụ "${title}" vào giỏ hàng!`);
}

// Hàm hiển thị thông báo thành công
function showSuccessMessage(message) {
    // Tạo element thông báo
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #28a745;
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
        z-index: 1000;
        font-weight: 600;
        transform: translateX(100%);
        transition: transform 0.3s ease;
    `;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    // Animation hiển thị
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // Tự động ẩn sau 3 giây
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Hàm cập nhật hiển thị danh sách dịch vụ đã đăng ký
function updateRegisteredServicesDisplay() {
    const registeredList = document.getElementById('registered-list');
    if (!registeredList) return;
    
    registeredList.innerHTML = '';

    registeredServices.forEach(service => {
        const serviceDiv = document.createElement('div');
        serviceDiv.className = 'registered-item';
        serviceDiv.innerHTML = `
            <span class="registered-item-name">${service.name}</span>
            <span class="registered-item-type">${service.trainingType}</span>
            <button onclick="removeService('${service.code}')" 
                    title="Xóa dịch vụ này"
                    style="background: #ff4757; color: white; border: none; border-radius: 50%; 
                           width: 20px; height: 20px; font-size: 12px; cursor: pointer; 
                           margin-left: 8px; transition: background 0.2s;">×</button>
        `;
        registeredList.appendChild(serviceDiv);
    });
}

// Hàm xóa dịch vụ khỏi danh sách đã đăng ký
function removeService(maDV) {
    const serviceToRemove = registeredServices.find(s => s.code === maDV);
    
    if (serviceToRemove && confirm(`Bạn có chắc muốn xóa dịch vụ "${serviceToRemove.name}"?`)) {
        registeredServices = registeredServices.filter(service => service.code !== maDV);
        saveRegisteredServicesToSession(); // Cập nhật sessionStorage
        updateRegisteredServicesDisplay();
        updateTotalAmount();
        
        if (registeredServices.length === 0) {
            hideRegisteredServicesBar();
        }
        
        showSuccessMessage(`Đã xóa dịch vụ "${serviceToRemove.name}" khỏi giỏ hàng!`);
    }
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

// Hàm đóng modal
function closeModal() {
    const modal = document.getElementById('service-detail-modal');
    if (modal) {
        modal.style.display = 'none';
        // Hiển thị lại các slider
        document.querySelectorAll('.slider-container').forEach(s => s.style.display = 'block');
    }
}

// Hàm cập nhật tổng tiền
function updateTotalAmount() {
    const total = registeredServices.reduce((sum, service) => sum + service.price, 0);
    const paymentBtn = document.querySelector('.payment-btn');

    // Disable nút thanh toán nếu không có dịch vụ nào
    if (registeredServices.length === 0) {
        if (paymentBtn) {
            paymentBtn.disabled = true;
            paymentBtn.textContent = 'THANH TOÁN';
        }
    } else {
        if (paymentBtn) {
            paymentBtn.disabled = false;
            paymentBtn.textContent = `THANH TOÁN (${registeredServices.length})`;
        }
    }
}

// Hàm xử lý thanh toán (VNPay flow)
function processPayment() {
    if (registeredServices.length === 0) {
        alert('Chưa có dịch vụ nào để thanh toán!');
        return;
    }
    
    console.log('Processing payment with VNPay flow:', registeredServices);
    
    // Lấy form và container cho mã dịch vụ
    const form = document.getElementById('payment-form');
    const dsMaDVContainer = document.getElementById('dsMaDV-inputs');
    
    if (!form || !dsMaDVContainer) {
        alert('Có lỗi xảy ra! Vui lòng thử lại.');
        return;
    }
    
    // Đổi form action về endpoint tạo hóa đơn trước VNPay
    form.action = '/dich-vu-gym/thanh-toan';
    
    // Xóa các input cũ
    dsMaDVContainer.innerHTML = '';
    
    // Tính tổng tiền
    const tongTien = registeredServices.reduce((sum, service) => sum + service.price, 0);
    
    // Thêm tổng tiền (VNPay flow yêu cầu)
    const inputTongTien = document.createElement('input');
    inputTongTien.type = 'hidden';
    inputTongTien.name = 'tongtien';
    inputTongTien.value = tongTien;
    dsMaDVContainer.appendChild(inputTongTien);
    console.log('Added total amount:', tongTien);
    
    // Thêm input cho từng mã dịch vụ (VNPay flow dùng 'dichvu')
    registeredServices.forEach(service => {
        const inputDV = document.createElement('input');
        inputDV.type = 'hidden';
        inputDV.name = 'dichvu';  // VNPay flow dùng 'dichvu'
        inputDV.value = service.code;
        dsMaDVContainer.appendChild(inputDV);
        console.log('Added service code to form (VNPay flow):', service.code);
    });
    
    // Log form data trước khi submit
    const formData = new FormData(form);
    console.log('Form data being submitted (VNPay flow):');
    for (let [key, value] of formData.entries()) {
        console.log(`${key}: ${value}`);
    }
    
    // Xóa session sau khi submit thành công
    clearRegisteredServicesSession();
    
    // Submit form - sẽ tạo hóa đơn và redirect đến trang thanh toán VNPay
    console.log('Submitting form to (VNPay flow):', form.action);
    form.submit();
}

// Hàm scroll cho slider
function scrollRow(rowId, direction) {
    const row = document.getElementById(rowId);
    const scrollAmount = 300;
    row.scrollBy({
        left: direction * scrollAmount,
        behavior: 'smooth'
    });
} 