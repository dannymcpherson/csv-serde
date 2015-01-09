//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bizo.hive.serde.csv.opencsv_2_3_patched;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class has been copied/modified to provide better null value support.
 * Added variables nullString, sbRaw, pendingRaw, and related code.
 */
public class CSVParser {
    private final char separator;
    private final char quotechar;
    private final char escape;
    private final boolean strictQuotes;
    private final String nullString;
    private String pending;
    private String pendingRaw;
    private boolean inField;
    private final boolean ignoreLeadingWhiteSpace;
    public static final char DEFAULT_SEPARATOR = ',';
    public static final int INITIAL_READ_SIZE = 128;
    public static final char DEFAULT_QUOTE_CHARACTER = '\"';
    public static final char DEFAULT_ESCAPE_CHARACTER = '\\';
    public static final boolean DEFAULT_STRICT_QUOTES = false;
    public static final boolean DEFAULT_IGNORE_LEADING_WHITESPACE = true;
    public static final char NULL_CHARACTER = '\u0000';

    public CSVParser() {
        this(',', '\"', '\\');
    }

    public CSVParser(char separator) {
        this(separator, '\"', '\\');
    }

    public CSVParser(char separator, char quotechar) {
        this(separator, quotechar, '\\');
    }

    public CSVParser(char separator, char quotechar, char escape) {
        this(separator, quotechar, escape, false);
    }

    public CSVParser(char separator, char quotechar, char escape, boolean strictQuotes) {
        this(separator, quotechar, escape, strictQuotes, true, null);
    }

    public CSVParser(char separator, char quotechar, char escape, boolean strictQuotes, boolean ignoreLeadingWhiteSpace, String nullString) {
        this.inField = false;
        if(this.anyCharactersAreTheSame(separator, quotechar, escape)) {
            throw new UnsupportedOperationException("The separator, quote, and escape characters must be different!");
        } else if(separator == 0) {
            throw new UnsupportedOperationException("The separator character must be defined!");
        } else {
            this.separator = separator;
            this.quotechar = quotechar;
            this.escape = escape;
            this.strictQuotes = strictQuotes;
            this.ignoreLeadingWhiteSpace = ignoreLeadingWhiteSpace;
            this.nullString = nullString;
        }
    }

    private boolean anyCharactersAreTheSame(char separator, char quotechar, char escape) {
        return this.isSameCharacter(separator, quotechar) || this.isSameCharacter(separator, escape) || this.isSameCharacter(quotechar, escape);
    }

    private boolean isSameCharacter(char c1, char c2) {
        return c1 != 0 && c1 == c2;
    }

    public boolean isPending() {
        return this.pending != null;
    }

    public String[] parseLineMulti(String nextLine) throws IOException {
        return this.parseLine(nextLine, true);
    }

    public String[] parseLine(String nextLine) throws IOException {
        return this.parseLine(nextLine, false);
    }

    private String[] parseLine(String nextLine, boolean multi) throws IOException {
        if(!multi && this.pending != null) {
            this.pending = null;
            this.pendingRaw = null;
        }

        if(nextLine == null) {
            if(this.pending != null) {
                String var8 = this.pending;
                this.pending = null;
                this.pendingRaw = null;
                return nullStringEquals(this.pendingRaw) ? null : new String[]{var8};
            } else {
                return null;
            }
        } else {
            ArrayList tokensOnThisLine = new ArrayList();
            StringBuilder sb = new StringBuilder(128);
            StringBuilder sbRaw = new StringBuilder(128);
            boolean inQuotes = false;
            if(this.pending != null) {
                append(this.pending, sb, sbRaw);
                this.pending = null;
                this.pendingRaw = null;
                inQuotes = true;
            }

            for(int i = 0; i < nextLine.length(); ++i) {
                char c = nextLine.charAt(i);
                if(c == this.escape) {
                    sbRaw.append(this.escape);
                    if(this.isNextCharacterEscapable(nextLine, inQuotes || this.inField, i)) {
                        append(nextLine.charAt(i + 1), sb, sbRaw);
                        ++i;
                    }
                } else if(c == this.quotechar) {
                    sbRaw.append(this.quotechar);
                    if(this.isNextCharacterEscapedQuote(nextLine, inQuotes || this.inField, i)) {
                        append(nextLine.charAt(i + 1), sb, sbRaw);
                        ++i;
                    } else {
                        if(!this.strictQuotes && i > 2 && nextLine.charAt(i - 1) != this.separator && nextLine.length() > i + 1 && nextLine.charAt(i + 1) != this.separator) {
                            if(this.ignoreLeadingWhiteSpace && sb.length() > 0 && this.isAllWhiteSpace(sb)) {
                                setLengthZero(sb, sbRaw);
                            } else {
                                append(c, sb, sbRaw);
                            }
                        }

                        inQuotes = !inQuotes;
                    }

                    this.inField = !this.inField;
                } else if(c == this.separator && !inQuotes) {
                    tokensOnThisLine.add(nullStringEquals(sbRaw.toString()) ? null : sb.toString());
                    setLengthZero(sb, sbRaw);
                    this.inField = false;
                } else if(!this.strictQuotes || inQuotes) {
                    append(c, sb, sbRaw);
                    this.inField = true;
                }
            }

            if(inQuotes) {
                if(!multi) {
                    throw new IOException("Un-terminated quoted field at end of CSV line");
                }

                append("\n", sb, sbRaw);
                this.pending = sb.toString();
                this.pendingRaw = sbRaw.toString();
                sb = null;
                sbRaw = null;
            }

            if(sb != null) {
                tokensOnThisLine.add(nullStringEquals(sbRaw.toString()) ? null : sb.toString());
            }

            return (String[])tokensOnThisLine.toArray(new String[tokensOnThisLine.size()]);
        }
    }

    private boolean nullStringEquals(String value) {
        return this.nullString != null && this.nullString.equals(value);
    }

    private void setLengthZero(StringBuilder sb, StringBuilder sbRaw) {
        sb.setLength(0);
        sbRaw.setLength(0);
    }

    private void append(char value, StringBuilder sb, StringBuilder sbRaw) {
        append(String.valueOf(value), sb, sbRaw);
    }

    private void append(String value, StringBuilder sb, StringBuilder sbRaw) {
        sb.append(value);
        sbRaw.append(value);
    }

    private boolean isNextCharacterEscapedQuote(String nextLine, boolean inQuotes, int i) {
        return inQuotes && nextLine.length() > i + 1 && nextLine.charAt(i + 1) == this.quotechar;
    }

    protected boolean isNextCharacterEscapable(String nextLine, boolean inQuotes, int i) {
        return inQuotes && nextLine.length() > i + 1 && (nextLine.charAt(i + 1) == this.quotechar || nextLine.charAt(i + 1) == this.escape);
    }

    protected boolean isAllWhiteSpace(CharSequence sb) {
        boolean result = true;

        for(int i = 0; i < sb.length(); ++i) {
            char c = sb.charAt(i);
            if(!Character.isWhitespace(c)) {
                return false;
            }
        }

        return result;
    }
}
