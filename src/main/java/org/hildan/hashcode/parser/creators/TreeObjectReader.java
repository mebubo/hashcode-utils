package org.hildan.hashcode.parser.creators;

import java.util.List;

import org.hildan.hashcode.HCHelper;
import org.hildan.hashcode.parser.Result;

public class TreeObjectReader<T> {

  private final Class<T> clazz;

  private final String[][] fieldNamesByLine;

  private final ChildReader<T>[] childrenReaders;

  public TreeObjectReader(Class<T> clazz, String[][] fieldNamesByLine, ChildReader<T>... childrenReaders) {
    this.clazz = clazz;
    this.fieldNamesByLine = fieldNamesByLine;
    this.childrenReaders = childrenReaders;
  }

  public Result<T> read(List<String> lines, HCHelper helper) {
    List<String> initLines = lines.subList(0, fieldNamesByLine.length);
    T obj = helper.parseObject(initLines, clazz, fieldNamesByLine);
    List<String> remainingLines = lines.subList(fieldNamesByLine.length, lines.size());
    for (ChildReader<T> childReader : childrenReaders) {
      remainingLines = childReader.create(remainingLines, obj, helper);
    }
    return new Result<>(obj, remainingLines);
  }
}
