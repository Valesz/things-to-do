package org.example.service.impl;

import org.example.model.Submission;
import org.example.repository.SubmissionRepository;
import org.example.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
    public Iterable<Submission> getBySubmissionsObject(Submission submission) {
        if (submission == null) {
            return getAllSubmissions();
        }

        SqlParameterSource namedParams = new MapSqlParameterSource()
                .addValue("id", submission.getId())
                .addValue("taskid", submission.getTaskid())
                .addValue("description", submission.getDescription())
                .addValue("timeofsubmission", submission.getTimeofsubmission() == null ? null : submission.getTimeofsubmission().toString())
                .addValue("acceptance", submission.getAcceptance())
                .addValue("submitterid", submission.getSubmitterid());

        String query = constructQueryByOwnObject(submission);

        return namedParameterJdbcTemplate.query(query, namedParams, rs -> {
            List<Submission> submissionList = new ArrayList<>();

            while (rs.next()) {
                submissionList.add(Submission.builder()
                        .id(rs.getLong("id"))
                        .taskid(rs.getLong("taskid"))
                        .description(rs.getString("description"))
                        .timeofsubmission(LocalDate.parse(rs.getString("timeofsubmission")))
                        .acceptance(rs.getBoolean("acceptance"))
                        .submitterid(rs.getLong("submitterid"))
                        .build()
                );
            }

            return submissionList;
        });
    }

    private String constructQueryByOwnObject(Submission submission) {
        StringBuilder query = new StringBuilder(" SELECT * FROM \"submission\" ");

        query.append(" WHERE 1 = 1 ");

        if (submission.getId() != null) {
            query.append(" AND id = :id ");
        }

        if (submission.getTaskid() != null) {
            query.append(" AND taskid = :taskid ");
        }

        if (submission.getDescription() != null) {
            query.append(" AND description = :description ");
        }

        if (submission.getTimeofsubmission() != null) {
            query.append(" AND timeofsubmission = :timeofsubmission ");
        }

        if (submission.getAcceptance() != null) {
            query.append(" AND acceptance = :acceptance ");
        }

        if (submission.getSubmitterid() != null) {
            query.append(" AND submitterid = :submitterid ");
        }

        return query.toString();
    }

    @Override
    public Submission saveSubmission(Submission submission) {
        if (submission.getId() != null) {
            throw new IllegalArgumentException("Remove id property, or use Update instead of Save.");
        }

        return submissionRepository.save(submission);
    }

    @Override
    public Submission updateSubmission(Submission submission) {
        if (!submissionRepository.existsById(submission.getId())) {
            throw new IllegalArgumentException("Submission with id " + submission.getId() + " doesn't exist. Please use save to save this instance.");
        }

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
