package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Bid {
    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;
    private double amount;

    @ManyToOne( optional = false ) @NotNull
    private Freelancer bidder;
    
    @ManyToOne( optional = false ) @NotNull
    private Project project;

    @Override
    public String toString() {
        return "Bid of " + amount;
    }
}
