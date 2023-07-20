package org.GreenDude.OpenReportPlatform.Input;

import lombok.Getter;
import org.GreenDude.OpenReportPlatform.Input.Models.VelocityInput;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public class ExcelReader {

    private Map<String, Integer> listOfTeams = new HashMap<>();
    private List<VelocityInput> velocityInputs = new ArrayList<>();
    private Workbook workbook;
    String kpiTrackerTabName = "KPIs Tracker";
    String sow;

    public void setKpiTrackerTabName(String name) {
        this.kpiTrackerTabName = name;
    }

    public void setListOfTeams(List<String> listOfTeams){
        listOfTeams.forEach(l->this.listOfTeams.put(l, -1));
    }

    public Workbook getWorkbook(String path, String sow) {
        this.sow = sow;
        try (FileInputStream file = new FileInputStream(new File(path.concat(sow).concat(".xlsx")))) {
            workbook = new XSSFWorkbook(file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return workbook;
    }

    public List<VelocityInput> extractVelocityForSprint(String sprint) {
        int velocity = 0;
        Sheet kpiSheet = workbook.getSheet(kpiTrackerTabName);
        int sprintFieldId = 0;

        try {
            for (Row row : kpiSheet) {

                //Get indexes of KPI Fields
                if (listOfTeams.containsValue(-1)) {
                    if (!row.getCell(sprintFieldId).getStringCellValue().isEmpty()) {
                        StreamSupport.stream(row.spliterator(), false)
                                .filter(c -> listOfTeams.containsKey(c.getStringCellValue()))
                                .forEach(c -> listOfTeams.replace(c.getStringCellValue(), c.getColumnIndex()));
                    }
                    continue;
                }

                if (row.getCell(sprintFieldId).getStringCellValue().equalsIgnoreCase(sprint)){
                    for(Map.Entry<String, Integer> e : listOfTeams.entrySet()){
                        velocityInputs.add(VelocityInput.builder()
                                .teamName(e.getKey())
                                .sowGroup(sow)
                                .sprint(sprint)
                                .velocity((int) row.getCell(e.getValue()).getNumericCellValue())
                                .build());
                    }
                    return velocityInputs;
                }
            }
        } catch (RuntimeException e){
            throw new RuntimeException("Sprint not found in the KPI report", e);
        }
        return null;
    }
}
