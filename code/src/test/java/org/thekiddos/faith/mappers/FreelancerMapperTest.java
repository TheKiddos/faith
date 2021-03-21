package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.Test;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.models.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FreelancerMapperTest {
    private final FreelancerMapper freelancerMapper = FreelancerMapper.INSTANCE;
    
    @Test
    void nullTest() {
        aseertNull( freelancerMapper.toEntity( null ) );
    }
    
    @Test
    void toEntityTest() {
        FreelancerDto dto = FreelancerDto.builder
                            .summary( "Hehhehe" )
                            .available( true )
                            .skills( "c++\nsuck" )
                            .build();
        
        Freelancer freelancer = freelancerMapper.toEntity( dto );
        assertEquals( "Hehhehe", frelancer.getSummary() );
        assertTrue( freelancer.isAvailable() );
        assertEquals( Set.of( Skill.of( "c++" ), Skill.of( "suck" ) ), freelancer.getSkills() );
    }
}