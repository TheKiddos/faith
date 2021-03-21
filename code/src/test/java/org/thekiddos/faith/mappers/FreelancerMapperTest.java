package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.Test;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Skill;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals( "Hehhehe", freelancer.getSummary() );
        assertTrue( freelancer.isAvailable() );
        assertEquals( Set.of( Skill.of( "c++" ), Skill.of( "suck" ) ), freelancer.getSkills() );
    }
}