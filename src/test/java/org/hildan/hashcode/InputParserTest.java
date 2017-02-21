package org.hildan.hashcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class InputParserTest {

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
  public void parseIntArray() throws Exception {
    assertArrayEquals(new int[] {42}, inputParser.parseIntArray("42"));
    assertArrayEquals(new int[] {42, 12, 1}, inputParser.parseIntArray("42 12 1"));
  }

  @Test
  public void parseLongArray() throws Exception {
    assertArrayEquals(new long[] {42}, inputParser.parseLongArray("42"));
    assertArrayEquals(new long[] {42, 12, 1}, inputParser.parseLongArray("42 12 1"));
  }

  @Test
  public void parseDoubleArray() throws Exception {
    assertArrayEquals(new double[] {42}, inputParser.parseDoubleArray("42.0"), 0.00001);
    assertArrayEquals(new double[] {42, 12.33, 1.2}, inputParser.parseDoubleArray("42 12.33 1.2"), 0.00001);
  }

  @Test
  public void parseStringArray() throws Exception {
    assertArrayEquals(new String[] {"42.0"}, inputParser.parseStringArray("42.0"));
    assertArrayEquals(new String[] {"42", "12.33", "1.2"}, inputParser.parseStringArray("42 12.33 1.2"));
  }

  @Test
  public void parsePrimitiveList() throws Exception {
    assertEquals(Collections.singletonList(42.0), inputParser.parsePrimitiveWrapperList("42.0", Double.class));
    assertEquals(Arrays.asList(42.0, 12.33, 1.1), inputParser.parsePrimitiveWrapperList("42 12.33 1.1", Double
            .class));
  }

  @Test
  public void parseObject_oneLineBean() throws Exception {
    List<String> lines = new ArrayList<>();
    lines.add("42 Bob");
    TestBean bob = inputParser.parseObject(lines.get(0), TestBean.class, "age", "name");
    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
  }

  @Test
  public void parseObject_multilineBean() throws Exception {
    List<String> lines = new ArrayList<>();
    lines.add("42");
    lines.add("Bob");
    TestBean bob = inputParser.parseObject(lines, TestBean.class, new String[][]{{"age"}, {"name"}});
    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
  }

  @Test
  public void parseObjectList_oneLineBeans() throws Exception {
    List<String> lines = new ArrayList<>();
    lines.add("42 Bob");
    lines.add("12 Michel");
    List<TestBean> list = inputParser.parseObjectList(lines, TestBean.class, "age", "name");

    assertEquals(2, list.size());
    TestBean bob = list.get(0);
    TestBean michel = list.get(1);

    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
    assertEquals(12, michel.age);
    assertEquals("Michel", michel.name);
  }

  @Test
  public void parseObjectList_multilineBeans() throws Exception {
    List<String> lines = new ArrayList<>();
    lines.add("42");
    lines.add("Bob");
    lines.add("12");
    lines.add("Michel");
    List<TestBean> list = inputParser.parseObjectList(lines, TestBean.class, new String[][]{{"age"}, {"name"}});

    assertEquals(2, list.size());
    TestBean bob = list.get(0);
    TestBean michel = list.get(1);

    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
    assertEquals(12, michel.age);
    assertEquals("Michel", michel.name);
  }
}