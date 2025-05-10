# Ứng dụng Cho Thuê Xe Máy

Ứng dụng quản lý cho thuê xe máy được viết bằng Java Swing và MySQL.

## Yêu cầu hệ thống

- JDK 15 hoặc cao hơn
- MySQL Server 8.0 hoặc cao hơn
- Maven 3.6.0 hoặc cao hơn

## Hướng dẫn cài đặt

### 1. Cài đặt các công cụ cần thiết

#### Cài đặt JDK 15
1. Tải JDK 15 từ trang chủ Oracle hoặc OpenJDK
2. Cài đặt JDK
3. Thiết lập biến môi trường JAVA_HOME và thêm %JAVA_HOME%\bin vào PATH

#### Cài đặt MySQL
1. Tải MySQL Server 8.0 từ trang chủ MySQL
2. Cài đặt MySQL Server
3. Ghi nhớ username và password của root user

#### Cài đặt Maven
1. Tải Maven từ trang chủ Apache Maven
2. Giải nén vào thư mục mong muốn
3. Thiết lập biến môi trường M2_HOME và thêm %M2_HOME%\bin vào PATH

### 2. Clone và cấu hình project

```bash
# Clone repository
git clone https://github.com/hoangnguyen1104/cho-thue-xe-may.git
cd cho-thue-xe-may

# Cấu hình MySQL
# Mở file src/main/java/com/chothuexemay/controller/DatabaseManager.java
# Cập nhật thông tin kết nối:
private static final String URL = "jdbc:mysql://localhost:3306/chothuexemay?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root"; // Thay đổi username nếu cần
private static final String PASSWORD = "admin"; // Thay đổi password nếu cần
```

### 3. Build và chạy ứng dụng

```bash
# Build project
mvn clean install

# Chạy ứng dụng
java -jar target/cho-thue-xe-may-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Hướng dẫn sử dụng

### Đăng nhập
- Tài khoản Admin:
  - Username: admin
  - Password: admin123
- Tài khoản User:
  - Username: user
  - Password: user123

### Quản lý xe máy (Admin)
1. Thêm xe mới:
   - Click "Thêm xe"
   - Điền thông tin: biển số, tên xe, hãng xe, giá thuê
   - Click "Lưu"

2. Sửa thông tin xe:
   - Chọn xe cần sửa
   - Click "Sửa"
   - Cập nhật thông tin
   - Click "Lưu"

3. Xóa xe:
   - Chọn xe cần xóa
   - Click "Xóa"
   - Xác nhận xóa

### Quản lý đơn thuê (Admin)
1. Xem danh sách đơn thuê
2. Tìm kiếm đơn thuê theo:
   - Tên khách hàng
   - Ngày thuê
   - Ngày trả
   - Trạng thái
3. Trả xe:
   - Chọn đơn thuê
   - Click "Trả xe"
   - Xác nhận trả xe

### Thống kê (Admin)
1. Xem thống kê theo:
   - Ngày
   - Tuần
   - Tháng
2. Thông tin hiển thị:
   - Số lượng đơn thuê
   - Tổng doanh thu

### Thuê xe (User)
1. Xem danh sách xe có sẵn
2. Thuê xe:
   - Chọn xe muốn thuê
   - Click "Thuê xe"
   - Chọn ngày trả
   - Xác nhận thuê

## Cấu trúc project

```
src/main/java/com/chothuexemay/
├── controller/         # Các class xử lý logic
│   ├── DataController.java
│   └── DatabaseManager.java
├── model/             # Các class model
│   ├── DonThue.java
│   ├── User.java
│   └── XeMay.java
└── view/              # Các class giao diện
    ├── LoginFrame.java
    ├── MainFrame.java
    ├── QuanLyDonThuePanel.java
    ├── QuanLyXePanel.java
    ├── ThongKePanel.java
    └── ThueXePanel.java
```

## Xử lý lỗi thường gặp

1. Lỗi kết nối database:
   - Kiểm tra MySQL đã được cài đặt và đang chạy
   - Kiểm tra thông tin kết nối trong DatabaseManager.java
   - Đảm bảo database chothuexemay đã được tạo

2. Lỗi không tìm thấy JDK:
   - Kiểm tra biến môi trường JAVA_HOME
   - Kiểm tra PATH có chứa %JAVA_HOME%\bin

3. Lỗi không tìm thấy Maven:
   - Kiểm tra biến môi trường M2_HOME
   - Kiểm tra PATH có chứa %M2_HOME%\bin

## Đóng góp

Mọi đóng góp đều được hoan nghênh. Vui lòng tạo issue hoặc pull request để đóng góp. 