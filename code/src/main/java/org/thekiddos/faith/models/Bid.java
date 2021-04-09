package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
@Entity
public class Bid {
    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;
    private double amount;

    @ManyToOne( optional = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE } ) @NotNull
    private Freelancer bidder;
    
    @ManyToOne( optional = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE } ) @NotNull
    private Project project;

    @Override
    public String toString() {
        return "Bid of " + amount;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Bid bid = (Bid) o;
        return Double.compare( bid.amount, amount ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash( amount );
    }
}
