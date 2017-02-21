package org.hildan.hashcode;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InputParser {

  private final String separator;

  public InputParser(String separator) {
    this.separator = separator;
  }

  public int[] parseIntArray(String line) {
    String[] words = line.split(separator);
    return Arrays.stream(words).mapToInt(Integer::parseInt).toArray();
  }

  public long[] parseLongArray(String line) {
    String[] words = line.split(separator);
    return Arrays.stream(words).mapToLong(Long::parseLong).toArray();
  }

  public double[] parseDoubleArray(String line) {
    String[] words = line.split(separator);
    return Arrays.stream(words).mapToDouble(Double::parseDouble).toArray();
  }

  public String[] parseStringArray(String line) {
    return line.split(separator);
  }

  public <T> List<T> parsePrimitiveWrapperList(String line, Class<T> clazz) {
    String[] words = line.split(separator);
    if (clazz.isPrimitive()) {
      throw new IllegalArgumentException("Cannot create list of primitive type");
    }
    return Arrays.stream(words).map(w -> clazz.cast(convert(clazz, w))).collect(Collectors.toList());
  }

  public <T> T parseObject(String line, Class<T> clazz, String... fieldNames) {
    return parseObject(Collections.singletonList(line), clazz, new String[][] {fieldNames});
  }

  public <T> T parseObject(List<String> lines, Class<T> clazz, String[][] fieldNamesByRow) {
    try {
      T obj = clazz.newInstance();
      for (int row = 0; row < fieldNamesByRow.length; row++) {
        setFields(obj, row, lines.get(row), fieldNamesByRow[row]);
      }
      return obj;
    } catch (IllegalAccessException | InstantiationException e) {
      throw new InputParsingException(e);
    }
  }

  private <T> void setFields(T obj, int lineNum, String line, String... fieldNames) {
    String[] words = line.split(separator);
    if (words.length != fieldNames.length) {
      throw new InputParsingException(lineNum, line,
              "The number of words doesn't match the expected fields: " + Arrays.toString(fieldNames));
    }
    for (int f = 0; f < fieldNames.length; f++) {
      try {
        Field field = obj.getClass().getDeclaredField(fieldNames[f]);
        setField(obj, words[f], field, lineNum, line);
      } catch (NoSuchFieldException e) {
        throw new InputParsingException(lineNum, line, e);
      }
    }

  }

  private static <T> void setField(T obj, String value, Field field, int lineNum, String line) {
    try {
      field.setAccessible(true);
      field.set(obj, convert(field.getType(), value));
    } catch (IllegalArgumentException e) {
      throw new InputParsingException(lineNum, line,
              "Type mismatch, cannot assign value '" + value + "' to field '" + field.getName() + "' of type " +
                      field.getType().getSimpleName());
    } catch (IllegalAccessException e) {
      throw new InputParsingException(lineNum, line, e);
    }
  }

  private static Object convert(Class<?> targetType, String value) {
    if (targetType.isAssignableFrom(boolean.class)|| targetType.equals(Boolean.class)) {
      return convertToBoolean(value);
    } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
      return Long.valueOf(value);
    } else if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
      return Integer.valueOf(value);
    } else if (targetType.equals(double.class) || targetType.equals(Double.class)) {
      return Double.valueOf(value);
    } else if (targetType.equals(float.class) || targetType.equals(Float.class)) {
      return Float.valueOf(value);
    } else if (targetType.equals(String.class)) {
      return value;
    }
    throw new IllegalArgumentException(value);
  }

  private static boolean convertToBoolean(String value) {
    if (value.equalsIgnoreCase("false") || "0".equals(value)) {
      return false;
    }
    if (value.equalsIgnoreCase("true") || "1".equals(value)) {
      return true;
    }
    throw new IllegalArgumentException(value);
  }

  public <T> Object[] parseObjectArray(List<String> lines, Class<T> clazz, String... fieldNames) {
    List<T> list = parseObjectList(lines, clazz, fieldNames);
    //noinspection unchecked
    return list.toArray((T[])Array.newInstance(clazz, list.size()));
  }

  public <T> List<T> parseObjectList(List<String> lines, Class<T> clazz, String... fieldNames) {
    return lines.stream().map(l -> parseObject(l, clazz, fieldNames)).collect(Collectors.toList());
  }

  public <T> List<T> parseObjectList(List<String> lines, Class<T> clazz, String[][] fieldNamesByRow) {
    List<T> list = new ArrayList<>(lines.size());
    int nbLinesPerBean = fieldNamesByRow.length;
    for (int i = 0; i < lines.size() / nbLinesPerBean; i++) {
      int firstLine = i * nbLinesPerBean;
      list.add(parseObject(lines.subList(firstLine, firstLine + nbLinesPerBean), clazz, fieldNamesByRow));
    }
    return list;
  }
}
