package org.thekiddos.faith.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long id;
    @NotNull @NotEmpty
    private String name;
    @NotNull
    private String description;
    @NotNull
    private double preferredBid;
    @NotNull
    private long duration;
    @NotNull
    private double minimumQualification;
    @NotNull
    private boolean allowBidding;
}
