package org.hildan.hashcode.config;

import org.intellij.lang.annotations.RegExp;

public class Config {

  @RegExp
  private static final String DEFAULT_SEPARATOR = "\\s";

  @RegExp
  public final String separator;

  public Config() {
    this.separator = DEFAULT_SEPARATOR;
  }

  public Config(@RegExp String separator) {
    this.separator = separator;
  }
}
