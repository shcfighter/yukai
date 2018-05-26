package com.zero.common.utils.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhaozhong on 2017/1/20.
 */
public class SimpleExcelReader {

    private Workbook wb;
    private Sheet sh;

    private int sl = 1;

    public SimpleExcelReader(InputStream is) throws IOException, InvalidFormatException {
        this(is, 0, 1);
    }

    public SimpleExcelReader(InputStream is, int sh, int sl) throws IOException, InvalidFormatException {
        this.wb = WorkbookFactory.create(is);
        this.sl = sl;
        this.sh = this.wb.getSheetAt(sh);
    }

    public List<Object> head() {
        Row row = sh.getRow(sl - 1);
        List<Object> ra = new ArrayList(row.getLastCellNum());
        row.cellIterator().forEachRemaining(cell -> ra.add(cellVal(cell)));
        return ra;
    }

    public List<List<Object>> read() {
        final List<List<Object>> dd = new ArrayList<>(sh.getLastRowNum());
        Row row;
        for (int i = sl; (row = sh.getRow(i)) != null; i++) {
            List<Object> ra = new ArrayList(row.getLastCellNum());
            row.cellIterator().forEachRemaining(cell -> ra.add(cellVal(cell)));
            dd.add(ra);
        }
        return dd;
    }

    public List<Map<String, Object>> read(String[] dict) {
        final List<Map<String, Object>> dd = new ArrayList<>(sh.getLastRowNum());
        Row row;
        for (int i = sl; (row = sh.getRow(i)) != null; i++) {
            final Map<String, Object> lm = new TreeMap<>();
            Cell cell;
            for (int j = row.getFirstCellNum(); (cell = row.getCell(j)) != null && j - row.getFirstCellNum() <= dict.length; j++) {
                String k = dict[j - row.getFirstCellNum()];
                lm.put(k, cellVal(cell));
            }
            dd.add(lm);
        }
        return dd;
    }

    private Object cellVal(Cell cell) {
        Object val;
        try {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    val = Boolean.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    val = cell.getCellFormula();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    val = cell.getNumericCellValue();
                    break;
                case Cell.CELL_TYPE_ERROR:
                    val = new StringBuilder().append("Error_").append(cell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                default:
                    val = String.valueOf(cell.getStringCellValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            val = null;
        }
        return val;
    }


}
