package org.hildan.hashcode;

public class InputParsingException extends RuntimeException {

  public InputParsingException(Throwable cause) {
    super(cause);
  }

  public InputParsingException(int lineNum, String line, String message) {
    super(String.format("Line %d: \"%s\"\n\t%s)", lineNum, line, message));
  }

  public InputParsingException(int lineNum, String line, Throwable cause) {
    super(String.format("Line %d: \"%s\")", lineNum, line), cause);
  }
}
