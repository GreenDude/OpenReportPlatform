package org.GreenDude.OpenReportPlatform.Input.Models;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class NetPromoterScore {

    private String teamName;
    private double nps;
    private boolean isComplexModel = false;
}
