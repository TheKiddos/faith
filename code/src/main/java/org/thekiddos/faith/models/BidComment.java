package org.thekiddos.faith.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class BidComment {
    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;

    private String text;

    @ManyToOne( optional = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE } ) @NotNull
    private Bid bid;
}
