package org.thekiddos.faith.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidDto {
    private Long id;
    @NotNull @Min(10)
    private double amount;
    private String comment; // TODO: change name to add comment?
    @NotNull
    private long projectId;
    @Builder.Default
    private List<BidCommentDto> bidComments = new ArrayList<>();
    private FreelancerDto bidder;
}
