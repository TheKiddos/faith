package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Objects;

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
    private boolean closed = false;

    @ManyToOne( optional = false ) @NotNull
    private Stakeholder owner;

    @Override
    public String toString() {
        return "Project(" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", preferredBid=" + preferredBid +
                ", duration=" + duration +
                ", minimumQualification=" + minimumQualification +
                ", allowBidding=" + allowBidding +
                ')';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Project project = (Project) o;
        return id.equals( project.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id );
    }
}
