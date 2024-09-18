package org.example.service;

import org.example.model.Submission;
import org.example.utils.exceptions.ConstraintException;
import org.example.utils.exceptions.NullValueException;
import org.springframework.stereotype.Service;

@Service
public interface SubmissionService {

    boolean setAcceptance(Long id, boolean accepted);

    Iterable<Submission> getAllSubmissions();

    Submission getSubmissionById(Long id);

    Iterable<Submission> getBySubmissionsObject(Submission submission);

    Submission saveSubmission(Submission submission) throws NullValueException, ConstraintException;

    Submission updateSubmission(Submission submission) throws NullValueException, ConstraintException;

    boolean deleteSubmission(Long id);

    boolean deleteAll();

}
