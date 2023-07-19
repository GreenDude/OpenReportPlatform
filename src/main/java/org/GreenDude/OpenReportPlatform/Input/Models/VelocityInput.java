package org.GreenDude.OpenReportPlatform.Input.Models;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
public class VelocityInput {

    private String sowGroup;
    private String teamName;
    private int velocity;
    private String sprint;
}
