package com.clansocket.config.preset;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.runelite.client.config.ConfigManager;

import com.clansocket.ClanSocketConstants;
import com.clansocket.util.Json;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public final class PresetCodec
{
	private PresetCodec() {
	}

	public static String encode(final PresetSchema schema)
	{
		return Json.GSON.toJson(schema);
	}

	public static PresetSchema decode(final String json)
	{
		final PresetSchema schema;
		try
		{
			schema = Json.GSON.fromJson(json, PresetSchema.class);
		} catch (final JsonSyntaxException ex)
		{
			throw new IllegalArgumentException(PresetConstants.LOG_BAD_JSON, ex);
		}
		validateDecoded(schema);
		return schema;
	}

	private static void validateDecoded(final PresetSchema schema)
	{
		if (schema == null || schema.getValues() == null)
		{
			throw new IllegalArgumentException(PresetConstants.LOG_BAD_JSON);
		}
		if (schema.getVersion() != PresetConstants.SCHEMA_VERSION)
		{
			throw new IllegalArgumentException(PresetConstants.LOG_BAD_VERSION);
		}
		for (final Map.Entry<String, JsonElement> e : schema.getValues().entrySet())
		{
			if (!e.getValue().isJsonPrimitive())
			{
				throw new IllegalArgumentException(PresetConstants.LOG_BAD_JSON);
			}
		}
	}

	public static String canonicalHash(final PresetSchema schema)
	{
		final Map<String, JsonElement> sorted = new TreeMap<>(schema.getValues());
		sorted.keySet().removeIf(PresetCodec::isDenylisted);
		return sha256Hex(Json.GSON.toJson(sorted));
	}

	public static boolean isDenylisted(final String key)
	{
		if (key.startsWith(PresetConstants.EXCLUDED_PREFIX_RSPROFILE)
		        || key.startsWith(PresetConstants.EXCLUDED_PREFIX_SNAP_HASH)
		        || key.startsWith(PresetConstants.EXCLUDED_PREFIX_WIKIAV))
		{
			return true;
		}
		return ClanSocketConstants.CONFIG_KEY_MODE.equals(key)
		        || ClanSocketConstants.CONFIG_KEY_SERVER_WS_URL.equals(key);
	}

	public static PresetSchema snapshot(final ConfigManager configManager)
	{
		final Map<String, JsonElement> values = new LinkedHashMap<>();
		final String prefix = ClanSocketConstants.CONFIG_GROUP + ".";
		final List<String> keys = configManager.getConfigurationKeys(prefix);
		for (final String fullKey : keys)
		{
			final String key = fullKey.startsWith(prefix) ? fullKey.substring(prefix.length()) : fullKey;
			if (isDenylisted(key))
			{
				continue;
			}
			final String val = configManager.getConfiguration(ClanSocketConstants.CONFIG_GROUP, key);
			if (val == null)
			{
				continue;
			}
			values.put(key, toJsonValue(val));
		}
		return new PresetSchema(PresetConstants.SCHEMA_VERSION, values);
	}

	private static JsonElement toJsonValue(final String s)
	{
		if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s))
		{
			return new JsonPrimitive(Boolean.parseBoolean(s));
		}
		try
		{
			return new JsonPrimitive(Long.parseLong(s));
		} catch (final NumberFormatException ignored)
		{
		}
		try
		{
			return new JsonPrimitive(Double.parseDouble(s));
		} catch (final NumberFormatException ignored)
		{
		}
		return new JsonPrimitive(s);
	}

	private static String sha256Hex(final String s)
	{
		try
		{
			final MessageDigest md = MessageDigest.getInstance("SHA-256");
			final byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
			final StringBuilder sb = new StringBuilder(bytes.length * 2);
			for (final byte b : bytes)
			{
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (final NoSuchAlgorithmException ex)
		{
			throw new IllegalStateException(ex);
		}
	}
}
