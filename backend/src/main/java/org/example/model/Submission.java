package org.example.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.listing.SubmissionListing;
import org.example.utils.enums.SubmissionAcceptanceEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@Table(name = "submission")
@AllArgsConstructor
@NoArgsConstructor
public class Submission
{

	@Id
	@Column(value = "ID")
	private Long id;

	@Column(value = "TASKID")
	private Long taskid;

	@Column(value = "DESCRIPTION")
	private String description;

	@Column(value = "TIMEOFSUBMISSION")
	private LocalDate timeofsubmission;

	@Column(value = "ACCEPTANCE")
	private SubmissionAcceptanceEnum acceptance;

	@Column(value = "SUBMITTERID")
	private Long submitterid;

	public boolean listingObjEquals(SubmissionListing other)
	{
		return Objects.equals(this.id, other.getId())
			&& Objects.equals(this.taskid, other.getTaskid())
			&& Objects.equals(this.description, other.getDescription())
			&& Objects.equals(this.timeofsubmission, other.getTimeofsubmission())
			&& Objects.equals(this.acceptance, other.getAcceptance())
			&& Objects.equals(this.submitterid, other.getSubmitterid());
	}
}