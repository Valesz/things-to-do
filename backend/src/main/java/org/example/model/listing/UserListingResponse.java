package org.example.model.listing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserListingResponse
{
	private Iterable<User> users;
	private long totalRows;
}
