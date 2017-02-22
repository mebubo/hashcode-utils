package org.hildan.hashcode.parser;

import java.util.List;

import org.hildan.hashcode.HCHelper;
import org.hildan.hashcode.config.Config;
import org.hildan.hashcode.parser.creators.TreeObjectReader;

public class HCParser {

  private final HCHelper helper;

  public HCParser(Config config) {
    this.helper = new HCHelper(config);
  }

  public <T> T parse(List<String> lines, TreeObjectReader<T> creator) {
    return creator.read(lines, helper).obj;
  }
}
