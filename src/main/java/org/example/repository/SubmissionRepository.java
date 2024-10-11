package org.example.repository;

import org.example.model.Submission;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("submissionRepository")
public interface SubmissionRepository extends CrudRepository<Submission, Long>
{

	@Modifying
	@Query("UPDATE \"submission\" SET ACCEPTANCE = :ACCEPTANCE WHERE ID = :ID")
	Integer setAcceptance(@Param("ID") Long id, @Param("ACCEPTANCE") Boolean acceptance);
}
