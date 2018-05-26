package com.zero.common.utils.excel;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.zero.common.utils.DateUtils;
import com.zero.common.utils.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.zero.common.utils.excel.ExportConfig.ColumnName;

public abstract class AbstractExporter<T> implements ExcelExporter {

	protected List<T> dataList;
	protected Class<T> classType;
	protected List<Field> exportFieldList;
	
	@Override
	public void export(OutputStream os) throws IOException {
		// TODO Auto-generated method stub

	}
	
	protected abstract void validExportData();
	
	protected List<Field> asyncAnnotationField() {
        return Arrays.stream(classType.getDeclaredFields())
                .filter(field -> null != field.getAnnotation(ExportConfig.ColumnName.class))
                .sorted((f1, f2) -> f1.getAnnotation(ExportConfig.ColumnName.class).order() > f2.getAnnotation(ExportConfig.ColumnName.class).order() ? 1 : -1)
                .collect(Collectors.toList());
    }

	protected Object convertData(Field field, Class<T> classType, T obj){
		try {
			PropertyDescriptor propDesc = new PropertyDescriptor(field.getName(), classType);
			Method readMethod = propDesc.getReadMethod();
			if (null != readMethod) {
				return this.setValue(readMethod.invoke(obj, null), field.getAnnotation(ColumnName.class));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	};
	
	protected Object setValue(Object val, ExportConfig.ColumnName columnDef) {
		Object obj = null;
		final String defVal = Objects.nonNull(columnDef) ? columnDef.defaultValue() : "";
		final String codeName = Objects.nonNull(columnDef) ? columnDef.codeName() : null;
		final String dateFormat = Objects.nonNull(columnDef) ? columnDef.dateFormat() : DateUtils.DATEFORMAT;

		if (Objects.isNull(val)) {
			obj = defVal;
		}
		if (null == val && null != columnDef) {
			obj = columnDef.defaultValue();
		} else if (val instanceof Boolean) {
			obj = (Boolean) val;
		} else if (val instanceof Number) {
			try {
				obj = Integer.valueOf(String.valueOf(val));
			} catch (NumberFormatException e) {
				// ignore , not int convert to double
				obj = Double.valueOf(String.valueOf(val));
			}
		} else if (val instanceof Date) {
			obj = DateUtils.getStringFormat((Date) val, dateFormat);
		} else if (val instanceof Calendar) {
			obj = (Calendar) val;
		} else if (val instanceof CharSequence) {
			obj = String.valueOf(val);
		} else {
			obj = String.valueOf(val);
		}
		if (StringUtils.isNotEmpty(codeName)) {
			Integer intCode = Integer.parseInt(val.toString());
			//String dictVal = ReportDataResolver.getSysCodeSerivce().findLabelByNameAndValue(codeName, intCode);
			String dictVal = EnumUtils.getValue(codeName, intCode);
			obj = dictVal;
		}

		return obj;
	}
}
