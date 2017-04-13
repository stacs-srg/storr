/***************************************************************************
 *                                                                         *
 * nds Library                                                             *
 * Copyright (C) 2005-2011 Distributed Systems Architecture Research Group *
 * University of St Andrews, Scotland                                      *
 * http://www-systems.cs.st-andrews.ac.uk/                                 *
 *                                                                         *
 * This file is part of nds, a package of utility classes.                 *
 *                                                                         *
 * nds is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation, either version 3 of the License, or       *
 * (at your option) any later version.                                     *
 *                                                                         *
 * nds is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License       *
 * along with nds.  If not, see <http://www.gnu.org/licenses/>.            *
 *                                                                         *
 ***************************************************************************/
package uk.ac.standrews.cs.storr.impl.temp;

import org.json.JSONException;
import org.json.JSONTokener;
import uk.ac.standrews.cs.utilities.archive.Diagnostic;

import java.io.Reader;

/**
 * Class to parse JSON values from a stream. Aims to minimize the number of object creations.
 *
 * @author Alan Dearle al@st-andrews.ac.uk
 */
public class JSONReader {

    /**
     * The Constant ERROR.
     */
    public static final int ERROR = -2;
    /**
     * The Constant UNINITIALIZED.
     */
    public static final int UNINITIALIZED = -1;
    /**
     * The Constant NULL.
     */
    public static final int NULL = 0;
    /**
     * The Constant BOOLEAN.
     */
    public static final int BOOLEAN = 1;
    /**
     * The Constant STRING.
     */
    public static final int STRING = 2;
    /**
     * The Constant INTEGER.
     */
    public static final int INTEGER = 3;
    /**
     * The Constant DOUBLE.
     */
    public static final int DOUBLE = 4;
    /**
     * The Constant LONG.
     */
    public static final int LONG = 5;
    /**
     * The Constant OBJECT.
     */
    public static final int OBJECT = 6;
    /**
     * The Constant ENDOBJECT.
     */
    public static final int ENDOBJECT = 7;
    /**
     * The Constant ARRAY.
     */
    public static final int ARRAY = 8;
    /**
     * The Constant ENDARRAY.
     */
    public static final int ENDARRAY = 9;
    /**
     * The Constant COLON.
     */
    public static final int COLON = 10;
    /**
     * The Constant COMMA.
     */
    public static final int COMMA = 11;
    private static final char BACKSLASH_CHAR = '\'';
    private static final char QUOTE_CHAR = '"';
    private static final int INT_RADIX = 16;
    /**
     * The tokenizer.
     */
    protected final JSONTokener tokenizer;
    protected int the_symbol = UNINITIALIZED;
    protected int theInteger;
    private boolean theBoolean;
    private String theString;
    private double theDouble;
    private Long theLong;

    /**
     * Construct a JSON reader using the provided {@link Reader}.
     *
     * @param reader the reader
     */
    public JSONReader(final Reader reader) {

        tokenizer = new JSONTokener(reader);
    }

    /**
     * providers a printable version of the current symbol in the parse.
     *
     * @return a printable version of the current symbol in the parse.
     */
    public String currentSymbol() {

        return decode(the_symbol);
    }

    /**
     * Checks whether the current symbol in the parse is of the kind passed in the given symbol.
     *
     * @param symbol the symbol
     * @return true if the current symbol in the parse is of the kind passed in the given symbol.
     */
    public boolean have(final int symbol) {

        return symbol == the_symbol;
    }

    /**
     * Checks whether the current symbol in the parse is null. If it is null it eats the input symbol, if that symbol is a COMMA that will be consumed too.
     *
     * @return true if the current symbol in the parse is null
     * @throws JSONException the jSON exception
     */
    public boolean checkNull() throws JSONException {

        final boolean is_null = have(NULL);
        if (is_null) {
            nextSymbol();
            if (have(COMMA)) {
                nextSymbol();
            }
        }
        return is_null;
    }

    /**
     * Consumes a start object symbol from the stream.
     *
     * @throws JSONException if some other item is found
     */
    public void object() throws JSONException {

        if (the_symbol == OBJECT) {
            nextJSONSymbol();
        } else {
            throw new JSONException("Expected an object, but found: " + decode(the_symbol));
        }
    }

    /**
     * Consumes an end object symbol from the stream.
     *
     * @throws JSONException the jSON exception
     */
    public void endObjectNoLookahead() throws JSONException {

        if (the_symbol != ENDOBJECT) {
            throw new JSONException("expected } found: " + decode(the_symbol));
        }
    }

    /**
     * Consumes an end object symbol from the stream.
     *
     * @throws JSONException if some other item is found.
     */
    public void endObject() throws JSONException {

        if (the_symbol == ENDOBJECT) {
            nextJSONSymbol(COMMA);
        } else {
            throw new JSONException("expected } found: " + decode(the_symbol));
        }
    }

    /**
     * Consumes a start array symbol from the stream.
     *
     * @throws JSONException if some other item is found.
     */
    public void array() throws JSONException {

        if (the_symbol == ARRAY) {
            nextJSONSymbol();
        } else {
            throw new JSONException("expected [ found: " + decode(the_symbol));
        }
    }

    /**
     * Consumes an end array symbol from the stream.
     *
     * @throws JSONException if some other item is found.
     */
    public void endArray() throws JSONException {

        if (the_symbol == ENDARRAY) {
            nextJSONSymbol(COMMA);
        } else {
            throw new JSONException("expected ] found: " + decode(the_symbol));
        }
    }

    /**
     * Consumes a key symbol from the stream.
     *
     * @return the key token from the stream
     * @throws JSONException if some other item is found
     */
    public String key() throws JSONException {

        if (the_symbol == STRING) {
            final String result = theString;
            nextJSONSymbol(COLON);
            return result;
        }

        throw new JSONException("expected key, found: " + decode(the_symbol));
    }

    /**
     * Consumes a key symbol from the stream. Expects the consumed key to be equal to the given key.
     *
     * @param expected the expected key
     * @return the consumed key
     * @throws JSONException if the consumed key is not equal to the given key
     */
    public String key(final String expected) throws JSONException {

        final String k = key();
        if (!k.equals(expected)) {
            throw new JSONException("expected key not found: expected: " + expected + ", read: " + k);
        }
        return k;
    }

    /**
     * Consumes a null symbol from the stream.
     *
     * @throws JSONException if some other item is found
     */
    public void nullValue() throws JSONException {

        if (the_symbol == NULL) {
            nextJSONSymbol(COMMA);
        } else {
            throw new JSONException("expected null, found: " + decode(the_symbol));
        }
    }

    /**
     * Consumes a boolean value from the stream.
     *
     * @return the value from the stream
     * @throws JSONException if some other item is found
     */
    public boolean booleanValue() throws JSONException {

        if (the_symbol == BOOLEAN) {
            final boolean result = theBoolean;
            nextJSONSymbol(COMMA);
            return result;
        }

        throw new JSONException("expected boolean, found: " + decode(the_symbol));
    }

    /**
     * Consumes a double value from the stream.
     *
     * @return the value from the stream
     * @throws JSONException if some other item is found
     */
    public double doubleValue() throws JSONException {

        if (the_symbol == DOUBLE) {
            final double result = theDouble;
            nextJSONSymbol(COMMA);
            return result;
        } else if (the_symbol == INTEGER) {
            final double result = theInteger;
            nextJSONSymbol(COMMA);
            return result;
        }

        throw new JSONException("expected double, found: " + decode(the_symbol));
    }

    /**
     * Consumes an integer value from the stream.
     *
     * @return the value from the stream
     * @throws JSONException if some other item is found
     */
    public int intValue() throws JSONException {

        if (the_symbol == INTEGER) {
            final int result = theInteger;
            nextJSONSymbol(COMMA);
            return result;
        }

        throw new JSONException("expected int, found: " + decode(the_symbol));
    }

    /**
     * Consumes a long value from the stream.
     *
     * @return the value from the stream
     * @throws JSONException if some other item is found
     */
    public long longValue() throws JSONException {

        if (the_symbol == LONG) {
            final long result = theLong;

            nextJSONSymbol(COMMA);
            return result;
        } else if (the_symbol == INTEGER) {
            // The long value may be incorrectly typed as an integer, so just take convert it back to a long.
            final long result = theInteger;

            nextJSONSymbol(COMMA);
            return result;
        }

        throw new JSONException("expected long, found: " + decode(the_symbol));
    }

    /**
     * Consumes an string value from the stream.
     *
     * @return the value from the stream
     * @throws JSONException the jSON exception
     */
    public String stringValue() throws JSONException {

        if (the_symbol == STRING) {
            final String result = theString;
            nextJSONSymbol(COMMA);
            return result;
        }

        throw new JSONException("expected string, found: " + decode(the_symbol));
    }

    /**
     * Moves the parse on one symbol and sets the variable the_symbol to be the current symbol. This value can be accessed using currentSymbol().
     *
     * @throws JSONException if there is a syntax error, or no characters are available Copied and adapted from JSONTokener
     */
    public void nextSymbol() throws JSONException {

        char c = tokenizer.nextClean();

        String s;

        switch (c) {
            case QUOTE_CHAR:
            case BACKSLASH_CHAR:
                theString = tokenizer.nextString(c);
                setNextSymbol(STRING);
                return;
            case '{':
                setNextSymbol(OBJECT);
                return;
            case ':':
                setNextSymbol(COLON);
                return;
            case ',':
                setNextSymbol(COMMA);
                return;
            case '}':
                setNextSymbol(ENDOBJECT);
                return;
            case '[':
            case '(':
                setNextSymbol(ARRAY);
                return;
            case ']':
            case ')':
                setNextSymbol(ENDARRAY);
                return;
            default:
                break;
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        final StringBuffer sb = new StringBuffer();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = tokenizer.next();
        }
        tokenizer.back();

        s = sb.toString().trim();
        if (s.equals("")) {
            throw new JSONException("No data available");
        }
        stringToValue(s);
    }

    /**
     * Checks if the end of stream is reached.
     *
     * @return true, if is end of stream is reached
     * @throws JSONException if an error occurs during parsing
     */
    public boolean isEndOfStream() throws JSONException {

        tokenizer.next();
        final boolean end = tokenizer.end();
        tokenizer.back();
        return end;
    }

    // -------------------------------------------------------------------------------------------

    /**
     * Consumes a comma symbol from the stream.
     *
     * @throws JSONException if some other item is found
     */
    private void comma() throws JSONException {

        if (the_symbol == COMMA) {
            nextJSONSymbol();
        } else {
            throw new JSONException("expected comma, found " + decode(the_symbol));
        }
    }

    /**
     * Consumes a colon symbol from the stream.
     *
     * @throws JSONException if some other item is found
     */
    private void colon() throws JSONException {

        if (the_symbol == COLON) {
            nextJSONSymbol();
        } else {
            throw new JSONException("expected colon, found " + decode(the_symbol));
        }
    }

    /**
     * Call nextSymbol and absorb the exception. Only used internally by other parse commands. This will be caught on next call - the_symbol set to error symbol
     */
    private void nextJSONSymbol() {

        try {
            nextSymbol();
        } catch (final Exception e) {
            Diagnostic.trace("Exception in nextJSONSymbol", Diagnostic.FULL);
            setNextSymbol(ERROR);
        }
    }

    private void setNextSymbol(final int symbol) {

        the_symbol = symbol;
    }

    /**
     * Call nextJSONSymbol This also eats legal JSON micro syntax such as colons and commas.
     *
     * @param symbol the symbol
     */
    private void nextJSONSymbol(final int symbol) {

        try {
            nextJSONSymbol();
            if (symbol == COLON) { // must be a colon
                colon();
            } else if (symbol == COMMA) { // eat a comma if we expect one
                if (have(COMMA)) {
                    comma();
                }
            }
        } catch (final JSONException e) {

            Diagnostic.trace("Error in nextJSONSymbol(symbol) expecting:" + decode(symbol) + ", current is: " + currentSymbol(), Diagnostic.FULL);
            setNextSymbol(ERROR);
        }
    }

    /**
     * Try to convert a string into a number, boolean, or null. If the string can't be converted, return the string.
     *
     * @param s a String. Copied and adapted from JSONObject
     */
    private void stringToValue(final String s) {

        if (s.equals("")) { // What is this case?
            setNextSymbol(STRING);
            theString = s;
            return;
        }
        if (s.equalsIgnoreCase("true")) {
            setNextSymbol(BOOLEAN);
            theBoolean = true;
            return;
        }
        if (s.equalsIgnoreCase("false")) {
            setNextSymbol(BOOLEAN);
            theBoolean = false;
            return;
        }
        if (s.equalsIgnoreCase("null")) {
            setNextSymbol(NULL);
            return;
        }

        /*
         * If it might be a number, try converting it.
         * We support the non-standard 0x- convention.
         * If a number cannot be produced, then the value will just
         * be a string. Note that the 0x-, plus, and implied string
         * conventions are non-standard. A JSON parser may accept
         * non-JSON forms as long as it accepts all correct JSON forms.
         */

        final char b = s.charAt(0);
        if (b >= '0' && b <= '9' || b == '.' || b == '-' || b == '+') {
            if (b == '0' && s.length() > 2 && (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                try {
                    theInteger = Integer.parseInt(s.substring(2), INT_RADIX);
                    setNextSymbol(INTEGER);
                } catch (final Exception ignore) {
                }
            }
            try {
                if (s.indexOf('.') > -1 || s.indexOf('e') > -1 || s.indexOf('E') > -1) {
                    theDouble = Double.valueOf(s);
                    setNextSymbol(DOUBLE);
                    return;
                }

                final Long myLong = new Long(s);
                if (myLong.longValue() == myLong.intValue()) {
                    theInteger = myLong.intValue();
                    setNextSymbol(INTEGER);
                    return;
                }

                theLong = myLong;
                setNextSymbol(LONG);
                return;
            } catch (final Exception ignore) {
            }
        }
        // if we drop through to here we have a String
        setNextSymbol(STRING);
        theString = s;
    }

    protected String decode(final int symbol) {

        switch (symbol) {
            case ERROR:
                return "parse error";
            case UNINITIALIZED:
                return "uninitialised - call nextSymbol()";
            case NULL:
                return "null";
            case BOOLEAN:
                return theBoolean ? "true" : "false";
            case STRING:
                return theString;
            case INTEGER:
                return Integer.toString(theInteger);
            case DOUBLE:
                return Double.toString(theDouble);
            case LONG:
                return Long.toString(theLong);
            case OBJECT:
                return "{";
            case ENDOBJECT:
                return "}";
            case ARRAY:
                return "[";
            case ENDARRAY:
                return "]";
            case COLON:
                return "colon";
            case COMMA:
                return "comma";
            default:
                return "unknown symbol";
        }
    }
}
