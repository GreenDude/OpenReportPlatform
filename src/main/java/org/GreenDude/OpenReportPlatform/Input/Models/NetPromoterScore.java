package org.GreenDude.OpenReportPlatform.Input.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetPromoterScore {

    private String teamName;
    private double nps;
    private boolean isComplexModel = false;
}
