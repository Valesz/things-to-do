package org.example.repository;

import org.example.model.Submission;
import org.example.model.listing.SubmissionListing;
import org.example.utils.enums.SubmissionAcceptanceEnum;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("submissionRepository")
public interface SubmissionRepository extends CrudRepository<Submission, Long>
{

	@Query("SELECT SUBMISSION.ID, TASKID, DESCRIPTION, SUBMISSION.TIMEOFSUBMISSION, ACCEPTANCE, SUBMITTERID, USERNAME AS SUBMITTERNAME "
		+ "FROM \"submission\" SUBMISSION INNER JOIN \"user\" USERT ON SUBMISSION.submitterid = USERT.ID")
	Iterable<SubmissionListing> getAllSubmissions();

	@Modifying
	@Query("UPDATE \"submission\" SET ACCEPTANCE = :ACCEPTANCE WHERE ID = :ID")
	Integer setAcceptance(@Param("ID") Long id, @Param("ACCEPTANCE") SubmissionAcceptanceEnum acceptance);
}
