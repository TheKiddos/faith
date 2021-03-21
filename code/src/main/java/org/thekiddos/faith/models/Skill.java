package org.thekiddos.faith.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thekiddos.faith.repositories.SkillRepository;
import org.thekiddos.faith.utils.ContextManager;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

        var skillObject = new Skill( skill );
        return ContextManager.getBean( SkillRepository.class ).save( skillObject );
    }

    public static List<Skill> createSkills( String skills ) {
        List<Skill> createdSkills = new ArrayList<>();
        if ( skills == null )
            return createdSkills;

        var skillArray = skills.split( "\\R+" );

        for ( var skill : skillArray )
            if ( !skill.isEmpty() )
                createdSkills.add( Skill.of( skill ) );

        return createdSkills;
    }
}
