package org.example.controller;

import org.example.model.Submission;
import org.example.service.SubmissionService;
import org.example.utils.exceptions.ServiceException;
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
        } catch (ServiceException e) {
            switch (e.getServiceExceptionTypeEnum()) {
                case CONSTRAINT_VIOLATION:
                case NULL_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

                case ILLEGAL_ID_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());

                default:
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Submission updateSubmission(@RequestBody Submission submission) {
        try {
            return submissionService.updateSubmission(submission);
        } catch (ServiceException e) {
            switch (e.getServiceExceptionTypeEnum()) {
                case CONSTRAINT_VIOLATION:
                case NULL_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

                case ILLEGAL_ID_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

                default:
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Iterable<Submission> listSubmissions(@RequestBody(required = false) Submission submission) {
        return submissionService.getBySubmissionsObject(submission);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteSubmission(@PathVariable(value = "id") Long id) {
        submissionService.deleteSubmission(id);
    }

}
