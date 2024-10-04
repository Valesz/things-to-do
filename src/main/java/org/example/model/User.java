package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.utils.UserStatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User
{

	@Id
	@Column(value = "ID")
	private Long id;

	@Column(value = "USERNAME")
	private String username;

	@Column(value = "EMAIL")
	private String email;

	@Column(value = "TIMEOFCREATION")
	private LocalDate timeofcreation;

	@Column(value = "STATUS")
	private UserStatusEnum status;

	@Column(value = "PASSWORD")
	private String password;

	@Column(value = "CLASSIFICATION")
	private Double classification;

	@Column(value = "PRECISIONOFANSWERS")
	private Double precisionofanswers;
}