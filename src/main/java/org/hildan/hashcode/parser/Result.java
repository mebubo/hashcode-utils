package org.hildan.hashcode.parser;

import java.util.List;

public class Result<T> {
  public final T obj;
  public final List<String> remainingLines;

  public Result(T obj, List<String> remainingLines) {
    this.obj = obj;
    this.remainingLines = remainingLines;
  }
}
