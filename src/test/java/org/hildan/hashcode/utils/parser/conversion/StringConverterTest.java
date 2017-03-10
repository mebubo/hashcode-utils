package org.hildan.hashcode.utils.parser.conversion;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Theories.class)
public class StringConverterTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static class SuccessfulConversion<T> {
        final Class<T> type;
        final String input;
        final T targetValue;

        SuccessfulConversion(Class<T> type, String input, T targetValue) {
            this.type = type;
            this.input = input;
            this.targetValue = targetValue;
        }
    }

    private static class ConversionFailure {
        final Class<?> type;
        final String input;

        ConversionFailure(Class<?> type, String input) {
            this.type = type;
            this.input = input;
        }
    }

    @DataPoints({"all", "primitives"})
    public static SuccessfulConversion<?>[] conversions() {
        return new SuccessfulConversion<?>[] {
                new SuccessfulConversion<>(Boolean.class, "true", true),
                new SuccessfulConversion<>(Boolean.class, "True", true),
                new SuccessfulConversion<>(Boolean.class, "TRUE", true),
                new SuccessfulConversion<>(Boolean.class, "false", false),
                new SuccessfulConversion<>(Boolean.class, "False", false),
                new SuccessfulConversion<>(Boolean.class, "FALSE", false),
                new SuccessfulConversion<>(Integer.class, "42", 42),
                new SuccessfulConversion<>(Integer.class, "0", 0),
                new SuccessfulConversion<>(Integer.class, "-10", -10),
                new SuccessfulConversion<>(Long.class, "42", 42L),
                new SuccessfulConversion<>(Long.class, "0", 0L),
                new SuccessfulConversion<>(Long.class, "-10", -10L),
                new SuccessfulConversion<>(Float.class, "42", 42.0F),
                new SuccessfulConversion<>(Float.class, "4.2", 4.2F),
                new SuccessfulConversion<>(Float.class, "0", 0.0F),
                new SuccessfulConversion<>(Float.class, "-10.4", -10.4F),
                new SuccessfulConversion<>(Double.class, "42", 42.0),
                new SuccessfulConversion<>(Double.class, "4.2", 4.2),
                new SuccessfulConversion<>(Double.class, "0", 0.0),
                new SuccessfulConversion<>(Double.class, "-10.4", -10.4),
                new SuccessfulConversion<>(Character.class, "t", 't'),
                new SuccessfulConversion<>(Character.class, "5", '5'),
                new SuccessfulConversion<>(Character.class, " ", ' '),
        };
    }

    @DataPoints({"all", "strings"})
    public static SuccessfulConversion<?>[] conversionsToString() {
        return new SuccessfulConversion<?>[] {
                new SuccessfulConversion<>(String.class, "test", "test"),
                new SuccessfulConversion<>(String.class, " ", " "),
                new SuccessfulConversion<>(String.class, "", ""),
        };
    }

    @DataPoints
    public static ConversionFailure[] conversionFailures() {
        return new ConversionFailure[] {
                new ConversionFailure(Boolean.class, ""),
                new ConversionFailure(Boolean.class, " "),
                new ConversionFailure(Boolean.class, "not a boolean"),

                new ConversionFailure(Integer.class, ""),
                new ConversionFailure(Integer.class, " "),
                new ConversionFailure(Integer.class, "not a number"),
                new ConversionFailure(Integer.class, "42.0"),

                new ConversionFailure(Long.class, ""),
                new ConversionFailure(Long.class, " "),
                new ConversionFailure(Long.class, "not a number"),
                new ConversionFailure(Long.class, "42.0"),

                new ConversionFailure(Float.class, ""),
                new ConversionFailure(Float.class, " "),
                new ConversionFailure(Float.class, "not a number"),
                new ConversionFailure(Float.class, "4.2.0"),

                new ConversionFailure(Double.class, ""),
                new ConversionFailure(Double.class, " "),
                new ConversionFailure(Double.class, "not a number"),
                new ConversionFailure(Double.class, "4.2.0"),

                new ConversionFailure(Character.class, ""),
                new ConversionFailure(Character.class, "too long"),
        };
    }

    @Theory
    public void convert_success(@FromDataPoints("all") SuccessfulConversion<?> conversion) {
        Object convertedValue = StringConverter.convert(conversion.type, conversion.input);
        assertEquals(conversion.targetValue, convertedValue);
    }

    @Theory
    public void convert_failure(ConversionFailure conversion) {
        try {
            StringConverter.convert(conversion.type, conversion.input);
            fail();
        } catch (StringConversionException e) {
            assertEquals(conversion.type, e.getTargetType());
            assertEquals(conversion.input, e.getValue());
        }
    }

    @Test
    public void convert_failsOnNonPrimitiveTarget() {
        thrown.expect(IllegalArgumentException.class);
        StringConverter.convert(Object.class, "anything");
    }

    @Theory
    public void convertToPrimitiveWrapper_success(@FromDataPoints("primitives") SuccessfulConversion<?> conversion) {
        Object convertedValue = StringConverter.convertToPrimitiveWrapper(conversion.type, conversion.input);
        assertTrue(conversion.type.isInstance(convertedValue));
        assertEquals(conversion.targetValue, convertedValue);
    }

    @Theory
    public void convertToPrimitiveWrapper_failsOnBadInput(ConversionFailure conversion) {
        thrown.expect(StringConversionException.class);
        StringConverter.convertToPrimitiveWrapper(conversion.type, conversion.input);
    }

    @Theory
    public void convertToPrimitiveWrapper_failsOnStringTarget(@FromDataPoints("strings") SuccessfulConversion<?>
            conversion) {
        thrown.expect(IllegalArgumentException.class);
        StringConverter.convertToPrimitiveWrapper(conversion.type, conversion.input);
    }

    @Test
    public void convertToPrimitiveWrapper_failsOnNonPrimitiveTarget() {
        thrown.expect(IllegalArgumentException.class);
        StringConverter.convertToPrimitiveWrapper(Object.class, "anything");
    }

    @Test
    public void unusedConstructorCoverage() {
        new StringConverter();
    }
}