package com.clansocket.tracking.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.clansocket.protocol.inventory.ItemChange;

final class Deltas
{
	private Deltas() {
	}

	static Map<Integer, Integer> rawIdQtyMap(final int[] ids, final int[] qtys)
	{
		final Map<Integer, Integer> out = new HashMap<>();
		for (int i = 0; i < ids.length; i++)
		{
			out.merge(ids[i], qtys[i], Integer::sum);
		}
		return out;
	}

	static List<ItemChange> compute(final Map<Integer, Integer> prev, final Map<Integer, Integer> current,
	        final ItemNames itemNames)
	{
		final Set<Integer> allIds = new HashSet<>();
		allIds.addAll(prev.keySet());
		allIds.addAll(current.keySet());
		final List<ItemChange> changes = new ArrayList<>();
		for (final int id : allIds)
		{
			final int diff = current.getOrDefault(id, 0) - prev.getOrDefault(id, 0);
			if (diff != 0)
			{
				changes.add(new ItemChange(id, diff, itemNames.resolve(id), itemNames.resolvePrice(id)));
			}
		}
		return changes;
	}
}
