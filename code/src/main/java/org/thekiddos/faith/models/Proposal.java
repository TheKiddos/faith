package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
@Entity
public class Proposal {
    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;
    @NotNull @Min(1)
    private double amount;
    @Enumerated( EnumType.STRING )
    private Status status = Status.NEW;

    @ManyToOne( optional = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE } ) @NotNull
    private Freelancer freelancer;
    
    @ManyToOne( optional = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE } ) @NotNull
    private Project project;

    @Override
    public String toString() {
        return "Proposal of " + amount;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Proposal proposal = (Proposal) o;
        return Double.compare( proposal.amount, amount ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash( amount );
    }
}
