package com.dottags;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;

@PluginDescriptor(
	name = "Dot Tags",
	description = "Shift + right-click an item to mark it with a small colored dot",
	tags = {"inventory", "tag", "dot", "highlight", "marker"}
)
public class DotTagsPlugin extends Plugin
{
	private static final String TAG_KEY_PREFIX = "tag_";
	private static final int GROUP_COUNT = 6;

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private DotTagsOverlay overlay;

	@Inject
	private DotTagsConfig config;

	@Provides
	DotTagsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DotTagsConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	/** Returns the dot color for an item, or null if it isn't tagged. */
	Color getTagColor(int itemId)
	{
		Integer group = getTagGroup(itemId);
		return group == null ? null : groupColor(group);
	}

	private Integer getTagGroup(int itemId)
	{
		String v = configManager.getConfiguration(DotTagsConfig.GROUP, TAG_KEY_PREFIX + itemManager.canonicalize(itemId));
		if (v == null)
		{
			return null;
		}
		try
		{
			int g = Integer.parseInt(v);
			return g >= 1 && g <= GROUP_COUNT ? g : null;
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	private void setTagGroup(int itemId, Integer group)
	{
		String key = TAG_KEY_PREFIX + itemManager.canonicalize(itemId);
		if (group == null)
		{
			configManager.unsetConfiguration(DotTagsConfig.GROUP, key);
		}
		else
		{
			configManager.setConfiguration(DotTagsConfig.GROUP, key, group);
		}
	}

	private Color groupColor(int group)
	{
		switch (group)
		{
			case 1: return config.group1();
			case 2: return config.group2();
			case 3: return config.group3();
			case 4: return config.group4();
			case 5: return config.group5();
			case 6: return config.group6();
			default: return null;
		}
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		if (!client.isKeyPressed(KeyCode.KC_SHIFT))
		{
			return;
		}

		MenuEntry[] entries = event.getMenuEntries();
		for (int idx = entries.length - 1; idx >= 0; --idx)
		{
			MenuEntry entry = entries[idx];
			Widget w = entry.getWidget();
			if (w == null || !"Examine".equals(entry.getOption()))
			{
				continue;
			}

			int iface = WidgetUtil.componentToInterface(w.getId());
			if (iface != InterfaceID.INVENTORY && iface != InterfaceID.BANKSIDE && iface != InterfaceID.BANKMAIN)
			{
				continue;
			}

			int itemId = w.getItemId();
			if (itemId <= 0)
			{
				continue;
			}

			Integer current = getTagGroup(itemId);

			MenuEntry parent = client.getMenu().createMenuEntry(idx)
				.setOption("Dot tag")
				.setTarget(entry.getTarget())
				.setType(MenuAction.RUNELITE);
			Menu sub = parent.createSubMenu();

			for (int g = GROUP_COUNT; g >= 1; g--)
			{
				final int group = g;
				Color c = groupColor(g);
				sub.createMenuEntry(0)
					.setOption(ColorUtil.prependColorTag((current != null && current == g ? "● " : "○ ") + "Group " + g, c))
					.setType(MenuAction.RUNELITE)
					.onClick(e -> setTagGroup(itemId, group));
			}

			if (current != null)
			{
				sub.createMenuEntry(0)
					.setOption("Remove dot")
					.setType(MenuAction.RUNELITE)
					.onClick(e -> setTagGroup(itemId, null));
			}
			break;
		}
	}
}
