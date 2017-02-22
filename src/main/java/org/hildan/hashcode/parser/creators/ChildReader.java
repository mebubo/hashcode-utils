package org.hildan.hashcode.parser.creators;

import java.util.List;

import org.hildan.hashcode.HCHelper;

public interface ChildReader<P> {

  List<String> create(List<String> lines, P parent, HCHelper helper);
}
