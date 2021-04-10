package org.thekiddos.faith.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidDto {
    @NotNull @Min(10)
    private double amount;
    private String comment;
    @NotNull
    private long projectId;
}
