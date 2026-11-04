package com.dottags;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import javax.inject.Inject;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

class DotTagsOverlay extends WidgetItemOverlay
{
	private static final int PADDING = 2;
	private static final Color BORDER = new Color(0, 0, 0, 200);

	private final DotTagsPlugin plugin;
	private final DotTagsConfig config;

	@Inject
	DotTagsOverlay(DotTagsPlugin plugin, DotTagsConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		showOnInventory();
		showOnBank();
	}

	@Override
	public void renderItemOverlay(Graphics2D g, int itemId, WidgetItem widgetItem)
	{
		if (!config.showInBank() && WidgetUtil.componentToInterface(widgetItem.getWidget().getId()) == InterfaceID.BANKMAIN)
		{
			return;
		}

		Color color = plugin.getTagColor(itemId);
		if (color == null)
		{
			return;
		}

		Rectangle b = widgetItem.getCanvasBounds();
		int size = config.dotSize();
		int x;
		int y;
		switch (config.position())
		{
			case TOP_RIGHT:
				x = b.x + b.width - size - PADDING;
				y = b.y + PADDING;
				break;
			case BOTTOM_LEFT:
				x = b.x + PADDING;
				y = b.y + b.height - size - PADDING;
				break;
			case BOTTOM_RIGHT:
				x = b.x + b.width - size - PADDING;
				y = b.y + b.height - size - PADDING;
				break;
			case TOP_LEFT:
			default:
				x = b.x + PADDING;
				y = b.y + PADDING;
				break;
		}

		float a = config.opacity() / 100f;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		if (config.dotBorder())
		{
			g.setColor(withAlpha(Color.BLACK, Math.min(a, BORDER.getAlpha() / 255f)));
			g.fill(new Ellipse2D.Float(x - 1, y - 1, size + 2, size + 2));
		}

		if (config.dotStyle() == DotStyle.FLAT)
		{
			g.setColor(withAlpha(color, a));
			g.fill(new Ellipse2D.Float(x, y, size, size));
			return;
		}

		drawGlossyDot(g, x, y, size, color, a);
	}

	private static void drawGlossyDot(Graphics2D g, float x, float y, float s, Color base, float a)
	{
		Paint oldPaint = g.getPaint();
		Stroke oldStroke = g.getStroke();

		// soft drop shadow: two faint, slightly offset layers
		g.setColor(withAlpha(Color.BLACK, 0.10f * a));
		g.fill(new Ellipse2D.Float(x - 0.6f, y + 0.2f, s + 1.2f, s + 1.4f));
		g.setColor(withAlpha(Color.BLACK, 0.18f * a));
		g.fill(new Ellipse2D.Float(x - 0.1f, y + 0.6f, s + 0.2f, s + 0.4f));

		// body: lighter at the top, slightly deeper at the bottom
		Ellipse2D.Float body = new Ellipse2D.Float(x, y, s, s);
		g.setPaint(new GradientPaint(x, y, withAlpha(mix(base, Color.WHITE, 0.30f), a),
			x, y + s, withAlpha(mix(base, Color.BLACK, 0.12f), a)));
		g.fill(body);

		// glossy highlight on the upper half
		float hw = s * 0.62f;
		float hh = s * 0.42f;
		float hx = x + (s - hw) / 2f;
		float hy = y + s * 0.08f;
		g.setPaint(new GradientPaint(hx, hy, withAlpha(Color.WHITE, 0.55f * a),
			hx, hy + hh, withAlpha(Color.WHITE, 0f)));
		g.fill(new Ellipse2D.Float(hx, hy, hw, hh));

		// hairline ring so it reads crisply against busy item art
		g.setPaint(withAlpha(Color.WHITE, 0.35f * a));
		g.setStroke(new BasicStroke(0.6f));
		g.draw(new Ellipse2D.Float(x + 0.3f, y + 0.3f, s - 0.6f, s - 0.6f));

		g.setPaint(oldPaint);
		g.setStroke(oldStroke);
	}

	private static Color withAlpha(Color c, float a)
	{
		int alpha = Math.max(0, Math.min(255, Math.round(a * 255f)));
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	private static Color mix(Color c, Color with, float t)
	{
		return new Color(
			Math.round(c.getRed() + (with.getRed() - c.getRed()) * t),
			Math.round(c.getGreen() + (with.getGreen() - c.getGreen()) * t),
			Math.round(c.getBlue() + (with.getBlue() - c.getBlue()) * t));
	}
}
