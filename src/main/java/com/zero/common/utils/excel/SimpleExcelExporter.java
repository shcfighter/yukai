package com.zero.common.utils.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CDATASection;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhaozhong on 2016/9/14.
 */
public class SimpleExcelExporter<T> extends AbstractExcelExporter {

    private List<T> dataList;
    private Class<T> classType;
    private ExcelVersion excelVersion;

    private ExportConfig.SheetName sheetName;
    private List<Field> exportFieldList;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleExcelExporter.class);

    public SimpleExcelExporter(List<T> dataList, Class<T> classType) {
        this(dataList, classType, null);
    }

    public SimpleExcelExporter(List<T> dataList, Class<T> classType, ExcelVersion excelVersion) {
        if (null == dataList)
            throw new NullPointerException("dataList is null");
        if (null == classType)
            throw new NullPointerException("classType is null");

        this.dataList = dataList;
        this.classType = classType;
        this.excelVersion = null == excelVersion ? ExcelVersion.EXCEL_2010 : excelVersion;

        this.sheetName = classType.getAnnotation(ExportConfig.SheetName.class);
        this.exportFieldList = asyncAnnotationField();
    }


    @Override
    protected Sheet createSheet(Workbook workbook) {
        return workbook.createSheet(sheetName.value());
    }

    protected void renderSheetHeader(Sheet sheet) {
        Row row = sheet.createRow(0);
        for (int c = 0; c < exportFieldList.size(); c++) {
            Cell cell = row.createCell(c);
            cell.setCellValue(exportFieldList.get(c).getAnnotation(ExportConfig.ColumnName.class).value());
        }
    }

    protected void renderSheetData(Sheet sheet) {
        for (int r = 0; r < dataList.size(); r++) {
            Row row = sheet.createRow(r + 1);
            for (int c = 0; c < exportFieldList.size(); c++) {
                Cell cell = row.createCell(c);
                Object data = dataList.get(r);
                setCellValue(cell, data, exportFieldList.get(c));
            }
        }
    }

    private String setCellValue(Cell cell, Object obj, Field field) {
        String value = "";
        try {
            PropertyDescriptor propDesc = new PropertyDescriptor(field.getName(), classType);
            Method readMethod = propDesc.getReadMethod();
            if (null != readMethod) {
                Object rVal = readMethod.invoke(obj, null);
                super.setCellValue(cell, rVal, field.getAnnotation(ExportConfig.ColumnName.class));
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            LOGGER.debug("set cell error", e);
        }
        return value;
    }

    @Override
    protected void validExportData() {
        if (null == classType.getAnnotation(ExportConfig.SheetName.class)) {
            throw new IllegalArgumentException("Can't found annotation ExportConfig.SheetName");
        }
    }

    private List<Field> asyncAnnotationField() {
        return Arrays.stream(classType.getDeclaredFields())
                .filter(field -> null != field.getAnnotation(ExportConfig.ColumnName.class))
                .sorted((f1, f2) -> f1.getAnnotation(ExportConfig.ColumnName.class).order() > f2.getAnnotation(ExportConfig.ColumnName.class).order() ? 1 : -1)
                .collect(Collectors.toList());
    }

}