package org.example.service;

import org.example.model.Submission;
import org.springframework.stereotype.Service;

@Service
public interface SubmissionService {

    boolean setAcceptance(Long id, boolean accepted);

    Iterable<Submission> getAllSubmissions();

    Submission getSubmissionById(Long id);

    Submission saveSubmission(Submission submission);

    boolean deleteSubmission(Long id);

    boolean deleteAll();

}
