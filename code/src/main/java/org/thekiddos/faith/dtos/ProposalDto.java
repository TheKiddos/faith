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
public class ProposalDto {
    private Long id;
    @NotNull @Min(1)
    private double amount;
    @NotNull
    private long projectId;
    @NotNull
    private long freelancerId;
}
