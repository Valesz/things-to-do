package org.example.utils.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonSerialize(using = UserStatusEnumSerializer.class)
@JsonDeserialize(using = UserStatusEnumDeserializer.class)
public enum UserStatusEnum
{
	AKTIV("AKTIV"),
	INAKTIV("INAKTIV");

	private final String status;

	UserStatusEnum(String status)
	{
		this.status = status;
	}

	@JsonCreator
	public static UserStatusEnum forValues(@JsonProperty("status") String status)
	{
		for (UserStatusEnum e : UserStatusEnum.values())
		{
			if (e.status.equals(status))
			{
				return e;
			}
		}

		return null;
	}

	@JsonValue
	public String getStatus()
	{
		return this.status;
	}

	public static UserStatusEnum findByValue(String status)
	{

		UserStatusEnum statusEnum = null;

		for (UserStatusEnum e : UserStatusEnum.values())
		{
			if (e.status.equals(status))
			{
				statusEnum = e;
				break;
			}
		}
		return statusEnum;
	}
}
