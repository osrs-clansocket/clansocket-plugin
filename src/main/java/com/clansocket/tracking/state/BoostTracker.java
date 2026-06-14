package com.clansocket.tracking.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import com.clansocket.ClanSocketConfig;
import com.clansocket.bus.Hashes;
import com.clansocket.bus.primitive.LatchedSnapshotTracker;
import com.clansocket.protocol.common.Payload;
import com.clansocket.protocol.state.BoostEntry;

@Singleton
public class BoostTracker extends LatchedSnapshotTracker<Long>
{
	@Inject
	private ClanSocketConfig config;

	@Subscribe
	public void onGameTick(final GameTick tick)
	{
		if (!config.streamBoosts() || !isLoggedIn())
		{
			return;
		}
		final List<BoostEntry> entries = new ArrayList<>();
		long sig = 1L;
		for (final Skill s : Skill.values())
		{
			final int diff = client.getBoostedSkillLevel(s) - client.getRealSkillLevel(s);
			sig = sig * Hashes.HASH_PRIME + diff;
			if (diff != 0)
			{
				entries.add(new BoostEntry(s.name(), diff));
			}
		}
		if (!latch.update(sig))
		{
			return;
		}
		batcher.enqueue(new Payload("boosts", "hash", Hashes.of(sig), "boosts", Collections.unmodifiableList(entries)));
	}
}
