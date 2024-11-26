package net.cakemc.discord.bot.captcha;

import net.logicsquad.nanocaptcha.image.ImageCaptcha;
import net.cakemc.discord.bot.captcha.impl.AlphanumericContentProducer;
import net.cakemc.discord.bot.captcha.impl.RandomLineWordRenderer;
import net.cakemc.discord.bot.captcha.impl.MarsagliaPolarGaussianProducer;
import net.cakemc.discord.bot.captcha.impl.RealGradiatedBackgroundProducer;
import rip.lunarydess.lilith.utility.ArrayKit;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class Captcha {
  public static final Font FONT_DEFAULT;
  public static final Font[] FONTS;
  private static final Color[] COLORS = {
      new Color(243, 139, 168, 255),
      new Color(235, 160, 172, 255),
      new Color(250, 179, 135, 255),
      new Color(249, 226, 175, 255),
      new Color(166, 227, 161, 255),
      new Color(148, 226, 213, 255),
      new Color(137, 220, 235, 255),
      new Color(116, 199, 236, 255),
      new Color(137, 180, 250, 255),
      new Color(180, 190, 254, 255)
  };

  static {
    Font[] fonts = initFonts().toArray(Font[]::new);
    FONT_DEFAULT = fonts[0];
    FONTS = ArrayKit.sliceFrom(Font[]::new, fonts, 1);
  }

  private static ArrayList<Font> initFonts() {
    ArrayList<Font> fonts = new ArrayList<>();
    String defaultFontName = "OCRAEXT.ttf";

    try {
      fonts.add(Font.createFont(Font.TRUETYPE_FONT, Captcha.class.getResourceAsStream("/fonts/OCRAEXT.TTF")).deriveFont(Font.PLAIN, 48.0F));
    } catch (FontFormatException | IOException exception) {
      throw new RuntimeException("Default-Font couldn't be loaded!", exception);
    }

    URL fontsFolderURL = Captcha.class.getResource("/fonts");
    if (fontsFolderURL == null) return fonts;

    File fontsFolder = new File(fontsFolderURL.getPath());
    if (Files.notExists(fontsFolder.toPath())) return fonts;

    File[] fontsFiles = fontsFolder.listFiles(
        (dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".ttf") &&
            !name.toLowerCase(Locale.ROOT).equals(defaultFontName.toLowerCase(Locale.ROOT))
    );
    if (fontsFiles == null) return fonts;
    for (File file : fontsFiles) {
      Font font;
      try {
        font = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(Font.PLAIN, 48.0F);
      } catch (FontFormatException | IOException ignored) {
        continue;
      }
      if (font == null) continue;
      fonts.add(font);
    }

    return fonts;
  }

  public static CompletableFuture<ImageCaptcha> generate() {
    return CompletableFuture.supplyAsync(() -> new ImageCaptcha.Builder(640, 360)
        .addContent(new AlphanumericContentProducer(5), new RandomLineWordRenderer.Builder()
            .randomColor(COLORS[0].darker(), ArrayKit.sliceFrom(
                Color[]::new, Arrays.stream(COLORS).map(color -> color.darker().darker()).toArray(Color[]::new), 1
            )).randomFont(FONT_DEFAULT, FONTS).build())
        .addBackground(new RealGradiatedBackgroundProducer(COLORS)).addBorder()
        .addNoise(new MarsagliaPolarGaussianProducer(12, 4, false))
        .build());
  }
}
