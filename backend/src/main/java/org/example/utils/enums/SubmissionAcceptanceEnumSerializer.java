package org.example.utils.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class SubmissionAcceptanceEnumSerializer extends StdSerializer<SubmissionAcceptanceEnum>
{
	public SubmissionAcceptanceEnumSerializer()
	{
		super(SubmissionAcceptanceEnum.class);
	}

	public SubmissionAcceptanceEnumSerializer(Class t)
	{
		super(t);
	}

	@Override
	public void serialize(SubmissionAcceptanceEnum submissionAcceptanceEnum, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException
	{
		jsonGenerator.writeString(submissionAcceptanceEnum.toString());
	}
}
