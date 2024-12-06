package org.example.service;

import org.example.model.Submission;
import org.example.model.listing.SubmissionListing;
import org.example.utils.enums.SubmissionAcceptanceEnum;
import org.example.utils.exceptions.ServiceException;
import org.springframework.stereotype.Service;

@Service
public interface SubmissionService
{

	Integer setAcceptance(Long id, SubmissionAcceptanceEnum accepted);

	Iterable<SubmissionListing> getAllSubmissions();

	Submission getSubmissionById(Long id);

	Iterable<SubmissionListing> getBySubmissionsObject(SubmissionListing submission);

	Submission saveSubmission(Submission submission) throws ServiceException;

	Submission updateSubmission(Submission submission) throws ServiceException;

	void deleteSubmission(Long id);

	void deleteAll();
}
