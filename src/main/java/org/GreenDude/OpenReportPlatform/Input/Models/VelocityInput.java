package org.GreenDude.OpenReportPlatform.Input.Models;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class VelocityInput {

    private String sowGroup;
    private String teamName;
    private int velocity;
    private String sprint;
}
