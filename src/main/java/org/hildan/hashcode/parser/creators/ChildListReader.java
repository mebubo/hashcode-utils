package org.hildan.hashcode.parser.creators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.hildan.hashcode.HCHelper;
import org.hildan.hashcode.parser.Result;

public class ChildListReader<T, P> implements ChildReader<P> {

  private final Function<P, Integer> getCount;

  private final TreeObjectReader<T> itemCreator;

  private final BiConsumer<P, List<T>> updateParent;

  public ChildListReader(Function<P, Integer> getCount, TreeObjectReader<T> itemCreator,
          BiConsumer<P, List<T>> updateParent) {
    this.getCount = getCount;
    this.itemCreator = itemCreator;
    this.updateParent = updateParent;
  }

  @Override
  public List<String> create(List<String> lines, P parent, HCHelper helper) {
    List<T> list = new ArrayList<>();
    List<String> remainingLines = lines;
    int count = getCount.apply(parent);
    for (int i = 0; i < count; i++) {
      Result<T> result = itemCreator.read(remainingLines, helper);
      remainingLines = result.remainingLines;
      list.add(result.obj);
    }
    updateParent.accept(parent, list);
    return remainingLines;
  }
}
