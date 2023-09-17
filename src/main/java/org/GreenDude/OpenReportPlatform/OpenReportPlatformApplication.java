package org.GreenDude.OpenReportPlatform;

import org.GreenDude.OpenReportPlatform.Input.ExcelReader;
import org.GreenDude.OpenReportPlatform.Input.Models.NetPromoterScore;
import org.GreenDude.OpenReportPlatform.Input.Models.VelocityInput;
import org.GreenDude.OpenReportPlatform.Utils.CustomDataTypes.ExcelField;
import org.GreenDude.OpenReportPlatform.Utils.ReflectionUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class OpenReportPlatformApplication {

	public static void main(String[] args) {
		ExcelReader excelReader = new ExcelReader();
		excelReader.setSow("SOW007");
//		excelReader.setListOfTeams(Arrays.asList("Team A", "Team B", "Team C"));

//		excelReader.getWorkbook("/Users/mosingheorghii/Documents/SOW007.xlsx");
//		excelReader.extractVelocityForSprint("Sprint 1").forEach(
//				v-> System.out.println(v.getTeamName() + " : " + v.getVelocity()));

		excelReader.getWorkbook("/Users/mosingheorghii/Documents/NPS Example.xlsx");
		System.out.println(excelReader
				.calculateNPS("Sheet1", "Team A", "Overall", "Using Complex model").toString());;

//		excelReader.getWorkbook("/Users/mosingheorghii/Documents/Planning Confidence.xlsx");
//		System.out.println(excelReader.extractPlanningConfidence("Team A", "Sprint 0", "Sheet1", "Planning Confidence").toString());
//
//		ReflectionUtils reflectionUtils = new ReflectionUtils();
//		String cn = "org.GreenDude.OpenReportPlatform.Input.Models.NetPromoterScore";
//
//		List<ExcelField> ef = new ArrayList<>();
//
//		ef.add(new ExcelField(1, "Team Name", "teamName", String.class, "Saturn"));
//		ef.add(new ExcelField(2, "NPS", "nps", double.class, 5d));
//		ef.add(new ExcelField(3, "Complexity Flag", "isComplexModel", boolean.class, false));
//		NetPromoterScore nf = (NetPromoterScore) reflectionUtils.generateObjectInstance(cn, ef);
//		int i = 0;

		excelReader.genericDataExtractor("", new HashMap<>() );
	}

}
