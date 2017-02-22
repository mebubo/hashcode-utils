package org.hildan.hashcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hildan.hashcode.config.Config;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HCHelperTest {

  private HCHelper hcHelper;

  public static class TestBean {
    private int age;
    public String name;
  }

  @Before
  public void setUp() {
    hcHelper = new HCHelper(new Config());
  }

  @Test
  public void parseIntArray_succeeds() {
    assertArrayEquals(new int[] {42}, hcHelper.parseIntArrayLine("42"));
    assertArrayEquals(new int[] {42, 12, 1}, hcHelper.parseIntArrayLine("42 12 1"));
  }

  @Test
  public void parseLongArray() {
    assertArrayEquals(new long[] {42}, hcHelper.parseLongArrayLine("42"));
    assertArrayEquals(new long[] {42, 12, 1}, hcHelper.parseLongArrayLine("42 12 1"));
  }

  @Test
  public void parseDoubleArray() {
    assertArrayEquals(new double[] {42}, hcHelper.parseDoubleArrayLine("42.0"), 0.00001);
    assertArrayEquals(new double[] {42, 12.33, 1.2}, hcHelper.parseDoubleArrayLine("42 12.33 1.2"), 0.00001);
  }

  @Test
  public void parseStringArray() {
    assertArrayEquals(new String[] {"42.0"}, hcHelper.parseStringArrayLine("42.0"));
    assertArrayEquals(new String[] {"42", "12.33", "1.2"}, hcHelper.parseStringArrayLine("42 12.33 1.2"));
  }

  @Test
  public void parsePrimitiveList() {
    assertEquals(Collections.singletonList(42.0), hcHelper.parsePrimitiveWrapperListLine("42.0", Double.class));
    assertEquals(Arrays.asList(42.0, 12.33, 1.1), hcHelper.parsePrimitiveWrapperListLine("42 12.33 1.1", Double
            .class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseArrayLine_failsOnPrimitive() {
    hcHelper.parseArrayLine("42 12", int.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseArrayLine_failsOnNonPrimitiveWrapperObject() {
    hcHelper.parseArrayLine("42 12", String.class);
  }

  @Test
  public void parseObject_oneLineBean() {
    List<String> lines = new ArrayList<>();
    lines.add("42 Bob");
    TestBean bob = hcHelper.parseObject(lines.get(0), TestBean.class, "age", "name");
    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
  }

  @Test
  public void parseObject_multilineBean() {
    List<String> lines = new ArrayList<>();
    lines.add("42");
    lines.add("Bob");
    TestBean bob = hcHelper.parseObject(lines, TestBean.class, new String[][]{{"age"}, {"name"}});
    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
  }

  @Test
  public void parseObjectList_oneLineBeans() {
    List<String> lines = new ArrayList<>();
    lines.add("42 Bob");
    lines.add("12 Michel");
    List<TestBean> list = hcHelper.parseObjectList(lines, TestBean.class, "age", "name");

    assertEquals(2, list.size());
    TestBean bob = list.get(0);
    TestBean michel = list.get(1);

    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
    assertEquals(12, michel.age);
    assertEquals("Michel", michel.name);
  }

  @Test
  public void parseObjectList_multilineBeans() {
    List<String> lines = new ArrayList<>();
    lines.add("42");
    lines.add("Bob");
    lines.add("12");
    lines.add("Michel");
    List<TestBean> list = hcHelper.parseObjectList(lines, TestBean.class, new String[][]{{"age"}, {"name"}});

    assertEquals(2, list.size());
    TestBean bob = list.get(0);
    TestBean michel = list.get(1);

    assertEquals(42, bob.age);
    assertEquals("Bob", bob.name);
    assertEquals(12, michel.age);
    assertEquals("Michel", michel.name);
  }
}