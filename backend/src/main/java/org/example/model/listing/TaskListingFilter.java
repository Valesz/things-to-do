package org.example.model.listing;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskListingFilter
{
	@Column(value = "ID")
	private Long id;

	@Column(value = "NAME")
	private String name;

	@Column(value = "DESCRIPTION")
	private String description;

	@Column(value = "TIMEOFCREATION")
	private LocalDate timeofcreation;

	private LocalDate createdAfter;

	private LocalDate createdBefore;

	@Column(value = "MAINTASKID")
	private Long maintaskid;

	@Column(value = "OWNERNAME")
	private String ownername;

	private Long ownerid;

	private Collection<String> keywords;

	private Long keywordsMatching;

	private Long completedUserId;

	private Boolean completed;
}
