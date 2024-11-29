package org.example.utils.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonSerialize(using = SubmissionAcceptanceEnumSerializer.class)
@JsonDeserialize(using = SubmissionAcceptanceEnumDeserializer.class)
public enum SubmissionAcceptanceEnum
{
	ACCEPTED("ACCEPTED"),
	REJECTED("REJECTED"),
	IN_PROGRESS("IN_PROGRESS");

	private final String acceptance;

	SubmissionAcceptanceEnum(String acceptance)
	{
		this.acceptance = acceptance;
	}

	@JsonCreator
	public static SubmissionAcceptanceEnum forValues(@JsonProperty("acceptance") String acceptance)
	{
		for (SubmissionAcceptanceEnum e : SubmissionAcceptanceEnum.values())
		{
			if (e.acceptance.equals(acceptance))
			{
				return e;
			}
		}

		return null;
	}

	@JsonValue
	public String getAcceptance()
	{
		return this.acceptance;
	}

	public static SubmissionAcceptanceEnum findByValue(String status)
	{

		SubmissionAcceptanceEnum acceptanceEnum = null;

		for (SubmissionAcceptanceEnum e : SubmissionAcceptanceEnum.values())
		{
			if (e.acceptance.equals(status))
			{
				acceptanceEnum = e;
				break;
			}
		}
		return acceptanceEnum;
	}
}
