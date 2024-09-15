package springproject.urssublog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springproject.urssublog.domain.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {
}
