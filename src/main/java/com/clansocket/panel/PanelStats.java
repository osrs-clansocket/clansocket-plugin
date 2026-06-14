package com.clansocket.panel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.client.config.ConfigManager;

import com.clansocket.panel.widgets.StreamGate;

@Singleton
public class PanelStats
{
	public enum ConnectionState
	{
		DISCONNECTED, CONNECTING, CONNECTED, RECONNECTING
	}

	private final ConfigManager configManager;
	private final AtomicReference<ConnectionState> connectionState = new AtomicReference<>(
	        ConnectionState.DISCONNECTED);
	private volatile String endpoint = "";
	private volatile String clanReason;
	private final Map<StreamGate, AtomicLong> counts = initCounts();
	private final Map<StreamGate, RateBuffer> rates = initRates();

	@Inject
	public PanelStats(final ConfigManager configManager) {
		this.configManager = configManager;
	}

	public ConnectionState getConnectionState()
	{
		return connectionState.get();
	}

	public String getEndpoint()
	{
		return endpoint;
	}

	public String getClanReason()
	{
		return clanReason;
	}

	public void setClanReason(final String reason)
	{
		this.clanReason = reason;
	}

	public long count(final StreamGate gate)
	{
		final AtomicLong c = counts.get(gate);
		return c == null ? 0L : c.get();
	}

	public void bump(final StreamGate gate)
	{
		final AtomicLong c = counts.get(gate);
		if (c != null)
		{
			c.incrementAndGet();
		}
		final RateBuffer rb = rates.get(gate);
		if (rb != null)
		{
			rb.bump();
		}
	}

	public int[] rateSnapshot(final StreamGate gate)
	{
		final RateBuffer rb = rates.get(gate);
		return rb == null ? new int[RateBuffer.WINDOW_SECONDS] : rb.snapshot();
	}

	public long lastEventAt(final StreamGate gate)
	{
		final RateBuffer rb = rates.get(gate);
		return rb != null ? rb.lastEventAt() : 0L;
	}

	public void loadCounts()
	{
		PanelStatsPersistence.load(configManager, counts, rates);
	}

	public void flushCounts()
	{
		PanelStatsPersistence.flush(configManager, counts, rates);
	}

	public void resetCounts()
	{
		PanelStatsPersistence.reset(configManager, counts, rates);
	}

	public void markConnecting(final String target)
	{
		endpoint = target == null ? "" : target;
		connectionState.set(ConnectionState.CONNECTING);
	}

	public void markConnected()
	{
		connectionState.set(ConnectionState.CONNECTED);
	}

	public void markDisconnected()
	{
		connectionState.set(ConnectionState.DISCONNECTED);
	}

	public void markReconnectAttempt(final int attempt)
	{
		connectionState.set(attempt == 0 ? ConnectionState.RECONNECTING : ConnectionState.DISCONNECTED);
	}

	private static <T> Map<StreamGate, T> initPerGate(final java.util.function.Supplier<T> factory)
	{
		final Map<StreamGate, T> map = new HashMap<>(StreamGate.ALL.size());
		StreamGate.ALL.forEach(gate -> map.put(gate, factory.get()));
		return map;
	}

	private static Map<StreamGate, AtomicLong> initCounts()
	{
		return initPerGate(AtomicLong::new);
	}

	private static Map<StreamGate, RateBuffer> initRates()
	{
		return initPerGate(RateBuffer::new);
	}
}
