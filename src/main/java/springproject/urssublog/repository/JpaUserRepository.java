package springproject.urssublog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springproject.urssublog.domain.User;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}