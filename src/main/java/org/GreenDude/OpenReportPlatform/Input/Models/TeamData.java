package org.GreenDude.OpenReportPlatform.Input.Models;

import lombok.Builder;

@Builder
public class TeamData {

    private final String defaultString = "NULL";
    private final float defaultValue = 0f;
    private String program;
    private String team;
    private int rollingVelocity;
    private float nps;
    private boolean isSimpleNPS;
    private float planningConfidence;
}
