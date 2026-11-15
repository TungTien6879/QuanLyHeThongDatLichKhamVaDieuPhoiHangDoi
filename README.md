# MEDCARE - Hệ thống đặt lịch khám và điều phối hàng đợi

MEDCARE là ứng dụng web hỗ trợ bệnh nhân đặt lịch khám và giúp nhân viên, bác sĩ
theo dõi quy trình tiếp nhận, khám bệnh. Đây là đồ án/MVP để chạy và thử nghiệm
trên máy cá nhân, chưa phải phần mềm dùng cho vận hành y tế thực tế.

## Công nghệ sử dụng

- Java 25
- Spring Boot 4.1.2, Spring MVC, Spring Security
- Thymeleaf
- Spring Data JPA và Hibernate
- Microsoft SQL Server / SQL Server Express
- Maven Wrapper

## Chức năng chính

- Bệnh nhân đăng ký tài khoản, đặt lịch, xem lịch đã đặt và hủy lịch.
- Nhân viên xác nhận lịch, check-in bệnh nhân và xác nhận mức độ ưu tiên.
- Bác sĩ xem hàng đợi, bắt đầu khám và hoàn tất lượt khám.
- Quản trị viên tạo tài khoản bác sĩ và nhân viên.
- Hệ thống lưu lịch hẹn, trạng thái và thông báo trong SQL Server.

## Yêu cầu trước khi chạy

Cài đặt và chuẩn bị:

1. **JDK 25**. Kiểm tra bằng `java -version`; kết quả cần hiển thị phiên bản 25.
2. **SQL Server** hoặc **SQL Server Express**, cùng công cụ quản lý như SSMS.
3. SQL Server đang chạy, bật kết nối TCP/IP ở cổng `1433`, và cho phép đăng nhập
   SQL Server Authentication bằng tài khoản `sa`.
4. Mở đúng thư mục dự án `quanlylichkham` trong VS Code — thư mục chứa `pom.xml`
   và `mvnw.cmd`.

Không cần cài Maven riêng; dự án có Maven Wrapper (`mvnw.cmd`).

## Cài đặt và chạy trên Windows

### 1. Tạo database

Mở file `database/01-tao-database.sql` bằng SSMS và chạy script. Script tạo
database `QuanLyLichKham` nếu database chưa tồn tại.

Các bảng còn lại được Hibernate tạo/cập nhật tự động khi ứng dụng khởi động
(`spring.jpa.hibernate.ddl-auto=update`).

### 2. Cấu hình mật khẩu SQL Server

Mở PowerShell tại thư mục `quanlylichkham` và nhập:

```powershell
$secure = Read-Host "Mat khau SQL Server cho tai khoan sa" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secure).Password
```

Mặc định ứng dụng kết nối tới SQL Server tại `127.0.0.1:1433`, database
`QuanLyLichKham`, bằng tài khoản `sa`. Nếu môi trường của bạn dùng máy chủ, cổng,
database hoặc tài khoản khác, hãy điều chỉnh cấu hình datasource trong
`src/main/resources/application.properties`.

### 3. Tạo tài khoản quản trị ban đầu

Trong cùng cửa sổ PowerShell, đặt email và mật khẩu quản trị. Mật khẩu phải có
ít nhất 10 ký tự:

```powershell
$env:ADMIN_EMAIL = "admin@medcare.local"
$adminSecure = Read-Host "Mat khau quan tri (toi thieu 10 ky tu)" -AsSecureString
$env:ADMIN_PASSWORD = [System.Net.NetworkCredential]::new("", $adminSecure).Password
```

Tài khoản quản trị chỉ được tạo khi email đó chưa có trong database. Nếu tài
khoản đã tồn tại, đặt lại các biến môi trường không làm thay đổi mật khẩu cũ.

### 4. Khởi động ứng dụng

Chạy lệnh sau trong cửa sổ PowerShell vừa cấu hình:

```powershell
.\mvnw.cmd spring-boot:run
```

Khi Maven tải các thư viện và ứng dụng khởi động thành công, mở:

**http://localhost:8080**

Đăng nhập bằng email `ADMIN_EMAIL` và mật khẩu quản trị đã nhập ở bước trước.
Đây là mật khẩu ứng dụng, không phải mật khẩu SQL Server.

Để dừng ứng dụng, quay lại cửa sổ PowerShell và nhấn `Ctrl+C`. Các biến môi
trường được đặt theo cách trên chỉ tồn tại trong cửa sổ PowerShell hiện tại.

## Luồng dùng thử

1. Đăng nhập quản trị và mở trang `/admin` để tạo tài khoản bác sĩ và nhân viên.
2. Đăng xuất quản trị. Tạo tài khoản bệnh nhân tại `/dang-ky`.
3. Đăng nhập bệnh nhân, đặt lịch và xem lịch tại `/lich-cua-toi`.
4. Đăng nhập tài khoản nhân viên, xác nhận lịch và check-in bệnh nhân tại
   `/nhan-vien`; xác nhận ưu tiên nếu phù hợp.
5. Đăng nhập tài khoản bác sĩ, mở `/bac-si`, bắt đầu khám và hoàn tất lượt khám.
6. Bệnh nhân có thể mở lại trang lịch của mình để xem trạng thái và thông báo.

Chỉ bệnh nhân tự đăng ký. Tài khoản quản trị, bác sĩ và nhân viên cần được tạo
hoặc cấp theo đúng luồng của ứng dụng.

## Cấu trúc dự án

```text
quanlylichkham/
├── database/          # Script tạo database
├── src/main/java/     # Mã nguồn Java
├── src/main/resources/
│   ├── static/        # CSS và tài nguyên tĩnh
│   └── templates/     # Giao diện Thymeleaf
├── src/test/          # Kiểm thử
├── pom.xml            # Cấu hình Maven và dependencies
└── mvnw.cmd           # Maven Wrapper cho Windows
```

## Kiểm thử và đóng gói

Các lệnh sau chạy trong thư mục dự án, nơi có `mvnw.cmd`:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

Kiểm thử khởi động ứng dụng cần kết nối được tới SQL Server đã cấu hình.
Sau khi đóng gói, có thể chạy file JAR trong `target` bằng:

```powershell
java -jar .\target\quanlylichkham-0.0.1-SNAPSHOT.jar
```

Trước khi chạy JAR, vẫn cần đặt các biến môi trường `DB_PASSWORD`,
`ADMIN_EMAIL` và `ADMIN_PASSWORD` như hướng dẫn ở trên.

## Xử lý lỗi thường gặp

- **`java -version` hiển thị phiên bản thấp hơn 25:** cài JDK 25 và cập nhật
  `JAVA_HOME`/`PATH`, sau đó mở lại PowerShell.
- **Không kết nối được SQL Server:** kiểm tra dịch vụ SQL Server đang chạy,
  TCP/IP đã bật ở cổng `1433`, database `QuanLyLichKham` đã được tạo và mật khẩu
  `sa` đúng.
- **Lỗi đăng nhập database:** đặt lại `DB_PASSWORD` trong đúng cửa sổ PowerShell
  đang chạy ứng dụng. Tài khoản datasource mặc định là `sa`.
- **Không đăng nhập được quản trị:** kiểm tra email/mật khẩu ban đầu. Nếu email
  quản trị đã tồn tại từ lần chạy trước, ứng dụng không tự đặt lại mật khẩu.
- **Cổng `8080` đang được sử dụng:** dừng ứng dụng khác đang dùng cổng đó hoặc
  đổi cổng cấu hình trước khi chạy.

## Giới hạn và lưu ý

- Thời gian chờ chỉ là ước tính: 15 phút cho mỗi bệnh nhân đang chờ trước đó của
  cùng bác sĩ; không phải dự đoán thời gian thực.
- Thứ tự hàng đợi dựa trên ưu tiên do nhân viên xác nhận rồi đến thời điểm
  check-in. Hệ thống không tự chẩn đoán hoặc phân loại cấp cứu.
- Mỗi bác sĩ chỉ có một lịch tại một thời điểm 30 phút; khung giờ đã hủy hiện
  chưa được tái sử dụng.
- Thông báo được lưu trong database và hiển thị khi tải lại trang; ứng dụng
  chưa gửi email/SMS hoặc cập nhật thời gian thực qua WebSocket.
- `ddl-auto=update` và `trustServerCertificate=true` chỉ phù hợp cho phát triển
  cục bộ. Không dùng cấu hình hiện tại cho môi trường production. Hệ thống thực
  tế cần migration, TLS được xác thực, kiểm thử đầy đủ, ghi log/audit, bảo vệ
  dữ liệu bệnh nhân và chính sách sao lưu.
