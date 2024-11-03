package na.kondratev.security_oauth2.repository;

import na.kondratev.security_oauth2.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByLogin(String login);
}
