package com.zero.common.utils.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by zhaozhong on 2016/9/13.
 */
public interface ExcelExporter {

    void export(OutputStream os) throws IOException;

    enum ExcelVersion {
        EXCEL_2007,EXCEL_2010,CSV
    }


}
