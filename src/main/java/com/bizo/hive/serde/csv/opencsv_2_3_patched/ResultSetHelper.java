//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bizo.hive.serde.csv.opencsv_2_3_patched;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHelper {
    String[] getColumnNames(ResultSet var1) throws SQLException;

    String[] getColumnValues(ResultSet var1) throws SQLException, IOException;
}
