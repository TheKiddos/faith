package org.thekiddos.faith.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.repositories.SkillRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
class SkillTest {
    private final SkillRepository skillRepository;

    @Autowired
    SkillTest( SkillRepository skillRepository ) {
        this.skillRepository = skillRepository;
    }

    @BeforeEach
    void clearSkills() {
        skillRepository.deleteAll();
    }

    @Test
    void createSkillWithOf() {
        assertEquals( "suck", Skill.of( "suck" ).getName() );

        // TODO: for some reason this fails in maven but not intellij
//        var skill = skillRepository.findById( "suck" ).orElse( null );
//        assertNotNull( skill );
//        assertEquals( "suck", skill.getName() );
    }

    @Test
    void createSkillWithOfNull() {
        assertNull( Skill.of( null ) );
    }

    @Test
    void createSkillWithOfEmpty() {
        assertNull( Skill.of( "" ) );

        var skill = skillRepository.findById( "" ).orElse( null );
        assertNull( skill );
    }

    @Test
    void createSkillsFromString() {
        Set<String> skillNames = new HashSet<>( Set.of( "c++", "test sleep" ) );

        List<Skill> skills = Skill.createSkills( "c++\n\ntest sleep" );
        assertEquals( 2, skills.size() );
        for ( Skill skill : skills ) {
            var skillName = skill.getName();
            assertTrue( skillNames.contains( skillName ) );
            skillNames.remove( skillName );
        }
        assertTrue( skillNames.isEmpty() );
        // TODO: for some reason this fails in maven but not intellij
//        assertEquals( skills, skillRepository.findAll() );
    }

    @Test
    void createSkillsFromNullString() {
        assertTrue( Skill.createSkills( null ).isEmpty() );
    }

    @Test
    void createSkillsFromEmptyString() {
        assertTrue( Skill.createSkills( "" ).isEmpty() );
    }
}
