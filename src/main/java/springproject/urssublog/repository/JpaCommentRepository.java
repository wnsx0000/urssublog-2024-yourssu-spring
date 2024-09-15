package springproject.urssublog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springproject.urssublog.domain.Comment;
import springproject.urssublog.domain.User;

public interface JpaCommentRepository extends JpaRepository<Comment, Long> {
}
