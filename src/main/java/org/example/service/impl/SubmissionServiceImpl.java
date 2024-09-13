package org.example.service.impl;

import org.example.model.Submission;
import org.example.repository.SubmissionRepository;
import org.example.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public boolean setAcceptance(Long id, boolean accepted) {
        return submissionRepository.setAcceptance(id, accepted);
    }

    @Override
    public Iterable<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @Override
    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id).orElse(null);
    }

    @Override
    public Submission saveSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public boolean deleteSubmission(Long id) {
        try {
            submissionRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Deletion of " + id + " submission failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteAll() {
        try {
            submissionRepository.deleteAll();
        } catch (Exception e) {
            System.out.println("Deletion of all submissions failed: " + e.getMessage());
            return false;
        }

        return true;
    }
}
