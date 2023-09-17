package org.GreenDude.OpenReportPlatform.Utils.CustomDataTypes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExcelField {

    private int cellId;
    private String cellName;
    private String fieldName;
    private Class<?> type;
    private Object value;
}
