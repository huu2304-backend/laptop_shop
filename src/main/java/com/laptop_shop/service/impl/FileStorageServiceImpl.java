package com.laptop_shop.service.impl;

import com.laptop_shop.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            // Tự động tạo thư mục lưu trữ file tại C:/image nếu chưa tồn tại
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file tại: " + uploadDir, ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File không được để trống");
            }

            // Làm sạch tên file gốc, loại bỏ các ký tự nguy hiểm (ví dụ: ../../)
            String originalFilename = StringUtils.cleanPath(
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown"
            );

            // Chỉ lấy phần mở rộng (.jpg, .png, ...) để tránh Path Traversal Attack
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex);
            }

            // Tạo tên file duy nhất bằng UUID + timestamp (không dùng tên gốc từ user)
            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + extension;

            // Đường dẫn đích đầy đủ của file tại C:/image/
            Path targetLocation = this.fileStorageLocation.resolve(fileName).normalize();

            // Kiểm tra đảm bảo file không bị lưu ngoài thư mục cho phép (double-check)
            if (!targetLocation.startsWith(this.fileStorageLocation)) {
                throw new RuntimeException("Không thể lưu file ngoài thư mục cho phép: " + fileName);
            }

            // Sao chép luồng dữ liệu file trực tiếp vào thư mục đích
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL tương đối để phục vụ tĩnh
            return "/images/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file ảnh", e);
        }
    }
}
