package org.thekiddos.faith.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class ProjectDto {
    private String name;
    private String description;
    private double preferredBid;
    private Duration duration;
    private double minimumQualification;
    private boolean allowBidding;
}
