package org.example.service;

import org.example.model.Submission;
import org.springframework.stereotype.Service;

@Service
public interface SubmissionService {

    boolean setAcceptance(Long id, boolean accepted);

    Iterable<Submission> getAllSubmissions();

    Submission getSubmissionById(Long id);

    Iterable<Submission> getBySubmissionsObject(Submission submission);

    Submission saveSubmission(Submission submission);

    Submission updateSubmission(Submission submission);

    boolean deleteSubmission(Long id);

    boolean deleteAll();

}
