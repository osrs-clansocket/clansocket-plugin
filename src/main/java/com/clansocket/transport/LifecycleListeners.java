package com.clansocket.transport;

import javax.inject.Singleton;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public final class LifecycleListeners
{
	@Setter
	private Runnable onOpenListener;
	@Setter
	private Runnable onReidentifyListener;

	public void fireOnOpen()
	{
		runListener(onOpenListener, "onOpen");
	}

	public void fireOnReidentify()
	{
		runListener(onReidentifyListener, "onReidentify");
	}

	private void runListener(final Runnable listener, final String label)
	{
		if (listener == null)
		{
			return;
		}
		try
		{
			listener.run();
		} catch (final RuntimeException e)
		{
			log.warn("ClanSocket {} listener failed: {}", label, e.getMessage());
		}
	}
}
