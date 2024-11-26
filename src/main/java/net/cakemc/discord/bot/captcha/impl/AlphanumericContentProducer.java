package net.cakemc.discord.bot.captcha.impl;

import net.logicsquad.nanocaptcha.content.AbstractContentProducer;
import rip.lunarydess.lilith.utility.StringKit;

import java.security.SecureRandom;

public final class AlphanumericContentProducer extends AbstractContentProducer {
  // @formatter:off
  private static final char[] DEFAULT_CHARS = (
          StringKit.getLowerChars() +
          StringKit.getUpperChars() +
          StringKit.getNumberChars()
  ).toCharArray();
  // @formatter:on
  private static final SecureRandom RANDOM = new SecureRandom();
  private final int length;

  public AlphanumericContentProducer() {
    this(7);
  }

  public AlphanumericContentProducer(final int length) {
    super(length, DEFAULT_CHARS.clone());
    this.length = length;
  }

  @Override
  public String getContent() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.length; i++)
      sb.append(DEFAULT_CHARS[RANDOM.nextInt(DEFAULT_CHARS.length)]);
    return sb.toString();
  }
}
