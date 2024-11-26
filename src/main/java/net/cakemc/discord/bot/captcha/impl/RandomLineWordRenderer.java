package net.cakemc.discord.bot.captcha.impl;

import net.logicsquad.nanocaptcha.image.renderer.AbstractWordRenderer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public final class RandomLineWordRenderer extends AbstractWordRenderer {
  private static final Color TRANSLUCENT_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.4125F);
  private static final Map<RenderingHints.Key, Object> DEFAULT_HINTS = Map.of(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY,

      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON,

      RenderingHints.KEY_ALPHA_INTERPOLATION,
      RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,

      RenderingHints.KEY_COLOR_RENDERING,
      RenderingHints.VALUE_COLOR_RENDER_QUALITY
  );

  private RandomLineWordRenderer(
      double xOffset,
      double yOffset,
      Supplier<Color> colorSupplier,
      Supplier<Font> fontSupplier
  ) {
    super(
        xOffset,
        yOffset,
        colorSupplier,
        fontSupplier
    );
  }

  @Override
  public void render(final String input, BufferedImage image) {
    final Graphics2D graphics = image.createGraphics();
    graphics.setRenderingHints(DEFAULT_HINTS);
    FontRenderContext fontRenderContext = graphics.getFontRenderContext();
    Font[] fonts = new Font[input.length()];

    double maxWidth = 0.0D;
    double maxHeight = 0.0D;
    for (int i = 0; i < input.length(); i++) {
      Font font = fontSupplier().get();
      GlyphVector glyphVec = font.createGlyphVector(fontRenderContext, new char[]{input.charAt(i)});
      fonts[i] = font;
      maxWidth += glyphVec.getVisualBounds().getWidth();
      double fontHeight = glyphVec.getVisualBounds().getHeight();
      if (maxHeight >= fontHeight) continue;
      maxHeight = fontHeight;
    }

    Rectangle2D[] points = new Rectangle2D[fonts.length];
    double x = 0.0D, xAdd = 0.0D, xAddPrev = 0.0D;
    int idx = -1;
    for (var font : fonts) {
      ++idx;
      xAddPrev = xAdd;
      xAdd = (image.getWidth() * (1.0D / (Math.max(input.length() - idx, 1.0D)) * ThreadLocalRandom.current().nextDouble()));
      char[] chars = new char[]{input.charAt(idx)};
      x += xAdd;
      double y = image.getHeight() * ThreadLocalRandom.current().nextDouble();

      graphics.setFont(font);
      graphics.setColor(colorSupplier().get());
      Rectangle2D fontXY = graphics.getFont().createGlyphVector(fontRenderContext, chars).getVisualBounds();
      double
          renderX = Math.max(fontXY.getWidth(), Math.min(image.getWidth() - maxWidth, x + (xAdd < xAddPrev ? fontXY.getWidth() : 0.0D))),
          renderY = Math.max(fontXY.getHeight(), Math.min(image.getHeight() - maxHeight - fontXY.getHeight(), y));
      double
          pointX = renderX + fontXY.getWidth() * 0.5D,
          pointY = renderY - fontXY.getHeight() * 0.5D;
      points[idx] = new Rectangle2D.Double(pointX, pointY, pointX, pointY);
      graphics.drawChars(
          chars, 0, 1,
          (int) renderX, (int) renderY
      );
    }

    graphics.setColor(TRANSLUCENT_COLOR);
    graphics.setStroke(new BasicStroke(10.0F));
    for (int i = 1; i < points.length; i++) {
      Rectangle2D
          start = points[i - 1],
          end = points[i];
      graphics.drawLine(
          (int) start.getX(),
          (int) start.getY(),
          (int) end.getX(),
          (int) end.getY()
      );
    }
  }

  public static class Builder extends AbstractWordRenderer.Builder {
    @Override
    public RandomLineWordRenderer build() {
      return new RandomLineWordRenderer(xOffset, yOffset, colorSupplier, fontSupplier);
    }
  }
}
