package na.kondratev.security_oauth2.controller;

import lombok.AllArgsConstructor;
import na.kondratev.security_oauth2.model.Product;
import na.kondratev.security_oauth2.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController()
@AllArgsConstructor
public class ProductController {
    private ProductRepository productRepository;

    @GetMapping("/public/products")
    public ResponseEntity<Object> getProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/public/homepage")
    public String getHomepage() {
        return "Hello! Welcome to home page!";
    }


    @DeleteMapping("/admin/delete_product/{id}")
    public HttpStatus deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return HttpStatus.OK;
    }

    @GetMapping("/user/get_product/{id}")
    public Optional<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id);
    }

    @GetMapping("/user/test")
    public String test() {
        return "test page!";
    }
}