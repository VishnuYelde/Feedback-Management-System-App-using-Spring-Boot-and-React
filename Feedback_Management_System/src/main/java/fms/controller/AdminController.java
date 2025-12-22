package fms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fms.entity.Feedback;
import fms.entity.User;
import fms.repository.FeedbackRepository;
import fms.security.SecurityUtil;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/admin")
public class AdminController {
	
	@Autowired
	private FeedbackRepository feedbackRepository;
	
	// VIEW ALL FEEDBACKS
	@GetMapping("/feedbacks")
	public List<Feedback> getAllFeedbacks() {
		
		User user = SecurityUtil.getCurrentUser();
		
		if (!SecurityUtil.isAdmin(user)) {
			throw new RuntimeException("Access denied");
		}
		
		return feedbackRepository.findAll();
	}
	
	// DELETE ANY FEEDBACK
	@DeleteMapping("/feedback/{id}")
	public String deleteAnyFeedback(@PathVariable Long id) {
		
		User user = SecurityUtil.getCurrentUser();
		
		if (!SecurityUtil.isAdmin(user)) {
			throw new RuntimeException("Access denied");
		}
		
		feedbackRepository.deleteById(id);
		return "Feedback deleted by Admin";
	}
	
}
