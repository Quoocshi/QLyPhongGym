Link repository github của nhóm: https://github.com/Quoocshi/QLyPhongGym

# 🏋️ HƯỚNG DẪN CHẠY WEB QUẢN LÝ PHÒNG GYM TRÊN LOCAL

Ứng dụng được phát triển bằng **Spring Boot**, **Thymeleaf** và hỗ trợ đăng nhập bằng **Google OAuth2**. Hướng dẫn sau giúp bạn chạy ứng dụng trên máy cá nhân (local).

---

## ✅ Các bước thực hiện

---

### 🧰 Bước 1: Cài đặt Java 21

#### ➤ Trên Windows/macOS/Linux:

- Truy cập: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html  
- Tải và cài đặt JDK 21
- Cấu hình biến môi trường:
  - `JAVA_HOME` trỏ tới thư mục JDK
  - Thêm vào `PATH`: `%JAVA_HOME%\bin` (Windows) hoặc `$JAVA_HOME/bin` (macOS/Linux)
- Kiểm tra trên terminal:

  java -version
````


### 📦 Bước 2: Cài đặt Maven, Spring Boot và Thymeleaf

* Cài **Maven**:

  * Windows/macOS: tải từ [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) và giải nén
  * macOS (Homebrew):
    brew install maven
    ```

* Maven sẽ tự động xử lý Spring Boot và Thymeleaf thông qua `pom.xml`, không cần cài riêng.

* Kiểm tra Maven trên terminal:
  mvn -version
  ```

---

### 🔐 Bước 3: Lấy cấu hình OAuth2 của Google

1. Truy cập: [https://console.cloud.google.com/](https://console.cloud.google.com/)

2. Tạo project mới hoặc dùng project sẵn có

3. Vào **API & Services > Credentials**

4. Chọn **Create Credentials > OAuth client ID**

5. Application type: `Web Application`

6. Thêm redirect URI:

   ```
   http://localhost:8080/login/oauth2/code/google
   ```

7. Lưu lại:

   * **Client ID**
   * **Client Secret**

8. Mở file `src/main/resources/application.properties` và thêm:

   ```properties
   spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
   spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
---

### 📥 Bước 4: Clone project từ GitHub

Trong terminal hãy chuyển đến đường dẫn bạn muốn chứa thư mục project:
git clone https://github.com/https://github.com/Quoocshi/QLyPhongGym
cd QlyPhongGym
```

### 💻 Bước 5: Mở terminal và chuyển đến thư mục chứa file `pom.xml`

/QlyPhongGym cd java

> Đảm bảo bạn đang ở đúng thư mục java chứa file `pom.xml`.

---

### ⚙️ Bước 6: Build project

/QlyPhongGym/Java mvn clean install

> Lệnh này sẽ tải dependencies và biên dịch ứng dụng.

Đồng thời cài thêm dbForge Studio for oracle và execute các file QlyPhongGym.sql SampleData.sql DangKyDichVu_Procedure.sql và Funtions+Procedures.sql

### 🚀 Bước 7: Chạy ứng dụng
/QlyPhongGym/Java mvn spring-boot:run

---

### 🌐 Bước 8: Truy cập ứng dụng

Mở trình duyệt và vào địa chỉ:
http://localhost:8080
```

Bạn sẽ được chuyển đến trang đăng nhập của web.

---

## 📝 Ghi chú

* Đảm bảo cấu hình OAuth2 đúng redirect URI:
  `http://localhost:8080/login/oauth2/code/google`
* Bạn cần cài thêm dbForge Studio for oracle và cấu hình trong `application.properties`

---

## 📄 License

Dự án sử dụng cho mục đích học tập, nội bộ hoặc triển khai thử nghiệm.


