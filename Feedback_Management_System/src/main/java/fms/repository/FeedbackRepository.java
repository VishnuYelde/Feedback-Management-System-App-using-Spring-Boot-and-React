package fms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fms.entity.Feedback;
import fms.entity.User;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

	List<Feedback> findByUser(User user);
}
