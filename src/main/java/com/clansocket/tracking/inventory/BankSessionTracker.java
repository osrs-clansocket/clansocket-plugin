package com.clansocket.tracking.inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;

import com.clansocket.ClanSocketConfig;
import com.clansocket.bus.AbstractStateTracker;
import com.clansocket.protocol.common.Payload;
import com.clansocket.protocol.inventory.Item;
import com.clansocket.protocol.inventory.ItemChange;
import com.clansocket.tracking.movement.LocationContext;
import com.clansocket.util.Money;

@Singleton
public class BankSessionTracker extends AbstractStateTracker
{
	@Inject
	private ClanSocketConfig config;
	@Inject
	private ItemNames itemNames;
	@Inject
	private LocationContext locationContext;

	private boolean bankOpen;
	private boolean openEventEmitted;
	private long openedAtMs;
	private Map<Integer, Integer> openQtyMap;

	@Override
	protected void onLoginScreen()
	{
		resetSession();
	}

	@Subscribe
	public void onWidgetLoaded(final WidgetLoaded event)
	{
		if (event.getGroupId() != InterfaceID.BANKMAIN || !config.streamBank())
		{
			return;
		}
		bankOpen = true;
		openEventEmitted = false;
		openedAtMs = System.currentTimeMillis();
	}

	@Subscribe
	public void onWidgetClosed(final WidgetClosed event)
	{
		if (event.getGroupId() != InterfaceID.BANKMAIN || !bankOpen)
		{
			return;
		}
		if (config.streamBank() && openEventEmitted)
		{
			emitCloseFromCurrentState();
		}
		resetSession();
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.BANK || !bankOpen || !config.streamBank() || openEventEmitted)
		{
			return;
		}
		final ItemContainer ic = event.getItemContainer();
		if (ic == null)
		{
			return;
		}
		final List<Item> snap = ItemSnapshots.fromContainer(ic, itemNames, bankTabBoundaries());
		openQtyMap = toQtyMap(snap);
		batcher.enqueue(new Payload("bank_open", "hash", ItemSnapshots.hashByIdQty(snap), "items", snap));
		openEventEmitted = true;
	}

	private void emitCloseFromCurrentState()
	{
		final ItemContainer ic = client.getItemContainer(InventoryID.BANK);
		if (ic == null)
		{
			return;
		}
		final List<Item> snap = ItemSnapshots.fromContainer(ic, itemNames, bankTabBoundaries());
		final List<ItemChange> changes = openQtyMap == null
		        ? Collections.emptyList()
		        : Deltas.compute(openQtyMap, toQtyMap(snap), itemNames);
		final long netValue = Money.sumGp(changes, c -> c.qty, c -> c.price);
		final long bankValue = Money.sumGp(snap, it -> it.qty, it -> it.price);
		batcher.enqueue(new Payload("bank_close", "hash", ItemSnapshots.hashByIdQty(snap), "items", snap, "changes",
		        changes, "durationMs", System.currentTimeMillis() - openedAtMs, "netValue", netValue, "bankValue",
		        bankValue, "where", locationContext.capture()));
	}

	private int[] bankTabBoundaries()
	{
		final int[] boundaries = new int[InventoryConstants.BANK_TAB_COUNT];
		int cum = 0;
		for (int t = 0; t < boundaries.length; t++)
		{
			cum += client.getVarbitValue(VarbitID.BANK_TAB_1 + t);
			boundaries[t] = cum;
		}
		return boundaries;
	}

	private static Map<Integer, Integer> toQtyMap(final List<Item> items)
	{
		final Map<Integer, Integer> map = new HashMap<>(items.size());
		for (final Item it : items)
		{
			map.merge(it.id, it.qty, Integer::sum);
		}
		return map;
	}

	private void resetSession()
	{
		bankOpen = false;
		openEventEmitted = false;
		openedAtMs = 0;
		openQtyMap = null;
	}
}
