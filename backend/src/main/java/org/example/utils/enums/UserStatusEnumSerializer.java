package org.example.utils.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class UserStatusEnumSerializer extends StdSerializer<UserStatusEnum>
{

	public UserStatusEnumSerializer()
	{
		super(UserStatusEnum.class);
	}

	public UserStatusEnumSerializer(Class t)
	{
		super(t);
	}

	@Override
	public void serialize(UserStatusEnum userStatusEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException
	{
		jsonGenerator.writeString(userStatusEnum.getStatus());
	}
}
