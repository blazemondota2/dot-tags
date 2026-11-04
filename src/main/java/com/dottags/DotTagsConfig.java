package com.dottags;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(DotTagsConfig.GROUP)
public interface DotTagsConfig extends Config
{
	String GROUP = "dottags";

	@ConfigSection(name = "Dot style", description = "How the dot looks", position = 0)
	String styleSection = "style";

	@ConfigSection(name = "Group colors", description = "Color of each tag group", position = 1)
	String colorSection = "colors";

	@ConfigItem(keyName = "position", name = "Corner", description = "Which corner of the item the dot sits in",
		position = 0, section = styleSection)
	default DotPosition position()
	{
		return DotPosition.TOP_LEFT;
	}

	@Range(min = 3, max = 16)
	@ConfigItem(keyName = "dotSize", name = "Dot size", description = "Diameter of the dot in pixels",
		position = 1, section = styleSection)
	default int dotSize()
	{
		return 5;
	}

	@ConfigItem(keyName = "dotStyle", name = "Dot style", description = "Glossy = soft gradient with a highlight, Flat = plain circle",
		position = 1, section = styleSection)
	default DotStyle dotStyle()
	{
		return DotStyle.GLOSSY;
	}

	@Range(min = 10, max = 100)
	@ConfigItem(keyName = "opacity", name = "Opacity %", description = "How see-through the dot is (100 = solid)",
		position = 2, section = styleSection)
	default int opacity()
	{
		return 70;
	}

	@ConfigItem(keyName = "dotBorder", name = "Dark border", description = "Draw a thin dark ring so the dot stays visible on any item",
		position = 3, section = styleSection)
	default boolean dotBorder()
	{
		return false;
	}

	@ConfigItem(keyName = "showInBank", name = "Show in bank", description = "Also draw dots on items in the bank",
		position = 4, section = styleSection)
	default boolean showInBank()
	{
		return false;
	}

	@ConfigItem(keyName = "group1", name = "Group 1", description = "Color of group 1", position = 10, section = colorSection)
	default Color group1() { return new Color(0xFF3B30); }

	@ConfigItem(keyName = "group2", name = "Group 2", description = "Color of group 2", position = 11, section = colorSection)
	default Color group2() { return new Color(0x34C759); }

	@ConfigItem(keyName = "group3", name = "Group 3", description = "Color of group 3", position = 12, section = colorSection)
	default Color group3() { return new Color(0x0A84FF); }

	@ConfigItem(keyName = "group4", name = "Group 4", description = "Color of group 4", position = 13, section = colorSection)
	default Color group4() { return new Color(0xFFCC00); }

	@ConfigItem(keyName = "group5", name = "Group 5", description = "Color of group 5", position = 14, section = colorSection)
	default Color group5() { return new Color(0xBF5AF2); }

	@ConfigItem(keyName = "group6", name = "Group 6", description = "Color of group 6", position = 15, section = colorSection)
	default Color group6() { return new Color(0xFFFFFF); }
}
