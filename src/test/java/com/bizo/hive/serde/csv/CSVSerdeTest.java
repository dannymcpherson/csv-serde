package com.bizo.hive.serde.csv;

import java.util.List;
import java.util.Properties;

import org.apache.hadoop.hive.serde.Constants;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public final class CSVSerdeTest {
  private final RmnCsvSerde csv = new RmnCsvSerde();
  final Properties props = new Properties();
  
  @Before
  public void setup() throws Exception {
    props.put(Constants.LIST_COLUMNS, "a,b,c");
    props.put(Constants.LIST_COLUMN_TYPES, "string,string,string");
  }
  
  @Test
  public void testDeserialize() throws Exception {
    csv.initialize(null, props);    
    final Text in = new Text("hello,\"yes, okay\",1");
    
    final List<String> row = (List<String>) csv.deserialize(in);

    assertEquals("hello", row.get(0));
    assertEquals("yes, okay", row.get(1));
    assertEquals("1", row.get(2));
  }
  
  
  @Test
  public void testDeserializeCustomSeparators() throws Exception {
    props.put("separatorChar", "\t");
    props.put("quoteChar", "'");
    
    csv.initialize(null, props);
    
    final Text in = new Text("hello\t'yes\tokay'\t1");
    final List<String> row = (List<String>) csv.deserialize(in);
        
    assertEquals("hello", row.get(0));
    assertEquals("yes\tokay", row.get(1));    
    assertEquals("1", row.get(2));
  }
  
  @Test
  public void testDeserializeCustomEscape() throws Exception {
    props.put("quoteChar", "'");
    props.put("escapeChar", "\\");
    
    csv.initialize(null, props);
    
    final Text in = new Text("hello,'yes\\'okay',1");
    final List<String> row = (List<String>) csv.deserialize(in);
        
    assertEquals("hello", row.get(0));
    assertEquals("yes'okay", row.get(1));
    assertEquals("1", row.get(2));
  }

  @Test
  public void testDeserializeNullValue() throws Exception {
    props.put("separatorChar", "\t");
    props.put("quoteChar", "'");
    props.put("nullString", "NULL");

    csv.initialize(null, props);

    final Text in = new Text("'v1'\tNULL\t'v3'");
    final List<String> row = (List<String>) csv.deserialize(in);

    assertEquals("v1", row.get(0));
    assertNull(row.get(1));
    assertEquals("v3", row.get(2));
  }

  @Test
  public void testDeserializeNullValueLast() throws Exception {
    props.put("separatorChar", "\t");
    props.put("quoteChar", "'");
    props.put("nullString", "NULL");

    csv.initialize(null, props);

    final Text in = new Text("'v1'\t'v2'\tNULL");
    final List<String> row = (List<String>) csv.deserialize(in);

    assertEquals("v1", row.get(0));
    assertEquals("v2", row.get(1));
    assertNull(row.get(2));
  }

  @Test
  public void testDeserializeCustomValues() throws Exception {
    props.put("separatorChar", "\t");
    props.put("quoteChar", "'");
    props.put("nullString", "NULL");

    csv.initialize(null, props);

    final Text in = new Text("'NULL'\tNULL\tNULL");
    final List<String> row = (List<String>) csv.deserialize(in);

    assertEquals("NULL", row.get(0));
    assertNull(row.get(1));
    assertNull(row.get(2));
  }

  @Test
  public void testDeserializeCustomNullString() throws Exception {
    props.put("separatorChar", "\t");
    props.put("quoteChar", "'");
    props.put("nullString", "'\\N'");

    csv.initialize(null, props);

    final Text in = new Text("'v1'\t'\\N'\t'\\N'");
    final List<String> row = (List<String>) csv.deserialize(in);

    assertEquals("v1", row.get(0));
    assertNull(row.get(1));
    assertNull(row.get(2));
  }

  @Test
  public void testSerializeNullValueDefault() throws Exception {
    props.put("separatorChar", "\t");
    props.put("quoteChar", "'");
    props.put("nullString", "NULL");

    csv.initialize(null, props);

    final String[] out = new String[]{"v1", null, "v3"};
    Writable writable = csv.serialize(out, csv.getObjectInspector());

    assertEquals(new Text("'v1'\tNULL\t'v3'"), writable);
  }

  @Test
  public void testSerializeNullValue() throws Exception {
    props.put("separatorChar", "\t");
    props.put("quoteChar", "'");

    csv.initialize(null, props);

    final String[] out = new String[]{"v1", null, "v3"};
    Writable writable = csv.serialize(out, csv.getObjectInspector());

    assertEquals(new Text("'v1'\t\t'v3'"), writable);
  }

  @Test
  public void testSerializeEmptyString() throws Exception {
    props.put("separatorChar", "\t");
    props.put("quoteChar", "'");

    csv.initialize(null, props);

    final String[] out = new String[]{"v1", "", "v3"};
    Writable writable = csv.serialize(out, csv.getObjectInspector());

    assertEquals(new Text("'v1'\t''\t'v3'"), writable);
  }
}
