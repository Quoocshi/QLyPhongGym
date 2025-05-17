// Mảng chứa các đường dẫn ảnh
const bannerImages = [
    '/images/linhtinh.jpg',
    '/images/linhtinh2.webp',
    '/images/linhtinh3.webp'  
];

let currentImageIndex = 0;
const bannerSlider = document.getElementById('bannerSlider');
const dots = document.querySelectorAll('.slider-dot');
const prevArrow = document.getElementById('prevArrow');
const nextArrow = document.getElementById('nextArrow');
const loginModal = document.getElementById('loginModal');
const loginButton = document.getElementById('loginButton');
const closeButton = document.querySelector('.close');

// Kiểm tra URL có tham số error không
window.onload = function() {
    // Kiểm tra nếu URL có chứa tham số error
    if (window.location.href.includes('?error') || window.location.href.includes('&error')) {
        // Hiển thị modal
        loginModal.style.display = 'flex';
    }
};

// Tạo các banner slides
bannerImages.forEach(image => {
    const slide = document.createElement('div');
    slide.className = 'banner';
    slide.style.backgroundImage = `url('${image}')`;
    bannerSlider.appendChild(slide);
});

// Hàm cập nhật đánh dấu slider dots
function updateDots(index) {
    dots.forEach(dot => {
        dot.classList.remove('active');
    });
    dots[index].classList.add('active');
}

// Hàm chuyển đến ảnh tiếp theo với hiệu ứng trượt ngang
function nextImage() {
    currentImageIndex = (currentImageIndex + 1) % bannerImages.length;
    bannerSlider.style.transform = `translateX(-${currentImageIndex * 100}%)`;
    updateDots(currentImageIndex);
}

// Hàm chuyển đến ảnh trước đó với hiệu ứng trượt ngang
function prevImage() {
    currentImageIndex = (currentImageIndex - 1 + bannerImages.length) % bannerImages.length;
    bannerSlider.style.transform = `translateX(-${currentImageIndex * 100}%)`;
    updateDots(currentImageIndex);
}

// Thêm sự kiện click cho mũi tên
nextArrow.addEventListener('click', nextImage);
prevArrow.addEventListener('click', prevImage);

// Thêm sự kiện click cho dots
dots.forEach(dot => {
    dot.addEventListener('click', function() {
        const index = parseInt(this.getAttribute('data-index'));
        currentImageIndex = index;
        bannerSlider.style.transform = `translateX(-${currentImageIndex * 100}%)`;
        updateDots(currentImageIndex);
    });
});

// Tự động chuyển ảnh sau mỗi 5 giây
setInterval(nextImage, 5000);

// Mở modal khi nhấn nút đăng nhập
loginButton.onclick = function() {
    loginModal.style.display = 'flex';
}

// Đóng modal khi nhấn dấu X
closeButton.onclick = function() {
    loginModal.style.display = 'none';
    
    // Nếu có tham số error trong URL, xóa nó để tránh hiển thị modal lại khi tải lại trang
    if (window.location.href.includes('?error') || window.location.href.includes('&error')) {
        // Tạo đối tượng URL từ URL hiện tại
        let url = new URL(window.location.href);
        // Xóa tham số error
        url.searchParams.delete('error');
        // Thay đổi URL mà không reload trang
        window.history.pushState({}, '', url);
    }
}

// Đóng modal khi nhấn vào vùng bên ngoài
window.onclick = function(event) {
    if (event.target == loginModal) {
        loginModal.style.display = 'none';
        
        // Nếu có tham số error trong URL, xóa nó
        if (window.location.href.includes('?error') || window.location.href.includes('&error')) {
            let url = new URL(window.location.href);
            url.searchParams.delete('error');
            window.history.pushState({}, '', url);
        }
    }
}

const registerModal = document.getElementById('registerModal');

const registerButton = document.getElementById('registerButton');

const closeLogin = document.querySelector('.close');
const closeRegister = document.querySelector('.close-register');

const switchToRegister = document.querySelector('.login-footer a');
const switchToLogin = document.getElementById('switchToLogin');

// Mở modal
loginButton.onclick = () => loginModal.style.display = 'flex';
registerButton.onclick = () => registerModal.style.display = 'flex';

// Đóng modal
closeLogin.onclick = () => loginModal.style.display = 'none';
closeRegister.onclick = () => registerModal.style.display = 'none';

// Chuyển modal
switchToRegister.onclick = (e) => {
    e.preventDefault();
    loginModal.style.display = 'none';
    registerModal.style.display = 'flex';
};
switchToLogin.onclick = (e) => {
    e.preventDefault();
    registerModal.style.display = 'none';
    loginModal.style.display = 'flex';
};

// Đóng khi click bên ngoài
window.onclick = function(event) {
    if (event.target === loginModal) loginModal.style.display = 'none';
    if (event.target === registerModal) registerModal.style.display = 'none';
};

// ✅ Validate password khớp nhau
function validateRegisterForm() {
    const password = document.getElementById("reg-password").value;
    const confirm = document.getElementById("reg-confirm").value;
    const errorBox = document.getElementById("registerError");

    if (password !== confirm) {
        errorBox.textContent = "Mật khẩu xác nhận không khớp.";
        errorBox.style.display = "block";
        return false;
    }

    errorBox.style.display = "none";
    return true;
}
// ✅ Nếu server trả về yêu cầu hiển thị lại modal đăng ký (thông qua Thymeleaf)
document.addEventListener("DOMContentLoaded", function () {
    // Biến này sẽ được Thymeleaf render từ server
    const showRegisterModal = /*[[${showRegisterModal}]]*/ false;

    if (showRegisterModal) {
        registerModal.style.display = "flex";
    }
});






