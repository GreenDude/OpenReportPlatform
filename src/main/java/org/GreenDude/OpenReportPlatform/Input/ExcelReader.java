package org.GreenDude.OpenReportPlatform.Input;

import lombok.Getter;
import org.GreenDude.OpenReportPlatform.Input.Models.NetPromoterScore;
import org.GreenDude.OpenReportPlatform.Input.Models.PlanningConfidence;
import org.GreenDude.OpenReportPlatform.Input.Models.VelocityInput;
import org.GreenDude.OpenReportPlatform.Utils.ReflectionUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public class ExcelReader {

    private Map<String, Integer> listOfTeams = new HashMap<>();
    private List<VelocityInput> velocityInputs = new ArrayList<>();
    private Workbook workbook;
    String kpiTrackerTabName = "KPIs Tracker";
    String sow;

    ReflectionUtils reflectionUtils = new ReflectionUtils();

    public void setKpiTrackerTabName(String name) {
        this.kpiTrackerTabName = name;
    }

    public void setListOfTeams(List<String> listOfTeams) {
        listOfTeams.forEach(l -> this.listOfTeams.put(l, -1));
    }

    public void setSow(String sow) {
        this.sow = sow;
    }

    public void getWorkbook(String path) {
        try (FileInputStream file = new FileInputStream(new File(path))) {
            workbook = new XSSFWorkbook(file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<VelocityInput> extractVelocityForSprint(String sprint) {
        Sheet kpiSheet = workbook.getSheet(kpiTrackerTabName);
        int sprintFieldId = 0;

        try {
            for (Row row : kpiSheet) {

                //Get indexes of KPI Fields
                if (listOfTeams.containsValue(-1)) {
                    if (!row.getCell(sprintFieldId).getStringCellValue().isEmpty()) {
                        StreamSupport.stream(row.spliterator(), false).filter(c -> listOfTeams.containsKey(c.getStringCellValue())).forEach(c -> listOfTeams.replace(c.getStringCellValue(), c.getColumnIndex()));
                    }
                    continue;
                }

                if (row.getCell(sprintFieldId).getStringCellValue().equalsIgnoreCase(sprint)) {
                    for (Map.Entry<String, Integer> e : listOfTeams.entrySet()) {
                        velocityInputs.add(VelocityInput.builder().teamName(e.getKey()).sowGroup(sow).sprint(sprint).velocity((int) row.getCell(e.getValue()).getNumericCellValue()).build());
                    }
                    return velocityInputs;
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Sprint not found in the KPI report", e);
        }
        return null;
    }

    public NetPromoterScore calculateNPS(String formName, String teamName, String scoreMarker, String complexityMarker) {
        int scoreId = -1;
        int complexityId = -1;
        boolean isComplex = false;
        double nps = 0;
        List<Double> scores = new ArrayList<>();
        Cell scoreCell;

        Sheet npsSheet = workbook.getSheet(formName);
        for (Row row : npsSheet) {
            //Extract score id
            if (scoreId < 0) {
                for (Cell cell : row) {
                    if (cell.getCellType().equals(CellType.STRING)) {
                        if (cell.getStringCellValue().compareToIgnoreCase(scoreMarker) == 0) {
                            scoreId = cell.getColumnIndex();
                        }
                        if (cell.getStringCellValue().compareToIgnoreCase(complexityMarker) == 0) {
                            complexityId = cell.getColumnIndex();
                        }
                    }
                }
            }
            else {
                //Check complexity
                if(complexityId > -1){
                    isComplex = true;
                }

                //Extract scores
                scoreCell = row.getCell(scoreId);
                if(scoreCell.getCellType().equals(CellType.NUMERIC) || scoreCell.getCellType().equals(CellType.FORMULA)){
                    scores.add(scoreCell.getNumericCellValue());
                }
            }
        }

        if(isComplex){
            long totalVotes = scores.size();
            double promoters = scores.stream().filter(x->x>=9).count();
            double detractors = scores.stream().filter(x->x<=6).count();
            nps = (double) (promoters/totalVotes*100 - detractors/totalVotes*100);

        } else {
            OptionalDouble averageNPS = scores.stream().mapToDouble(a -> a).average();
            nps = (averageNPS.isPresent() ? averageNPS.getAsDouble() : 0);
        }
        return NetPromoterScore.builder()
                .isComplexModel(isComplex)
                .teamName(teamName)
                .nps(nps)
                .build();
    }

    public PlanningConfidence extractPlanningConfidence(String teamName, String sprint, String sheetName, String scoreMarker){
        Cell scoreCell;
        int pcId = -1;
        Sheet pcSheet = workbook.getSheet(sheetName);
        List<Integer> pcScores = new ArrayList<>();
        for (Row row : pcSheet){
            if (pcId < 0) {
                for (Cell cell : row) {
                    if (cell.getCellType().equals(CellType.STRING)) {
                        if (cell.getStringCellValue().compareToIgnoreCase(scoreMarker) == 0) {
                            pcId = cell.getColumnIndex();
                        }
                    }
                }
            }
            else {
                //Extract scores
                scoreCell = row.getCell(pcId);
                if(scoreCell.getCellType().equals(CellType.NUMERIC) || scoreCell.getCellType().equals(CellType.FORMULA)){
                    pcScores.add((int) scoreCell.getNumericCellValue());
                }
            }
        }

        OptionalDouble averageConfidence = pcScores.stream().mapToDouble(a -> a).average();
        return new PlanningConfidence(teamName, sprint, averageConfidence.isPresent() ? averageConfidence.getAsDouble() : 0);
    }

    public Object genericDataExtractor(String modelName, Map<String, String> fieldsToBeExtracted){
        reflectionUtils.getObjectPackage(this);

        return null;
    }
}
