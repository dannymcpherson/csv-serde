//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bizo.hive.serde.csv.opencsv_2_3_patched;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader implements Closeable {
    private BufferedReader br;
    private boolean hasNext;
    private CSVParser parser;
    private int skipLines;
    private boolean linesSkiped;
    public static final int DEFAULT_SKIP_LINES = 0;

    public CSVReader(Reader reader) {
        this(reader, ',', '\"', (char)'\\');
    }

    public CSVReader(Reader reader, char separator) {
        this(reader, separator, '\"', (char)'\\');
    }

    public CSVReader(Reader reader, char separator, char quotechar, String nullString) {
        this(reader, separator, quotechar, '\\', 0, false, nullString);
    }

    public CSVReader(Reader reader, char separator, char quotechar, boolean strictQuotes) {
        this(reader, separator, quotechar, '\\', 0, strictQuotes, null);
    }

    public CSVReader(Reader reader, char separator, char quotechar, char escape, String nullString) {
        this(reader, separator, quotechar, escape, 0, false, nullString);
    }

    public CSVReader(Reader reader, char separator, char quotechar, int line) {
        this(reader, separator, quotechar, '\\', line, false, null);
    }

    public CSVReader(Reader reader, char separator, char quotechar, char escape, int line) {
        this(reader, separator, quotechar, escape, line, false, null);
    }

    public CSVReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes, String nullString) {
        this(reader, separator, quotechar, escape, line, strictQuotes, true, nullString);
    }

    public CSVReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes, boolean ignoreLeadingWhiteSpace, String nullString) {
        this.hasNext = true;
        this.br = new BufferedReader(reader);
        this.parser = new CSVParser(separator, quotechar, escape, strictQuotes, ignoreLeadingWhiteSpace, nullString);
        this.skipLines = line;
    }

    public List<String[]> readAll() throws IOException {
        ArrayList allElements = new ArrayList();

        while(this.hasNext) {
            String[] nextLineAsTokens = this.readNext();
            if(nextLineAsTokens != null) {
                allElements.add(nextLineAsTokens);
            }
        }

        return allElements;
    }

    public String[] readNext() throws IOException {
        String[] result = null;

        do {
            String nextLine = this.getNextLine();
            if(!this.hasNext) {
                return result;
            }

            String[] r = this.parser.parseLineMulti(nextLine);
            if(r.length > 0) {
                if(result == null) {
                    result = r;
                } else {
                    String[] t = new String[result.length + r.length];
                    System.arraycopy(result, 0, t, 0, result.length);
                    System.arraycopy(r, 0, t, result.length, r.length);
                    result = t;
                }
            }
        } while(this.parser.isPending());

        return result;
    }

    private String getNextLine() throws IOException {
        if(!this.linesSkiped) {
            for(int nextLine = 0; nextLine < this.skipLines; ++nextLine) {
                this.br.readLine();
            }

            this.linesSkiped = true;
        }

        String var2 = this.br.readLine();
        if(var2 == null) {
            this.hasNext = false;
        }

        return this.hasNext?var2:null;
    }

    public void close() throws IOException {
        this.br.close();
    }
}
