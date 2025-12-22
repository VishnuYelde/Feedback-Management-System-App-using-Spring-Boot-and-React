package fms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fms.entity.Feedback;
import fms.entity.User;
import fms.repository.FeedbackRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

	@Autowired
	private FeedbackRepository feedbackRepository;

	// Add Feedback
	@PostMapping
	public Feedback addFeedback(@RequestBody Feedback feedback) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		feedback.setUser(user);
		return feedbackRepository.save(feedback);
	}

	// View own Feedbacks
	@GetMapping
	public List<Feedback> getMyFeedbacks() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return feedbackRepository.findByUser(user);
	}

	// DELETE OWN FEEDBACK
	@DeleteMapping("/{id}")
	public String deleteFeedback(@PathVariable Long id) {

		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Feedback feedback = feedbackRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Feedback not found"));

		// Ownership check
		if (!feedback.getUser().getId().equals(loggedInUser.getId())) {
			throw new RuntimeException("You are not allowed to delete this feedback");
		}

		feedbackRepository.delete(feedback);
		return "Feedback deleted successfully";
	}

	// UPDATE OWN FEEDBACK
	@PutMapping("/{id}")
	public Feedback updateFeedback(@PathVariable Long id, @RequestBody Feedback updatedFeedback) {

		User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Feedback existingFeedback = feedbackRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Feedback not found"));

		// Ownership check
		if (!existingFeedback.getUser().getId().equals(loggedInUser.getId())) {
			throw new RuntimeException("You are not allowed to edit this feedback");
		}

		existingFeedback.setTitle(updatedFeedback.getTitle());
		existingFeedback.setMessage(updatedFeedback.getMessage());
		existingFeedback.setRating(updatedFeedback.getRating());

		return feedbackRepository.save(existingFeedback);
	}

}
