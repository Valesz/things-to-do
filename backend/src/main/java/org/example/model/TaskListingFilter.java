package org.example.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskListingFilter
{
	private Long id;

	private String name;

	private String description;

	private LocalDate timeofcreation;

	private LocalDate createdAfter;

	private LocalDate createdBefore;

	private Long maintaskid;

	private Long ownerid;

	private Collection<String> keywords;

	private Long keywordsMatching;

	private Long completedUserId;

	private Boolean completed;
}
