package com.zero.common.utils.excel;

import com.zero.common.utils.DateUtils;
import com.zero.common.utils.EnumUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by zhaozhong on 2016/9/14.
 */
public abstract class AbstractExcelExporter implements ExcelExporter {

    protected ExcelVersion excelVersion = ExcelVersion.EXCEL_2010;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExcelExporter.class);

    private Workbook workbook;

    @Override
    public void export(OutputStream os) throws IOException {
        validExportData();
        try {
            renderWorkbook();
            workbook.write(os);
        } finally {
            if (null != workbook) {
                workbook.close();
            }
        }
    }

    protected abstract void validExportData();

    private Workbook renderWorkbook() throws IOException {
        this.workbook = createWorkBook();
        final Sheet sheet = createSheet(workbook);

        renderSheetHeader(sheet);
        renderSheetData(sheet);

        return workbook;
    }

    protected abstract void renderSheetHeader(Sheet sheet);

    protected abstract void renderSheetData(Sheet sheet);

    protected void setCellValue(Cell cell, Object val, ExportConfig.ColumnName columnDef) {
        final String defVal = Objects.nonNull(columnDef) ? columnDef.defaultValue() : "";
        final boolean useAutoType = Objects.nonNull(columnDef) ? columnDef.useAutoType() : true;
        final String codeName = Objects.nonNull(columnDef) ? columnDef.codeName() : null;
        final String dateFormat = Objects.nonNull(columnDef) ? columnDef.dateFormat() : DateUtils.DATEFORMAT;

        if (Objects.isNull(val)) {
            cell.setCellValue(defVal);
            return;
        }
        if (useAutoType) {
            if (null == val && null != columnDef) {
                cell.setCellValue(columnDef.defaultValue());
            } else if (val instanceof Boolean) {
                cell.setCellValue((Boolean) val);
            } else if (val instanceof Number) {
                try {
                    cell.setCellValue(Integer.valueOf(String.valueOf(val)));
                } catch (NumberFormatException e) {
                    // ignore , not int convert to double
                    cell.setCellValue(Double.valueOf(String.valueOf(val)));
                }
            } else if (val instanceof Date) {
                cell.setCellValue((Date) val);
                DataFormat dataFormat = workbook.createDataFormat();
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setDataFormat(dataFormat.getFormat(dateFormat));
                cell.setCellStyle(cellStyle);
            } else if (val instanceof Calendar) {
                cell.setCellValue((Calendar) val);
            } else if (val instanceof CharSequence) {
                cell.setCellValue(String.valueOf(val));
            } else {
                cell.setCellValue(String.valueOf(val));
            }
        } else {
            String strVal = toStringValue(val, columnDef);
            switch (columnDef.cellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    cell.setCellValue(BooleanUtils.toBoolean(strVal));
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (NumberUtils.isDigits(strVal)) {
                        cell.setCellValue(Double.valueOf(strVal));
                        break;
                    }
                case Cell.CELL_TYPE_STRING:
                    cell.setCellValue(strVal);
                    break;
                default:
                    cell.setCellValue(strVal);

            }
        }
        if (StringUtils.isNotEmpty(codeName) && ((Cell.CELL_TYPE_STRING == cell.getCellType() && NumberUtils.isDigits(cell.getStringCellValue())) || Cell.CELL_TYPE_NUMERIC == cell.getCellType())) {
            Integer intCode = Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? Double.valueOf(cell.getNumericCellValue()).intValue() : Integer.valueOf(cell.getStringCellValue());
            //String dictVal = ReportDataResolver.getSysCodeSerivce().findLabelByNameAndValue(codeName, intCode);
            String dictVal = EnumUtils.getValue(codeName, intCode);
            cell.setCellValue(dictVal);
        }
    }

    protected String toStringValue(Object val, ExportConfig.ColumnName columnDef) {
        String rVal;
        if (null != val) {
            if (val instanceof String) {
                rVal = (String) val;
            } else if (val instanceof Integer || val instanceof Long) {
                rVal = val.toString();
            } else if (val instanceof Double) {
                DecimalFormat format = new DecimalFormat("0.00");
                rVal = format.format(val);
            } else if (val instanceof BigDecimal) {
                rVal = ((BigDecimal) val).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            } else if (val instanceof Date) {
                rVal = DateUtils.getStringFormat((Date) val, DateUtils.DATEFORMAT);
            } else {
                rVal = String.valueOf(val);
            }
        } else {
            rVal = Objects.nonNull(null == columnDef) ? columnDef.defaultValue() : "";
        }
        return rVal;
    }

    private Workbook createWorkBook() {
        return ExcelVersion.EXCEL_2007.equals(excelVersion) ? new HSSFWorkbook() : new XSSFWorkbook();
    }

    protected Sheet createSheet(Workbook workbook) {
        return workbook.createSheet();
    }


}