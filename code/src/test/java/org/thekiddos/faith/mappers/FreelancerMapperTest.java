package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.models.*;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.repositories.ProposalRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class FreelancerMapperTest {
    private final FreelancerMapper freelancerMapper;

    // TODO: Implement with service instead of repo
    @MockBean
    private BidRepository bidRepository;
    @MockBean
    private ProposalRepository proposalRepository;

    @Autowired
    public FreelancerMapperTest( FreelancerMapper freelancerMapper ) {
        this.freelancerMapper = freelancerMapper;
    }

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

        User user = new User();
        user.setEmail( "hello@gmail.com" );
        freelancer.setUser( user );

        FreelancerDto dto = freelancerMapper.toDto( freelancer );
        assertEquals( freelancer.getSummary(), dto.getSummary() );
        assertEquals( freelancer.isAvailable(), dto.isAvailable() );
        assertEquals( freelancer.getSkills(), Skill.createSkills( dto.getSkills() ) );
        assertEquals( freelancer.getUser().getEmail(), dto.getUser().getEmail() );
    }

    @Test
    void nullFreelancerTest() {
        assertNull( freelancerMapper.toDto( null ) );
    }

    @Test
    void toDtoWithProject() {
        Freelancer freelancer = new Freelancer();
        freelancer.setSummary( "Hehhehe" );
        freelancer.setAvailable( true );
        freelancer.setSkills( Set.of( Skill.of( "c++" ), Skill.of( "suck" ) ) );

        User user = new User();
        user.setEmail( "hello@gmail.com" );
        freelancer.setUser( user );

        Project project = new Project();
        project.setId( 1L );

        Bid bid = new Bid();
        bid.setAmount( 200 );
        
        Proposal proposal = new Proposal();
        proposal.setAmount( 20 );


        Mockito.doReturn( Optional.of( bid ) ).when( bidRepository ).findByBidderAndProject( freelancer, project );
        Mockito.doReturn( Optional.of( proposal ) ).when( proposalRepository ).findByProjectAndFreelancer( project, freelancer );

        FreelancerDto dto = freelancerMapper.toDtoWithProject( freelancer, project );
        assertEquals( freelancer.getSummary(), dto.getSummary() );
        assertEquals( freelancer.isAvailable(), dto.isAvailable() );
        assertEquals( freelancer.getSkills(), Skill.createSkills( dto.getSkills() ) );
        assertEquals( freelancer.getUser().getEmail(), dto.getUser().getEmail() );
        assertEquals( bid.getAmount(), dto.getProjectBidAmount() );
        assertEquals( proposal.getAmount(), proposal.getProjectProposalAmount() );

        Mockito.verify( bidRepository, Mockito.times( 1 ) ).findByBidderAndProject( freelancer, project );
    }

    @Test
    void toDtoWithProjectNull() {
        Freelancer freelancer = new Freelancer();
        freelancer.setSummary( "Hehhehe" );
        freelancer.setAvailable( true );
        freelancer.setSkills( Set.of( Skill.of( "c++" ), Skill.of( "suck" ) ) );

        User user = new User();
        user.setEmail( "hello@gmail.com" );
        freelancer.setUser( user );

        FreelancerDto dto = freelancerMapper.toDtoWithProject( freelancer, null );
        assertEquals( freelancer.getSummary(), dto.getSummary() );
        assertEquals( freelancer.isAvailable(), dto.isAvailable() );
        assertEquals( freelancer.getSkills(), Skill.createSkills( dto.getSkills() ) );
        assertEquals( freelancer.getUser().getEmail(), dto.getUser().getEmail() );
        assertEquals( 0, dto.getProjectBidAmount() );

        Mockito.verify( bidRepository, Mockito.times( 0 ) ).findByBidderAndProject( freelancer, null );
    }
}
