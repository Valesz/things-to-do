package org.example.model.listing;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.utils.enums.SubmissionAcceptanceEnum;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionListing
{

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

	@Column(value = "SUBMITTERNAME")
	private String submittername;
}
