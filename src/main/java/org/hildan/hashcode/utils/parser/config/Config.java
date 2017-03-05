package org.hildan.hashcode.utils.parser.config;

import org.intellij.lang.annotations.RegExp;

public class Config {

  @RegExp
  private static final String DEFAULT_SEPARATOR = "\\s";

  @RegExp
  private final String separator;

  public Config() {
    this.separator = DEFAULT_SEPARATOR;
  }

  public Config(@RegExp String separator) {
    this.separator = separator;
  }

  public String getSeparator() {
    return separator;
  }
}
