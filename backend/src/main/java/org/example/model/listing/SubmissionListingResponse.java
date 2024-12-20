package org.example.model.listing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionListingResponse
{
	private Iterable<SubmissionListing> submissions;
	private long totalRows;
}
