package com.clansocket.panel;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import net.runelite.client.config.ConfigManager;

import com.clansocket.ClanSocketConstants;
import com.clansocket.panel.widgets.StreamGate;

public final class PanelStatsPersistence
{
	private PanelStatsPersistence() {
	}

	public static void load(final ConfigManager mgr, final Map<StreamGate, AtomicLong> counts,
	        final Map<StreamGate, RateBuffer> rates)
	{
		for (final StreamGate gate : StreamGate.ALL)
		{
			final Long c = mgr.getRSProfileConfiguration(ClanSocketConstants.CONFIG_GROUP, countConfigKey(gate),
			        long.class);
			counts.get(gate).set(c == null ? 0L : c);
			final Long t = mgr.getRSProfileConfiguration(ClanSocketConstants.CONFIG_GROUP, lastEventAtConfigKey(gate),
			        long.class);
			rates.get(gate).setLastEventAt(t == null ? 0L : t);
		}
	}

	public static void flush(final ConfigManager mgr, final Map<StreamGate, AtomicLong> counts,
	        final Map<StreamGate, RateBuffer> rates)
	{
		if (mgr.getRSProfileKey() == null)
		{
			return;
		}
		for (final StreamGate gate : StreamGate.ALL)
		{
			mgr.setRSProfileConfiguration(ClanSocketConstants.CONFIG_GROUP, countConfigKey(gate),
			        counts.get(gate).get());
			mgr.setRSProfileConfiguration(ClanSocketConstants.CONFIG_GROUP, lastEventAtConfigKey(gate),
			        rates.get(gate).lastEventAt());
		}
	}

	public static void reset(final ConfigManager mgr, final Map<StreamGate, AtomicLong> counts,
	        final Map<StreamGate, RateBuffer> rates)
	{
		for (final StreamGate gate : StreamGate.ALL)
		{
			counts.get(gate).set(0L);
			rates.get(gate).setLastEventAt(0L);
		}
		if (mgr.getRSProfileKey() == null)
		{
			return;
		}
		for (final StreamGate gate : StreamGate.ALL)
		{
			mgr.unsetRSProfileConfiguration(ClanSocketConstants.CONFIG_GROUP, countConfigKey(gate));
			mgr.unsetRSProfileConfiguration(ClanSocketConstants.CONFIG_GROUP, lastEventAtConfigKey(gate));
		}
	}

	private static String countConfigKey(final StreamGate gate)
	{
		return "count." + normalizeKey(gate.displayName());
	}

	private static String lastEventAtConfigKey(final StreamGate gate)
	{
		return "lastat." + normalizeKey(gate.displayName());
	}

	private static String normalizeKey(final String displayName)
	{
		return displayName.toLowerCase().replace(' ', '_').replace('-', '_');
	}
}
