package com.zero.common.utils.excel;

import org.apache.poi.ss.format.CellFormatType;
import org.apache.poi.ss.formula.udf.DefaultUDFFinder;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.annotation.*;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by zhaozhong on 2016/9/14.
 */
public abstract class ExportConfig {

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface ColumnName {
        int order();

        String value();

        String dateFormat() default "yyyy-MM-dd";

        String defaultValue() default "";

        String codeName() default "";

        int cellType() default Cell.CELL_TYPE_STRING;

        boolean useAutoType() default true;
    }


    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface SheetName {
        String value() default "Sheet1";
    }
    
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Csv {
    	
    }

    public static abstract class ExtendColumn {

        private Map<String, Object> column;

        public ExtendColumn() {
            this.column = new TreeMap<>((k1, k2) -> k1.compareTo(k2));
        }

        public ExtendColumn(Map<String, Object> column) {
            this.column = column;
        }

        public void addColumnData(String name, Object value) {
            column.put(name, value);
        }

        public void addColumnDataIfNotExist(String name, Object value) {
            column.putIfAbsent(name, value);
        }

        public Map<String, Object> getColumnData() {
            return column;
        }

        public boolean hasColumn(String columnName) {
            return column.containsKey(columnName);
        }

        @Override
        public String toString() {
            return "ExtendColumn{" +
                    "column=" + column +
                    '}';
        }

        protected void beforeUsing() {

        }
    }


}
