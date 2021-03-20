package org.thekiddos.faith.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDto {
    private String name;
    private String description;
    private double preferredBid;
    private long duration;
    private double minimumQualification;
    private boolean allowBidding;
}
