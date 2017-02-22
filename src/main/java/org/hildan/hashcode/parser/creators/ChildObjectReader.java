package org.hildan.hashcode.parser.creators;

import java.util.List;
import java.util.function.BiConsumer;

import org.hildan.hashcode.HCHelper;
import org.hildan.hashcode.parser.Result;

public class ChildObjectReader<T, P> implements ChildReader<P> {

  private final TreeObjectReader<T> objectCreator;

  private final BiConsumer<P, T> updateParent;

  public ChildObjectReader(TreeObjectReader<T> objectCreator, BiConsumer<P, T> updateParent) {
    this.objectCreator = objectCreator;
    this.updateParent = updateParent;
  }

  @Override
  public List<String> create(List<String> lines, P parent, HCHelper helper) {
    Result<T> result = objectCreator.read(lines, helper);
    updateParent.accept(parent, result.obj);
    return result.remainingLines;
  }
}
