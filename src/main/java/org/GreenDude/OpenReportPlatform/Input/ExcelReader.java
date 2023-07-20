package org.GreenDude.OpenReportPlatform.Input;

import lombok.Getter;
import org.GreenDude.OpenReportPlatform.Input.Models.NetPromoterScore;
import org.GreenDude.OpenReportPlatform.Input.Models.VelocityInput;
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

    public void setKpiTrackerTabName(String name) {
        this.kpiTrackerTabName = name;
    }

    public void setListOfTeams(List<String> listOfTeams) {
        listOfTeams.forEach(l -> this.listOfTeams.put(l, -1));
    }

    public void setSow(String sow) {
        this.sow = sow;
    }

    public Workbook getWorkbook(String path) {
        try (FileInputStream file = new FileInputStream(new File(path))) {
            workbook = new XSSFWorkbook(file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
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

    public NetPromoterScore extractNPS(String formName, String teamName, String npsMarker) {
        double nps = 0f;
        boolean isComplexModel = false;
        Sheet npsSheet = workbook.getSheet(formName);
        for (Row row : npsSheet) {
            for (Cell cell : row) {
                if (cell.getCellType().equals(CellType.STRING)) {
                    if (cell.getStringCellValue().equalsIgnoreCase(npsMarker)) {
                        nps = row.getCell(cell.getColumnIndex() + 1).getNumericCellValue();
                        Cell complexModelIndicatorCell = row.getCell(0);
                        if (complexModelIndicatorCell.getCellType().equals(CellType.STRING)) {
                            isComplexModel = (complexModelIndicatorCell.getStringCellValue().compareToIgnoreCase("Complex") == 0);
                        }
                        break;
                    }
                }
            }
        }

        return NetPromoterScore.builder()
                .isComplexModel(isComplexModel)
                .teamName(teamName)
                .nps(nps)
                .build();
    }

    public NetPromoterScore calculateNPS(String formName, String teamName, String scoreMarker, String complexityMarker) {
        int scoreId = -1;
        int complexityId = -1;
        boolean isComplex = false;
        double nps = 0;
        List<Double> scores = new ArrayList<>();
        Cell scoreCell;
        Cell complexityCell;
        boolean complexityChecked = false;

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
            nps = (averageNPS.isPresent() ? averageNPS.getAsDouble() : 0) * 10;
        }
        return NetPromoterScore.builder()
                .isComplexModel(isComplex)
                .teamName(teamName)
                .nps(nps)
                .build();
    }
}
