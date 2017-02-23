package org.hildan.hashcode.input;

public class InputParsingException extends RuntimeException {

  public InputParsingException() {
    super();
  }

  public InputParsingException(String message) {
    super(message);
  }

  public InputParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  public InputParsingException(int lineNum, String line, String message) {
    super(String.format("Line %d: \"%s\"\n\t%s", lineNum, line, message));
  }

  public InputParsingException(int lineNum, String line, Throwable cause) {
    super(String.format("Line %d: \"%s\"", lineNum, line), cause);
  }

  public InputParsingException(int lineNum, String line, String message, Throwable cause) {
    super(String.format("Line %d: \"%s\"\n\t%s", lineNum, line, message), cause);
  }
}
