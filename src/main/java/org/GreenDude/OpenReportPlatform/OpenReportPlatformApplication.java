package org.GreenDude.OpenReportPlatform;

import org.GreenDude.OpenReportPlatform.Input.ExcelReader;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class OpenReportPlatformApplication {

	public static void main(String[] args) {
		ExcelReader excelReader = new ExcelReader();
		excelReader.setListOfTeams(Arrays.asList("Team A", "Team B", "Team C"));
		excelReader.getWorkbook("/Users/mosingheorghii/Documents/SOW007.xlsx");
		excelReader.extractVelocityForSprint("Sprint 1", "Team B");
//		SpringApplication.run(OpenReportPlatformApplication.class, args);
	}

}
