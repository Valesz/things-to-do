package org.example.controller;

import org.example.model.Submission;
import org.example.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {

    @Autowired
    SubmissionService submissionService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Submission addSubmission(@RequestBody Submission submission) {
        try {
            return submissionService.saveSubmission(submission);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Submission updateSubmission(@RequestBody Submission submission) {
        try {
            return submissionService.updateSubmission(submission);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Iterable<Submission> listSubmissions(@RequestBody(required = false) Submission submission) {
        return submissionService.getBySubmissionsObject(submission);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean deleteSubmission(@PathVariable(value = "id") Long id) {
        return submissionService.deleteSubmission(id);
    }

}
