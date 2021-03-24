package org.thekiddos.faith.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.repositories.SkillRepository;
import org.thekiddos.faith.services.SkillService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
class SkillTest {
    private final SkillRepository skillRepository;
    private final SkillService skillService;

    @Autowired
    SkillTest( SkillRepository skillRepository, SkillService skillService ) {
        this.skillRepository = skillRepository;
        this.skillService = skillService;
    }

    @BeforeEach
    void clearSkills() {
        skillRepository.deleteAll();
    }

    @Test
    void createSkillWithOf() {
        assertEquals( "suck", Skill.of( "suck" ).getName() );
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
    }

    @Test
    void createSkillsFromNullString() {
        assertTrue( Skill.createSkills( null ).isEmpty() );
    }

    @Test
    void createSkillsFromEmptyString() {
        assertTrue( Skill.createSkills( "" ).isEmpty() );
    }

    @Test
    void createSkill() {
        skillService.createSkill( "Suck" );
        var skill = skillRepository.findById( "Suck" ).orElse( null );
        assertNotNull( skill );
        assertEquals( "Suck", skill.getName() );
    }

    // TODO: skill that already exists
}
