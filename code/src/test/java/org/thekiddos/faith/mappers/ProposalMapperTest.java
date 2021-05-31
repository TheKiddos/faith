package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.*;
import org.thekiddos.faith.models.*;
import org.thekiddos.faith.mappers.*;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class ProposalMapperTest {
    private final ProposalMapper proposalMapper;
    private final FreelancerService freelancerService;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectService projectService;

    private Project project;
    private Freelancer freelancer;

    @Autowired
    public ProposalMapperTest( ProposalMapper proposalMapper, FreelancerService freelancerService, UserRepository userRepository, ProjectRepository projectRepository, UserService userService, ProjectService projectService ) {
        this.proposalMapper = proposalMapper;
        this.freelancerService = freelancerService;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectService = projectService;
    }

    @BeforeEach
    public void setUp() {
        projectRepository.deleteAll();
        userRepository.deleteAll();

        ProjectDto projectDto = ProjectDto.builder()
                .name( "new world order" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();

        User user = userService.createUser( UserDto.builder()
                .email( "bhbh@gmail.com" )
                .password( "password" )
                .nickname( "bhbhbh" )
                .type( "Stakeholder" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        Stakeholder stakeholder = (Stakeholder) user.getType();

        this.project = projectService.createProjectFor( stakeholder, projectDto );
        
        
        User user = userService.createUser( UserDto.builder()
                .email( "freelancer@gmail.com" )
                .password( "password" )
                .nickname( "freelancer" )
                .type( "Frelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        this.freelancer = (Freelancer) user.getType(); 
    }

    @AfterEach
    public void tearDown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void toEntity() {
        ProposalDto dto = ProposalDto.builder()
                .amount( 100.0 )
                .freelancerId( this.freelancer.getId() )
                .projectId( this.project.getId() )
                .build();

        Proposal proposal = proposalMapper.toEntity( dto );

        assertEquals( dto.getAmount(), proposal.getAmount() );
        assertEquals( this.project, proposal.getProject() );
        assertEquals( this.freelancer, proposal.getFreelancer() );
    }

    @Test
    void toEntityInvalidProject() {
        ProposalDto dto = ProposalDto.builder()
                .amount( 100.0 )
                .freelancerId( this.freelancer.getId() )
                .projectId( -1L )
                .build();

        // For Some reason mapstruct throws RunTime instead of ProjectNotFoundException
        assertThrows( RuntimeException.class, () -> proposalMapper.toEntity( dto ) );
    }
    
    @Test
    void toEntityFreelancerNotAvailable() {
        FreelancerDto freelancerDto = FreelancerDto.builder()
                .available( false )
                .build();
                
        freelancerService.updateProfile( this.freelancer.getUser(), freelancerDto );
                
        ProposalDto dto = ProposalDto.builder()
                .amount( 100.0 )
                .freelancerId( this.freelancer.getId() )
                .projectId( this.project.getId() )
                .build();

        assertThrows( RuntimeException.class, () -> proposalMapper.toEntity( dto ) );
    }

    @Test
    void toDto() {
        Proposal proposal = new BidProposal;
        proposal.setId( 1L );
        proposal.setProject( this.project );
        proposal.setAmount( 20.0 );
        proposal.setFreelancer( this.freelancer );

        ProposalDto dto = proposalMapper.toDto( proposal );

        assertEquals( proposal.getId(), dto.getId() );
        assertEquals( proposal.getAmount(), dto.getAmount() );
        assertEquals( proposal.getProject().getId(), dto.getProjectId() );
        assertEquals( proposal.getFreekancer().getId(), dto.getFreelancerId() );
    }

    @Test
    void nullTest() {
        assertNull( bidMapper.toEntity( null ) );
        assertNull( bidMapper.toDto( null ) );
    }
}
