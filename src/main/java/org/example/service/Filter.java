package org.example.service;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class Filter
{

	Collection<String> keywords;

	String name;

	Long ownerId;

	Long completedUserId;
}
