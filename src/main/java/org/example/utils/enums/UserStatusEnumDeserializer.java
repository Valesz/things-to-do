package org.example.utils.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class UserStatusEnumDeserializer extends StdDeserializer<UserStatusEnum>
{
	public UserStatusEnumDeserializer()
	{
		super(UserStatusEnum.class);
	}

	public UserStatusEnumDeserializer(Class<?> vc)
	{
		super(vc);
	}

	@Override
	public UserStatusEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException
	{
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		if (node.textValue() == null)
		{
			return null;
		}

		String status = node.textValue();

		for (UserStatusEnum userStatusEnum : UserStatusEnum.values())
		{

			if (userStatusEnum.getStatus().equals(status))
			{
				return userStatusEnum;
			}
		}

		return null;
	}
}
