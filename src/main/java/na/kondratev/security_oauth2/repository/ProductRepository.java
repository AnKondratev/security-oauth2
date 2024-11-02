package na.kondratev.security_oauth2.repository;

import na.kondratev.security_oauth2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
