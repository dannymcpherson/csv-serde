//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bizo.hive.serde.csv.opencsv_2_3_patched;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ResultSetHelperService implements ResultSetHelper {
    public static final int CLOBBUFFERSIZE = 2048;
    private static final int NVARCHAR = -9;
    private static final int NCHAR = -15;
    private static final int LONGNVARCHAR = -16;
    private static final int NCLOB = 2011;

    public ResultSetHelperService() {
    }

    public String[] getColumnNames(ResultSet rs) throws SQLException {
        ArrayList names = new ArrayList();
        ResultSetMetaData metadata = rs.getMetaData();

        for(int nameArray = 0; nameArray < metadata.getColumnCount(); ++nameArray) {
            names.add(metadata.getColumnName(nameArray + 1));
        }

        String[] var5 = new String[names.size()];
        return (String[])names.toArray(var5);
    }

    public String[] getColumnValues(ResultSet rs) throws SQLException, IOException {
        ArrayList values = new ArrayList();
        ResultSetMetaData metadata = rs.getMetaData();

        for(int valueArray = 0; valueArray < metadata.getColumnCount(); ++valueArray) {
            values.add(this.getColumnValue(rs, metadata.getColumnType(valueArray + 1), valueArray + 1));
        }

        String[] var5 = new String[values.size()];
        return (String[])values.toArray(var5);
    }

    private String handleObject(Object obj) {
        return obj == null?"":String.valueOf(obj);
    }

    private String handleBigDecimal(BigDecimal decimal) {
        return decimal == null?"":decimal.toString();
    }

    private String handleLong(ResultSet rs, int columnIndex) throws SQLException {
        long lv = rs.getLong(columnIndex);
        return rs.wasNull()?"":Long.toString(lv);
    }

    private String handleInteger(ResultSet rs, int columnIndex) throws SQLException {
        int i = rs.getInt(columnIndex);
        return rs.wasNull()?"":Integer.toString(i);
    }

    private String handleDate(ResultSet rs, int columnIndex) throws SQLException {
        Date date = rs.getDate(columnIndex);
        String value = null;
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            value = dateFormat.format(date);
        }

        return value;
    }

    private String handleTime(Time time) {
        return time == null?null:time.toString();
    }

    private String handleTimestamp(Timestamp timestamp) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        return timestamp == null?null:timeFormat.format(timestamp);
    }

    private String getColumnValue(ResultSet rs, int colType, int colIndex) throws SQLException, IOException {
        String value = "";
        switch(colType) {
            case -16:
            case -15:
            case -9:
            case -1:
            case 1:
            case 12:
                value = rs.getString(colIndex);
                break;
            case -7:
            case 2000:
                value = this.handleObject(rs.getObject(colIndex));
                break;
            case -6:
            case 4:
            case 5:
                value = this.handleInteger(rs, colIndex);
                break;
            case -5:
                value = this.handleLong(rs, colIndex);
                break;
            case 2:
            case 3:
            case 6:
            case 7:
            case 8:
                value = this.handleBigDecimal(rs.getBigDecimal(colIndex));
                break;
            case 16:
                boolean b = rs.getBoolean(colIndex);
                value = Boolean.valueOf(b).toString();
                break;
            case 91:
                value = this.handleDate(rs, colIndex);
                break;
            case 92:
                value = this.handleTime(rs.getTime(colIndex));
                break;
            case 93:
                value = this.handleTimestamp(rs.getTimestamp(colIndex));
                break;
            case 2005:
            case 2011:
                Clob c = rs.getClob(colIndex);
                if(c != null) {
                    value = read(c);
                }
                break;
            default:
                value = "";
        }

        if(value == null) {
            value = "";
        }

        return value;
    }

    private static String read(Clob c) throws SQLException, IOException {
        StringBuilder sb = new StringBuilder((int)c.length());
        Reader r = c.getCharacterStream();
        char[] cbuf = new char[2048];

        int n;
        while((n = r.read(cbuf, 0, cbuf.length)) != -1) {
            sb.append(cbuf, 0, n);
        }

        return sb.toString();
    }
}
