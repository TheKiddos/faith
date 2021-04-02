package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.Test;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Skill;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FreelancerMapperTest {
    private final FreelancerMapper freelancerMapper = FreelancerMapper.INSTANCE;
    
    @Test
    void nullTest() {
        assertNull( freelancerMapper.toEntity( null ) );
    }
    
    @Test
    void toEntityTest() {
        FreelancerDto dto = FreelancerDto.builder()
                            .summary( "Hehhehe" )
                            .available( true )
                            .skills( "c++\nsuck" )
                            .build();
        
        Freelancer freelancer = freelancerMapper.toEntity( dto );
        assertEquals( dto.getSummary(), freelancer.getSummary() );
        assertEquals( dto.isAvailable(), freelancer.isAvailable() );
        assertEquals( "c++\nsuck", dto.getSkills() );
        assertEquals( Set.of( Skill.of( "c++" ), Skill.of( "suck" ) ), freelancer.getSkills() );
    }

    @Test
    void toDtoTest() {
        Freelancer freelancer = new Freelancer();
        freelancer.setSummary( "Hehhehe" );
        freelancer.setAvailable( true );
        freelancer.setSkills( Set.of( Skill.of( "c++" ), Skill.of( "suck" ) ) );

        FreelancerDto dto = freelancerMapper.toDto( freelancer );
        assertEquals( freelancer.getSummary(), dto.getSummary() );
        assertEquals( freelancer.isAvailable(), dto.isAvailable() );
        assertEquals( freelancer.getSkills(), Skill.createSkills( dto.getSkills() ) );
    }

    @Test
    void nullFreelancerTest() {
        assertNull( freelancerMapper.toDto( null ) );
    }
}