package org.hildan.hashcode;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InputParserTest {

  private List<String> lines;

  private InputParser inputParser;

  public static class TestBean {
    private int age;
    public String name;
  }

  @Before
  public void setUp() throws Exception {
    inputParser = new InputParser(" ");
  }

  @Test
  public void parseObject_oneLineBean() throws Exception {
    lines = new ArrayList<>();
    lines.add("42 Bob");
    TestBean bob = inputParser.parseObject(lines.get(0), TestBean.class, "age", "name");
    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
  }

  @Test
  public void parseObject_multilineBean() throws Exception {
    lines = new ArrayList<>();
    lines.add("42");
    lines.add("Bob");
    TestBean bob = inputParser.parseObject(lines, TestBean.class, new String[][]{{"age"}, {"name"}});
    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
  }

  @Test
  public void parseList_oneLineBeans() throws Exception {
    lines = new ArrayList<>();
    lines.add("42 Bob");
    lines.add("12 Michel");
    List<TestBean> list = inputParser.parseList(lines, TestBean.class, "age", "name");

    assertEquals(2, list.size());
    TestBean bob = list.get(0);
    TestBean michel = list.get(1);

    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
    assertEquals(12, michel.age);
    assertEquals("Michel", michel.name);
  }

  @Test
  public void parseList_multilineBeans() throws Exception {
    lines = new ArrayList<>();
    lines.add("42");
    lines.add("Bob");
    lines.add("12");
    lines.add("Michel");
    List<TestBean> list = inputParser.parseList(lines, TestBean.class, new String[][]{{"age"}, {"name"}});

    assertEquals(2, list.size());
    TestBean bob = list.get(0);
    TestBean michel = list.get(1);

    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
    assertEquals(12, michel.age);
    assertEquals("Michel", michel.name);
  }
}