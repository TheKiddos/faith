package org.thekiddos.faith.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerDto {
    private Long id;
    @NotNull
    private String summary;
    private boolean available;
    @NotNull
    private String skills;
    private UserDto user;
    private double projectBidAmount;
    private double projectProposalAmount;
    private double rating;
}
