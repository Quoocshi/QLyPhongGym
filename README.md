# Hệ Thống Quản Lý Phòng Gym (QLyPhongGym)

<!-- Banner -->
<img width="1906" height="1069" alt="image" src="https://github.com/user-attachments/assets/3a2e5fa8-0e84-46cc-8a0b-0855b20f91f5" />


> **Ứng dụng web quản lý phòng tập Gym toàn diện** — Hỗ trợ quản lý khách hàng, nhân viên, dịch vụ, lịch tập, thanh toán trực tuyến và chat real-time giữa nhân viên và khách hàng.

---

## Mục Lục

- [Giới Thiệu](#gioi-thieu)
- [Tính Năng Chính](#tinh-nang-chinh)
- [Công Nghệ Sử Dụng](#cong-nghe-su-dung)
- [Ảnh Chụp Màn Hình](#anh-chup-man-hinh)
- [Vấn Đề Được Giải Quyết](#van-de-duoc-giai-quyet)
- [Bài Học Rút Ra](#bai-hoc-rut-ra)

---

<a id="gioi-thieu"></a>

## Giới Thiệu

**QLyPhongGym** là một hệ thống quản lý phòng tập Gym được xây dựng như một dự án học tập, nhằm số hóa toàn bộ quy trình vận hành của phòng tập — từ đăng ký thành viên, quản lý dịch vụ, phân bổ lịch tập đến thanh toán trực tuyến và chat hỗ trợ real-time.

Hệ thống phân quyền rõ ràng cho **4 vai trò** (Role):
| Vai trò | Mô tả |
|---------|-------|
| **Admin** | Quản lý toàn bộ hệ thống: nhân viên, khách hàng, dịch vụ, bộ môn, doanh thu |
| **Staff (Lễ tân)** | Hỗ trợ quản lý khách hàng, dịch vụ, chat với khách hàng |
| **Trainer (Huấn luyện viên)** | Xem lịch dạy lớp, lịch PT cá nhân |
| **User (Khách hàng)** | Đăng ký dịch vụ, xem lịch tập, thanh toán, chat hỗ trợ |

---

<a id="tinh-nang-chinh"></a>

## Tính Năng Chính

### Quản Lý Người Dùng
- Đăng ký / Đăng nhập tài khoản (hỗ trợ **Google OAuth 2.0**)
- Phân quyền dựa trên Role Group (RBAC)
- Quản lý hồ sơ khách hàng & nhân viên (CRUD)

### Quản Lý Dịch Vụ & Bộ Môn
- Quản lý bộ môn (Gym, Yoga, Zumba, Cardio, CrossFit, Bơi lội...)
- Quản lý dịch vụ: Tự do, Lớp học, PT (Personal Trainer)
- Đăng ký dịch vụ với kiểm tra xung đột lịch tự động

### Quản Lý Lịch Tập & Lớp Học
- Tạo và quản lý lớp học theo bộ môn
- Phân lịch dạy cho Trainer
- Lịch tập cá nhân 1-1 với PT
- Xem lịch tập của khách hàng

### Thanh Toán & Hóa Đơn
- Tích hợp thanh toán **MoMo** (e-wallet)
- Quản lý hóa đơn (tạo, theo dõi trạng thái)
- Hệ thống Voucher & chương trình ưu đãi

### Thống Kê & Báo Cáo
- Thống kê doanh thu tổng quan
- Xuất báo cáo ra file **Excel** (Apache POI)
- Dashboard thống kê cho Admin

### Chat Real-time
- Chat trực tiếp giữa khách hàng và nhân viên qua **WebSocket/STOMP**
- Quản lý cuộc trò chuyện, lịch sử tin nhắn
- Phân công cuộc trò chuyện cho nhân viên

### Bảo Mật
- Xác thực bằng **JWT** (JSON Web Token)
- Đăng nhập qua **Google OAuth 2.0**
- Mã hóa mật khẩu với **BCrypt**
- Bảo mật API với **Spring Security**

---

<a id="cong-nghe-su-dung"></a>

## Công Nghệ Sử Dụng

### Backend
| Công nghệ | Phiên bản | Mô tả |
|-----------|-----------|-------|
| **Java** | 21 | Ngôn ngữ lập trình chính |
| **Spring Boot** | 3.4.5 | Framework chính cho backend |
| **Spring Data JPA** | — | ORM, tương tác cơ sở dữ liệu |
| **Spring Security** | — | Xác thực & phân quyền |
| **Spring WebSocket** | — | Chat real-time (STOMP protocol) |
| **JWT (java-jwt)** | 4.5.0 | Token-based authentication |
| **OAuth 2.0 + Google API** | — | Đăng nhập bằng Google |
| **SpringDoc OpenAPI** | 2.8.5 | Swagger UI cho API documentation |
| **Apache POI** | 5.4.1 | Xuất báo cáo Excel |
| **Lombok** | 1.18.42 | Giảm boilerplate code |
| **Maven** | — | Build tool & quản lý dependency |

### Frontend
| Công nghệ | Mô tả |
|-----------|-------|
| **React JS** | Thư viện JavaScript xây dựng giao diện người dùng |
| **HTML5 / CSS3** | Cấu trúc & giao diện |
| **SockJS + STOMP.js** | WebSocket client cho chat |

### Database
| Công nghệ | Mô tả |
|-----------|-------|
| **PostgreSQL** | Cơ sở dữ liệu chính (production) |
| **Oracle Database** | Cơ sở dữ liệu thay thế |
| **PL/SQL & PL/pgSQL** | Stored Procedures, Functions, Triggers |

### DevOps & Tools  
| Công nghệ | Mô tả |
|-----------|-------|
| **Docker** | Container hóa ứng dụng (multi-stage build) |
| **Git** | Quản lý phiên bản mã nguồn |
| **MoMo API** | Tích hợp cổng thanh toán |

---

<a id="anh-chup-man-hinh"></a>

## Ảnh Chụp Màn Hình

### Trang Đăng Nhập / Đăng Ký
<!-- Thêm ảnh trang đăng nhập -->
<img width="1732" height="836" alt="image" src="https://github.com/user-attachments/assets/8ea96f76-33af-4702-a39d-b0970509d523" />


<!-- Thêm ảnh trang đăng ký -->
<img width="1467" height="690" alt="image" src="https://github.com/user-attachments/assets/932ed243-4de1-4d9e-b793-20ef000eddca" />

---

### Giao Diện Admin
<!-- Thêm ảnh trang chủ Admin -->
<img width="1251" height="608" alt="image" src="https://github.com/user-attachments/assets/77f77bdc-a86a-474a-b74b-49a741ea3416" />

<!-- Thêm ảnh quản lý nhân viên -->
<img width="709" height="651" alt="image" src="https://github.com/user-attachments/assets/8ca603c5-1448-4b78-9d55-4231f171d2f6" />

---

### Giao Diện Staff
<!-- Thêm ảnh trang chủ Staff -->
<img width="1851" height="883" alt="image" src="https://github.com/user-attachments/assets/10565041-8331-4621-b8c1-873fde53b479" />

<img width="1252" height="575" alt="image" src="https://github.com/user-attachments/assets/921e3f4c-0de8-47e3-8d85-bccad8a4df98" />

<img width="528" height="581" alt="image" src="https://github.com/user-attachments/assets/c176e8a7-24dc-435f-be98-e74f2b35fe3d" />

<img width="1396" height="639" alt="image" src="https://github.com/user-attachments/assets/644654a8-fe7b-4693-9568-35cef219d121" />

<img width="647" height="594" alt="image" src="https://github.com/user-attachments/assets/175d69a1-ecf4-4ec9-8aa7-7056e516e18a" />

<!-- Thêm ảnh chat hỗ trợ khách hàng -->
<img width="1685" height="776" alt="image" src="https://github.com/user-attachments/assets/4800ccc9-8a3e-4fb3-a05b-c141cb504ae8" />

---

### Giao Diện Trainer
<!-- Thêm ảnh lịch dạy Trainer -->
<img width="636" height="751" alt="image" src="https://github.com/user-attachments/assets/1649e7a5-34ef-4ee2-b638-3aa9bee3b248" />

<img width="1315" height="614" alt="image" src="https://github.com/user-attachments/assets/33e21c0e-4d44-4cb5-9d29-32a25833651b" />

<img width="1685" height="776" alt="image" src="https://github.com/user-attachments/assets/6f93d4dc-ca2b-416d-946e-b0ac6843e00a" />

---

### Giao Diện User (Khách Hàng)
<!-- Thêm ảnh trang chủ khách hàng -->
<img width="1012" height="489" alt="image" src="https://github.com/user-attachments/assets/e30f1678-13be-4ba7-8ebd-2bdd2fedf780" />

<!-- Thêm ảnh đăng ký dịch vụ -->
<img width="1537" height="716" alt="image" src="https://github.com/user-attachments/assets/4db7e3a1-5c5f-4a32-99ba-3fd71723bc94" />


<!-- Thêm ảnh lịch tập -->
<img width="835" height="504" alt="image" src="https://github.com/user-attachments/assets/0ffa3ba3-d20a-41c0-b837-447e810a0a1e" />

<img width="948" height="560" alt="image" src="https://github.com/user-attachments/assets/b1421c6b-0101-4c27-a310-1b60b8a87098" />

<!-- Thêm ảnh thanh toán -->
<img width="605" height="668" alt="image" src="https://github.com/user-attachments/assets/63d2389e-874d-497f-8f7d-4dcfb8254c52" />

<img width="702" height="678" alt="image" src="https://github.com/user-attachments/assets/bdf851cf-4ea3-4994-bf5c-311ab924c172" />


<!-- Thêm ảnh chat với nhân viên -->
<img width="1629" height="750" alt="image" src="https://github.com/user-attachments/assets/ae31a17b-8c61-48a0-b05e-93077723e4a5" />

---

---

<a id="van-de-duoc-giai-quyet"></a>

## Vấn Đề Được Giải Quyết

| # | Vấn đề thực tế | Giải pháp của hệ thống |
|---|----------------|----------------------|
| 1 | Quản lý thông tin khách hàng & nhân viên thủ công, dễ sai sót | Hệ thống CRUD số hóa toàn bộ, tìm kiếm nhanh, autocomplete |
| 2 | Đăng ký dịch vụ bị trùng lịch, xung đột ca tập | Kiểm tra xung đột lịch tự động khi đăng ký dịch vụ |
| 3 | Thanh toán bất tiện, chỉ nhận tiền mặt | Tích hợp thanh toán online qua MoMo |
| 4 | Khó theo dõi doanh thu, không có báo cáo | Thống kê doanh thu real-time, xuất Excel |
| 5 | Khách hàng khó liên hệ hỗ trợ | Chat real-time qua WebSocket |
| 6 | Phân quyền không rõ ràng giữa các vai trò | Hệ thống RBAC phân quyền chi tiết (Admin, Staff, Trainer, User) |
| 7 | Đăng nhập nhiều hệ thống rắc rối | Hỗ trợ đăng nhập nhanh qua Google OAuth 2.0 |
| 8 | Khó mở rộng, triển khai phức tạp | Docker hóa ứng dụng, dễ dàng deploy |

---

<a id="bai-hoc-rut-ra"></a>

## Bài Học Rút Ra

### Kỹ Thuật
- **Spring Boot Ecosystem**: Nắm vững cách sử dụng Spring Boot kết hợp với nhiều module (Security, JPA, WebSocket, OAuth2) trong một dự án thực tế.
- **Thiết kế CSDL quan hệ**: Thực hành thiết kế 20+ bảng liên kết phức tạp, viết **Stored Procedures**, **Functions**, **Triggers** trên cả Oracle và PostgreSQL.
- **Bảo mật ứng dụng**: Hiểu sâu về JWT, OAuth2 flow, RBAC, mã hóa mật khẩu, bảo mật WebSocket.
- **Real-time communication**: Triển khai WebSocket với STOMP protocol cho chức năng chat real-time.
- **Tích hợp thanh toán**: Tích hợp cổng thanh toán MoMo, xử lý callback & IPN.
- **Docker**: Viết Dockerfile với multi-stage build để tối ưu kích thước image.

### Quy Trình
- **Kiến trúc phân lớp**: Áp dụng mô hình Controller → Service → Repository một cách nhất quán.
- **DTO Pattern**: Tách biệt entity và dữ liệu truyền qua API bằng DTO + Mapper.
- **Validation**: Xử lý validation ở cả phía client (JavaScript) và server (Spring Validation).
- **API Documentation**: Sử dụng Swagger/OpenAPI để document hóa toàn bộ REST API.

---

## License

Dự án này được phát triển cho mục đích học tập.

---

<p align="center">
  ⭐ Nếu thấy hữu ích, hãy cho mình một star nhé! ⭐
</p>

