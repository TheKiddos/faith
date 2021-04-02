package org.thekiddos.faith.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill {
    @Id @NotNull @NotEmpty
    private String name;

    public static Skill of( String skill ) {
        if ( skill == null || skill.isEmpty() )
            return null;

        return new Skill( skill );
    }

    public static Set<Skill> createSkills( String skills ) {
        Set<Skill> createdSkills = new HashSet<>();
        if ( skills == null )
            return createdSkills;

        var skillArray = skills.split( "\\R+" );

        for ( var skill : skillArray )
            if ( !skill.isEmpty() )
                createdSkills.add( Skill.of( skill ) );

        return createdSkills;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Skill skill = (Skill) o;
        return name.equals( skill.name );
    }

    @Override
    public int hashCode() {
        return Objects.hash( name );
    }
}
