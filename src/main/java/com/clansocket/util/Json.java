package com.clansocket.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public final class Json
{
	public static final Gson GSON = new Gson();

	public static String optString(final JsonObject obj, final String key)
	{
		return obj.has(key) ? obj.get(key).getAsString() : "";
	}

	private Json() {
	}
}
