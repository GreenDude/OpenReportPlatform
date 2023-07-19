package org.GreenDude.OpenReportPlatform.Input;

import lombok.Getter;
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
    private Workbook workbook;
    String kpiTrackerTabName = "KPIs Tracker";

    public void setKpiTrackerTabName(String name){
        this.kpiTrackerTabName = name;
    }

    public void setListOfTeams(List<String> listOfTeams){
        listOfTeams.forEach(l->this.listOfTeams.put(l, -1));
    }

    public Workbook getWorkbook(String path){
        try (FileInputStream file = new FileInputStream(new File(path))) {
            workbook = new XSSFWorkbook(file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return workbook;
    }

    public int extractVelocityForSprint(String sprint, String teamName){
        int velocity = 0;
        Sheet kpiSheet = workbook.getSheet(kpiTrackerTabName);
        List<String> indexationHelper = new ArrayList<>();
        int sprintFieldId = 0;
        int velocityFieldId = -1;

        for(Row row : kpiSheet){
            if(listOfTeams.containsValue(-1)){
                if(!row.getCell(0).getStringCellValue().isEmpty()) {
                    StreamSupport.stream(row.spliterator(), false)
                            .filter(c -> listOfTeams.containsKey(c.getStringCellValue()))
                            .forEach(c -> listOfTeams.replace(c.getStringCellValue(), c.getColumnIndex()));
                }
            }
        }

        return velocity;
    }

}
