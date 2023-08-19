package org.GreenDude.OpenReportPlatform;

import org.GreenDude.OpenReportPlatform.Input.ExcelReader;
import org.GreenDude.OpenReportPlatform.Input.Models.VelocityInput;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class OpenReportPlatformApplication {

	public static void main(String[] args) {
		ExcelReader excelReader = new ExcelReader();
		excelReader.setSow("SOW007");
		excelReader.setListOfTeams(Arrays.asList("Team A", "Team B", "Team C"));

		excelReader.getWorkbook("/Users/mosingheorghii/Documents/SOW007.xlsx");
		excelReader.extractVelocityForSprint("Sprint 1").forEach(
				v-> System.out.println(v.getTeamName() + " : " + v.getVelocity()));

		excelReader.getWorkbook("/Users/mosingheorghii/Documents/NPS Example.xlsx");
		System.out.println(excelReader
				.calculateNPS("Sheet1", "Team A", "Overall", "Using Complex model").toString());;
	}

}
