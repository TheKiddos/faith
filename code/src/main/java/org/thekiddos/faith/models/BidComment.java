package org.thekiddos.faith.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Setter
public class BidComment {
    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;

    @NotNull @NotEmpty
    private String text;

    @ManyToOne( optional = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE } ) @NotNull
    private Bid bid;

    @ManyToOne( optional = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE } ) @NotNull
    private User user;

    @Override
    public String toString() {
        return "BidComment(" +
                "text='" + text + '\'' +
                ')';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        BidComment that = (BidComment) o;
        return text.equals( that.text );
    }

    @Override
    public int hashCode() {
        return Objects.hash( text );
    }
}
