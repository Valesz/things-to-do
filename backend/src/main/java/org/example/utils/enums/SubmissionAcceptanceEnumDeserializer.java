package org.example.utils.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class SubmissionAcceptanceEnumDeserializer extends StdDeserializer<SubmissionAcceptanceEnum>
{
	public SubmissionAcceptanceEnumDeserializer()
	{
		super(SubmissionAcceptanceEnum.class);
	}

	public SubmissionAcceptanceEnumDeserializer(Class<?> t)
	{
		super(t);
	}

	@Override
	public SubmissionAcceptanceEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException
	{
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		if (node.textValue() == null)
		{
			return null;
		}

		String value = node.textValue();

		for (SubmissionAcceptanceEnum e : SubmissionAcceptanceEnum.values())
		{
			if (e.toString().equals(value))
			{
				return e;
			}
		}

		return null;
	}
}
