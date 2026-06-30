# TỔNG HỢP KIẾN THỨC DỰ ÁN (PROJECT KNOWLEDGE)

Chào bạn, với tư cách là một Senior Developer, mình đã review qua toàn bộ source code của project `laptop_shop` và tổng hợp lại những kiến thức cốt lõi mà bạn cần phải nắm vững để có thể làm chủ, maintain cũng như phát triển dự án này một cách tốt nhất. 

---

## 1. Kiến Trúc & Công Nghệ Cốt Lõi (Tech Stack)

Dự án này được xây dựng dựa trên hệ sinh thái **Spring Boot** với mô hình **MVC (Model-View-Controller)** truyền thống kết hợp Server-Side Rendering (hiển thị giao diện từ server).

### Backend
*   **Ngôn ngữ:** Java 21 (Phiên bản LTS mới nhất, hỗ trợ nhiều features hiện đại như Pattern Matching, Virtual Threads...).
*   **Framework chính:** Spring Boot 4.1.0.
*   **Database:** MySQL.
*   **ORM (Object-Relational Mapping):** Spring Data JPA (dựa trên Hibernate) giúp thao tác với database thông qua các Object thay vì viết SQL thuần.
*   **Bảo mật:** Spring Security (Dùng để xác thực Authentication và phân quyền Authorization).

### Frontend (Giao diện)
*   **Template Engine:** Thymeleaf. 
*   Đặc biệt có sử dụng thư viện `thymeleaf-extras-springsecurity6` để tùy biến giao diện dựa trên quyền của người dùng đang đăng nhập (ví dụ: chỉ Admin mới thấy nút Xóa/Sửa).

### Các thư viện hỗ trợ cực kỳ quan trọng
*   **Lombok:** Giúp sinh tự động các hàm `Getter`, `Setter`, `Constructor`, `Builder`... giảm thiểu boilerplate code (code dư thừa).
*   **MapStruct:** Tự động sinh ra code để mapping dữ liệu giữa các Object với nhau (cụ thể ở đây là giữa `Entity` và `DTO`).
*   **Spring Boot Validation:** Để kiểm tra tính hợp lệ của dữ liệu đầu vào (ví dụ: Không được để trống, độ dài tối đa...) ngay từ lớp DTO.

---

## 2. Cấu Trúc Thư Mục & Vai Trò (Project Structure)

Đây là chuẩn cấu trúc thư mục của một dự án Spring Boot tốt. Luồng đi của dữ liệu sẽ chia thành từng Layer rõ ràng:

*   **`entity`**: Chứa các class đại diện cho các bảng trong cơ sở dữ liệu (`Category`, `Product`, `User`...).
*   **`dto` (Data Transfer Object)**: Các object chuyên dùng để nhận dữ liệu từ client gửi lên hoặc trả dữ liệu về. *Tuyệt đối không dùng Entity để nhận dữ liệu trực tiếp từ Form/Client.*
*   **`mapper`**: Chứa các Interface dùng **MapStruct** để định nghĩa cách chuyển đổi từ `Entity -> DTO` và ngược lại `DTO -> Entity`.
*   **`repository`**: Các Interface kế thừa `JpaRepository`. Layer này chịu trách nhiệm giao tiếp trực tiếp với database (CRUD: Thêm, sửa, xóa, tìm kiếm).
*   **`service`**: **Trái tim của ứng dụng**. Nơi chứa toàn bộ logic nghiệp vụ (Business Logic). Tầng này sẽ gọi xuống `repository` để lấy/lưu dữ liệu.
*   **`controller`**: Nơi tiếp nhận Request từ người dùng (HTTP GET, POST...). Controller chỉ nên làm nhiệm vụ: Nhận request -> Gọi Service xử lý -> Trả về View (tên file html trong Thymeleaf) hoặc Redirect.
*   **`config`**: Chứa các file cấu hình bằng code Java:
    *   `Security.java`: Cấu hình phân quyền truy cập các đường dẫn, form đăng nhập, đăng xuất...
    *   `WebMvcConfig.java`: Cấu hình resource mapping (ví dụ map đường dẫn `/images/**` vào thư mục lưu ảnh ở ổ cứng).
*   **`exception`**: Chứa Global Exception Handler để bắt và xử lý lỗi tập trung, tránh crash app.
*   **`enums`**: Chứa các hằng số cố định (Ví dụ: `Role` của user, `Status` của đơn hàng).

---

## 3. Luồng Chạy Dữ Liệu Thực Tế (Data Flow)

Để dễ hình dung, khi một user bấm "Thêm danh mục (Category)":

1.  **View (Thymeleaf)**: Người dùng điền form tại trang `categories/add.html` và bấm Submit (POST).
2.  **Controller (`CategoryController`)**: Nhận dữ liệu dưới dạng `CategoryDTO`. Tại đây có annotation `@Valid` để kiểm tra lỗi đầu vào.
3.  **Service**: Nhận `CategoryDTO` từ Controller. Xử lý logic (nếu có, ví dụ check trùng tên). Sau đó dùng `Mapper` đổi `CategoryDTO` sang `Category` (Entity).
4.  **Repository**: Service gọi lệnh `repository.save(categoryEntity)` để lưu xuống DB.
5.  **Controller**: Controller nhận kết quả từ Service và thực hiện chuyển hướng (`redirect:/categories`) hoặc hiển thị thông báo thành công trên View.

---

## 4. Các Cấu Hình Đặc Biệt Cần Lưu Ý (`application.properties`)

Dựa vào file properties của bạn, có một vài điểm mình note lại để bạn nhớ:

*   **Cổng ứng dụng (Port):** App đang không chạy cổng 8080 mặc định mà chạy ở cổng **`15234`**. 
*   **Xử lý Upload File (Hình ảnh):** 
    *   Toàn bộ file upload đang được lưu vật lý tại: `C:/image/`. Nếu đem code qua máy khác (hoặc deploy lên server Linux) thì bắt buộc phải cấu hình lại đường dẫn này, nếu không sẽ lỗi.
    *   Max size của một file ảnh/request là: `10MB`.
*   **Quản lý Database Schema:** `spring.jpa.hibernate.ddl-auto=update` -> Hibernate sẽ tự động cập nhật cấu trúc bảng nếu bạn sửa code Entity.
*   **Cache UI:** `spring.thymeleaf.cache=false` -> Đang tắt cache để phục vụ việc Dev (sửa html lưu lại là thấy đổi ngay). Khi deploy production, nên đổi thành `true` để tăng hiệu năng.

---

## 5. Những Điểm Cần Tập Trung Học/Ôn Luyện

Để code cứng tay project này, bạn cần focus vào các kỹ năng sau:
1.  **Spring Data JPA:** Cách viết các custom query bằng JPQL hoặc Method Name Naming Convention (`findBy...`).
2.  **MapStruct:** Cách mapping các field có tên khác nhau, cách xử lý mapping List/Collection.
3.  **Thymeleaf:** Cú pháp `th:text`, `th:each`, `th:if`, cách làm việc với form (nhận lỗi validation `th:errors`), layout dialect.
4.  **Spring Security:** Cơ chế filter chain, cách cấu hình phân quyền dựa trên HTTP methods và Roles. Hiểu session hoạt động thế nào.

*Chúc bạn hoàn thành tốt dự án! Cứ bám sát kiến trúc MVC và các layer này thì dù app có phình to ra cũng không sợ rối.*
