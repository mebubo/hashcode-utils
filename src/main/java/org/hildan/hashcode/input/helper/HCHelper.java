package org.hildan.hashcode.input.helper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hildan.hashcode.input.InputParsingException;
import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.conversion.TypeConverter;
import org.intellij.lang.annotations.RegExp;

public class HCHelper {

  @RegExp
  private final Config config;

  public HCHelper(Config config) {
    this.config = config;
  }

  public int[] parseIntArrayLine(String line) {
    String[] words = split(line);
    return Arrays.stream(words).mapToInt(Integer::parseInt).toArray();
  }

  public long[] parseLongArrayLine(String line) {
    String[] words = split(line);
    return Arrays.stream(words).mapToLong(Long::parseLong).toArray();
  }

  public double[] parseDoubleArrayLine(String line) {
    String[] words = split(line);
    return Arrays.stream(words).mapToDouble(Double::parseDouble).toArray();
  }

  public String[] parseStringArrayLine(String line) {
    return split(line);
  }

  public <T> T[] parseArrayLine(String line, Class<T> primitiveWrapperClass) {
    List<T> list = parsePrimitiveWrapperListLine(line, primitiveWrapperClass);
    //noinspection unchecked
    return list.toArray((T[])Array.newInstance(primitiveWrapperClass, list.size()));
  }

  public <T> List<T> parsePrimitiveWrapperListLine(String line, Class<T> wrapperClass) {
    String[] words = split(line);
    Function<String, T> convert = w -> TypeConverter.convertToPrimitiveWrapper(wrapperClass, w);
    return Arrays.stream(words).map(convert).collect(Collectors.toList());
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
      throw new InputParsingException("Could not create object of class " + clazz.getSimpleName(), e);
    }
  }

  private <T> void setFields(T obj, int lineNum, String line, String... fieldNames) {
    String[] words = split(line);
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
      field.set(obj, TypeConverter.convert(field.getType(), value));
    } catch (IllegalArgumentException e) {
      throw new InputParsingException(lineNum, line,
              "Type mismatch, cannot assign value '" + value + "' to field '" + field.getName() + "' of type " +
                      field.getType().getSimpleName());
    } catch (IllegalAccessException e) {
      throw new InputParsingException(lineNum, line, e);
    }
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

  public String[] split(String line) {
    return line.split(config.getSeparator());
  }
}
