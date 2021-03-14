package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Entity
@Data
public class Project {
    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;
    private String name;
    private String description;
    private double preferredBid;
    private Duration duration;
    private double minimumQualification;
    private boolean allowBidding;

    @ManyToOne( optional = false ) @NotNull
    private Stakeholder owner;
}
