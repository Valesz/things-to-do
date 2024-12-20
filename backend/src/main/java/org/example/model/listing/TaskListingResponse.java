package org.example.model.listing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskListingResponse
{
	Iterable<TaskListingFilter> tasks;
	long totalTasks;
}
