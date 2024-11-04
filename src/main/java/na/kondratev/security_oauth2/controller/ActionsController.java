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
public class ActionsController {
    private ProductRepository productRepository;

    @GetMapping("/public/test")
    public String publicTestPage() {
        return "<h1>Public Test Page</h1>";
    }

    @GetMapping("/public/products")
    public ResponseEntity<Object> getProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }


    @DeleteMapping("/admin/delete_product/{id}")
    public HttpStatus deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return HttpStatus.OK;
    }

    @GetMapping("/user_access/get_product/{id}")
    public Optional<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id);
    }

    @GetMapping("/user_access/test")
    public String test() {
        return "<h1>User Access Test page</h1>";
    }
}
