import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;

public class FixEncoding {
    public static void main(String[] args) throws Exception {
        String[] files = {
            "src/main/resources/templates/index.html",
            "src/main/resources/templates/shop.html",
            "src/main/resources/templates/product-detail.html",
            "src/main/resources/templates/cart/cart.html",
            "src/main/resources/templates/order/checkout.html",
            "src/main/resources/templates/products/add.html",
            "src/main/resources/templates/products/edit.html",
            "src/main/resources/templates/products/list.html",
            "src/main/resources/templates/list-categories.html"
        };
        for (String file : files) {
            Path path = Paths.get(file);
            if (!Files.exists(path)) continue;
            
            // Read the corrupted UTF-8 string
            String corrupted = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            
            // Revert the PowerShell Set-Content corruption
            try {
                // Get bytes back as Windows-1252
                byte[] originalUtf8Bytes = corrupted.getBytes("Windows-1252");
                // Decode proper UTF-8
                String fixed = new String(originalUtf8Bytes, StandardCharsets.UTF_8);
                Files.write(path, fixed.getBytes(StandardCharsets.UTF_8));
                System.out.println("Fixed " + file);
            } catch (Exception e) {
                System.err.println("Could not fix " + file + ": " + e.getMessage());
            }
        }
    }
}
