package org.thekiddos.faith.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.*;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.ProposalRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.*;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class ProposalTest {
    private final ProposalService proposalService;
    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final FreelancerService freelancerService;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    @MockBean
    private EmailService emailService;
    
    private Project project;
    private User stakeholderUser;
    private User freelancerUser;
    private Freelancer freelancer;
    
    @Autowired
    public ProposalTest( ProposalService proposalService,
                         ProposalRepository proposalRepository, UserRepository userRepository,
                         ProjectRepository projectRepository,
                         UserService userService,
                         ProjectService projectService, FreelancerService freelancerService ) {
        this.proposalService = proposalService;
        this.proposalRepository = proposalRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectService = projectService;
        this.freelancerService = freelancerService;
    }
    
    @AfterEach
    public void tearDown() {
        proposalRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        proposalRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        ProjectDto projectDto = getTestProjectDto();

        User user = userService.createUser( UserDto.builder()
                .email( "bhbh@gmail.com" )
                .password( "password" )
                .nickname( "bhbhbh" )
                .type( "Stakeholder" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        this.stakeholderUser = user;
        Stakeholder stakeholder = (Stakeholder) user.getType();

        this.project = projectService.createProjectFor( stakeholder, projectDto );
        this.freelancerUser = userRepository.save( getTestUser() );
        this.freelancer = (Freelancer) this.freelancerUser.getType();

        FreelancerDto freelancerDto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        freelancerService.updateProfile( freelancerUser, freelancerDto );
    }

    @Test
    void sendProposal() {
        double amount = 10.0;
        
        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( this.freelancerUser.getType().getId() )
                           .projectId( this.project.getId() )
                           .build();
                           
        assertTrue( proposalRepository.findAll().isEmpty() );
        proposalService.sendProposal( dto );

        var proposals = proposalRepository.findAll();
        assertEquals( 1, proposals.size() );

        Proposal proposal = proposals.stream().findFirst().orElse( null );
        assertNotNull( proposal );
        assertNotNull( proposal.getId() );
        assertEquals( "Proposal of " + amount, proposal.toString() );
        assertEquals( amount, proposal.getAmount() );
        assertEquals( this.project, proposal.getProject() );
        assertEquals( freelancer, proposal.getFreelancer() );
        assertEquals( Status.NEW, proposal.getStatus() );

        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( this.freelancerUser.getEmail() ) ), anyString(), eq( EmailSubjectConstants.NEW_PROPOSAL ), eq( EmailTemplatesConstants.NEW_PROPOSAL_TEMPLATE ), any() );
    }

    @Test
    void sendAnotherProposalIsInvalid() {
        double amount = 10.0;
        
        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( this.freelancerUser.getType().getId() )
                           .projectId( this.project.getId() )
                           .build();
                           
        assertTrue( proposalRepository.findAll().isEmpty() );
        proposalService.sendProposal( dto );

        // sending another proposal
        assertThrows( ProposalNotAllowedException.class, () -> proposalService.sendProposal( dto ) );

        var proposals = proposalRepository.findAll();
        assertEquals( 1, proposals.size() );

        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( this.freelancerUser.getEmail() ) ), anyString(), eq( EmailSubjectConstants.NEW_PROPOSAL ), eq( EmailTemplatesConstants.NEW_PROPOSAL_TEMPLATE ), any() );
    }

    @Test
    void sendProposalWithNoProjectIsInvalid() {
        double amount = 10.0;
        
        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( this.freelancerUser.getType().getId() )
                           .projectId( -1L )
                           .build();
                           
        assertTrue( proposalRepository.findAll().isEmpty() );
        
        assertThrows( ProjectNotFoundException.class, () -> proposalService.sendProposal( dto ) );

        var proposals = proposalRepository.findAll();
        assertEquals( 0, proposals.size() );

        Mockito.verify( emailService, Mockito.times( 0 ) )
                .sendTemplateMail( eq( List.of( this.freelancerUser.getEmail() ) ), anyString(), eq( EmailSubjectConstants.NEW_PROPOSAL ), eq( EmailTemplatesConstants.NEW_PROPOSAL_TEMPLATE ), any() );
    }
    
    @Test
    void sendProposalWithNoFreelancerIsInvalid() {
        double amount = 10.0;
        
        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( -1L )
                           .projectId( this.project.getId() )
                           .build();
                           
        assertTrue( proposalRepository.findAll().isEmpty() );
        
        assertThrows( FreelancerNotFoundException.class, () -> proposalService.sendProposal( dto ) );

        var proposals = proposalRepository.findAll();
        assertEquals( 0, proposals.size() );

        Mockito.verify( emailService, Mockito.times( 0 ) )
                .sendTemplateMail( eq( List.of( this.freelancerUser.getEmail() ) ), anyString(), eq( EmailSubjectConstants.NEW_PROPOSAL ), eq( EmailTemplatesConstants.NEW_PROPOSAL_TEMPLATE ), any() );
    }
    
    @Test
    void sendProposalToUnavailableFreelancerIsInvalid() {
        double amount = 10.0;
        this.freelancer.setAvailable( false );
        this.freelancer.setUser( this.freelancerUser );
        this.freelancerUser = this.userRepository.save( this.freelancerUser );
        
        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( this.freelancer.getId() )
                           .projectId( this.project.getId() )
                           .build();
                           
        assertTrue( proposalRepository.findAll().isEmpty() );
        
        assertThrows( FreelancerNotFoundException.class, () -> proposalService.sendProposal( dto ) );

        var proposals = proposalRepository.findAll();
        assertEquals( 0, proposals.size() );

        Mockito.verify( emailService, Mockito.times( 0 ) )
                .sendTemplateMail( eq( List.of( this.freelancerUser.getEmail() ) ), anyString(), eq( EmailSubjectConstants.NEW_PROPOSAL ), eq( EmailTemplatesConstants.NEW_PROPOSAL_TEMPLATE ), any() );
    }

    @Test
    void sendProposalWithInvalidAmount() {
        double amount = -1;

        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( this.freelancer.getId() )
                           .projectId( this.project.getId() )
                           .build();
        assertTrue( proposalRepository.findAll().isEmpty() );

        assertThrows( TransactionSystemException.class, () -> proposalService.sendProposal( dto ) );

        var proposals = proposalRepository.findAll();
        assertEquals( 0, proposals.size() );

        Mockito.verify( emailService, Mockito.times( 0 ) )
                .sendTemplateMail( eq( List.of( this.freelancerUser.getEmail() ) ), anyString(), eq( EmailSubjectConstants.NEW_PROPOSAL ), eq( EmailTemplatesConstants.NEW_PROPOSAL_TEMPLATE ), any() );
    }

    @Test
    void findByProject() {
        double amount = 10.;
        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( this.freelancer.getId() )
                           .projectId( this.project.getId() )
                           .build();
        proposalService.sendProposal( dto );

        var freelancer2 = getTestUser();
        freelancer2.setEmail( "freelancer2@test.com" );
        freelancer2.setNickname( "freelancer2" );
        freelancer2 = userRepository.save( freelancer2 );
        FreelancerDto freelancerDto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        freelancerService.updateProfile( freelancer2, freelancerDto );
        
        dto.setFreelancerId( freelancer2.getType().getId() );
        proposalService.sendProposal( dto );

        var project2 = projectService.createProjectFor( this.project.getOwner(), getTestProjectDto() );
        dto.setProjectId( project2.getId() );
        proposalService.sendProposal( dto );

        var proposals = proposalService.findByProject( this.project );
        assertEquals( 2, proposals.size() );
        assertTrue( proposals.stream().allMatch( proposal -> proposal.getProject().equals( this.project ) ) );
    }
    
    @Test
    void findByFreelancer() {
        double amount = 10.;
        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( this.freelancer.getId() )
                           .projectId( this.project.getId() )
                           .build();
        proposalService.sendProposal( dto );

        var freelancer2 = getTestUser();
        freelancer2.setEmail( "freelancer2@test.com" );
        freelancer2.setNickname( "freelancer2" );
        freelancer2 = userRepository.save( freelancer2 );
        FreelancerDto freelancerDto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        freelancerService.updateProfile( freelancer2, freelancerDto );
        
        dto.setFreelancerId( freelancer2.getType().getId() );
        proposalService.sendProposal( dto );

        var project2 = projectService.createProjectFor( this.project.getOwner(), getTestProjectDto() );
        dto.setProjectId( project2.getId() );
        proposalService.sendProposal( dto );

        var proposals = proposalService.findByFreelancer( (Freelancer) freelancer2.getType() );
        assertEquals( 2, proposals.size() );

        // for java lambda
        User finalFreelancer = freelancer2;
        assertTrue( proposals.stream().allMatch( proposal -> proposal.getFreelancer().equals( finalFreelancer.getType() ) ) );
    }

    @Test
    void findByFreelancerDto() {
        double amount = 10.;
        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( this.freelancer.getId() )
                           .projectId( this.project.getId() )
                           .build();
        proposalService.sendProposal( dto );

        var freelancer2 = getTestUser();
        freelancer2.setEmail( "freelancer2@test.com" );
        freelancer2.setNickname( "freelancer2" );
        freelancer2 = userRepository.save( freelancer2 );
        FreelancerDto freelancerDto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        freelancerService.updateProfile( freelancer2, freelancerDto );
        
        dto.setFreelancerId( freelancer2.getType().getId() );
        proposalService.sendProposal( dto );

        var project2 = projectService.createProjectFor( this.project.getOwner(), getTestProjectDto() );
        dto.setProjectId( project2.getId() );
        proposalService.sendProposal( dto );

        var proposals = proposalService.findByFreelancerDto( (Freelancer) freelancer2.getType() );
        assertEquals( 2, proposals.size() );

        User finalFreelancer = freelancer2;
        assertTrue( proposals.stream().allMatch( proposal -> proposal.getFreelancerId() == finalFreelancer.getType().getId() ) );
    }

    @Test
    void findNewFreelancerProposalsDto() {
        double amount = 10.;
        ProposalDto dto = ProposalDto.builder()
                .amount( amount )
                .freelancerId( this.freelancer.getId() )
                .projectId( this.project.getId() )
                .build();
        proposalService.sendProposal( dto );

        var freelancer2 = getTestUser();
        freelancer2.setEmail( "freelancer2@test.com" );
        freelancer2.setNickname( "freelancer2" );
        freelancer2 = userRepository.save( freelancer2 );
        FreelancerDto freelancerDto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        freelancerService.updateProfile( freelancer2, freelancerDto );

        dto.setFreelancerId( freelancer2.getType().getId() );
        proposalService.sendProposal( dto );

        // Modify the status
        var proposal = proposalService.findByFreelancer( (Freelancer) freelancer2.getType() ).get( 0 );
        proposal.setStatus( Status.REJECTED );
        proposalRepository.save( proposal );

        var project2 = projectService.createProjectFor( this.project.getOwner(), getTestProjectDto() );
        dto.setProjectId( project2.getId() );
        proposalService.sendProposal( dto );

        var proposals = proposalService.findNewFreelancerProposals( (Freelancer) freelancer2.getType() );
        assertEquals( 1, proposals.size() );

        var proposalDto = proposals.get( 0 );
        assertEquals( freelancer2.getType().getId(), proposalDto.getFreelancerId() );
        assertEquals( Status.NEW.name(), proposalDto.getStatus() );
    }

    @Test
    void findProposalFor() {
        // Since method is simple we will test all cases here
        double amount = 10.;
        ProposalDto dto = ProposalDto.builder()
                .amount( amount )
                .freelancerId( this.freelancer.getId() )
                .projectId( this.project.getId() )
                .build();
        proposalService.sendProposal( dto );

        var freelancer2 = getTestUser();
        freelancer2.setEmail( "freelancer2@test.com" );
        freelancer2.setNickname( "freelancer2" );
        freelancer2 = userRepository.save( freelancer2 );
        FreelancerDto freelancerDto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        freelancerService.updateProfile( freelancer2, freelancerDto );

        dto.setFreelancerId( freelancer2.getType().getId() );
        proposalService.sendProposal( dto );

        var project2 = projectService.createProjectFor( this.project.getOwner(), getTestProjectDto() );
        dto.setProjectId( project2.getId() );
        proposalService.sendProposal( dto );

        var proposals = proposalService.findByFreelancerDto( (Freelancer) freelancer2.getType() );
        // Normal Case
        // we have two proposals here
        var proposal = proposalService.findProposalFor( (Freelancer) freelancer2.getType(), proposals.get( 0 ).getId() );
        assertEquals( (Freelancer) freelancer2.getType(), proposal.getFreelancer() );
        assertEquals( proposals.get( 0 ).getId(), proposal.getId() );

        // Wrong id for freelancer
        assertThrows( ProposalNotFoundException.class, () -> proposalService.findProposalFor( freelancer, proposals.get( 0 ).getId() ) );
    }

    @Test
    void findFreelancerAcceptedProposalFor() {
        // Since method is simple we will test all cases here
        double amount = 10.;
        ProposalDto dto = ProposalDto.builder()
                .amount( amount )
                .freelancerId( this.freelancer.getId() )
                .projectId( this.project.getId() )
                .build();
        proposalService.sendProposal( dto );

        var freelancer2 = getTestUser();
        freelancer2.setEmail( "freelancer2@test.com" );
        freelancer2.setNickname( "freelancer2" );
        freelancer2 = userRepository.save( freelancer2 );
        FreelancerDto freelancerDto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        freelancerService.updateProfile( freelancer2, freelancerDto );

        dto.setFreelancerId( freelancer2.getType().getId() );
        proposalService.sendProposal( dto );

        var project2 = projectService.createProjectFor( this.project.getOwner(), getTestProjectDto() );
        dto.setProjectId( project2.getId() );
        proposalService.sendProposal( dto );

        var proposals = proposalService.findByFreelancer( (Freelancer) freelancer2.getType() );

        // Accept One of the proposals
        var acceptedProposal = proposals.get( 0 );
        var pendingProposal = proposals.get( 1 );
        proposalService.setStatus( acceptedProposal, Status.ACCEPTED );
        proposals = proposalService.findByFreelancer( (Freelancer) freelancer2.getType() );

        // Normal Case Check For Actual Accepted Proposal
        var proposal = proposalService.findFreelancerAcceptedProposalFor( acceptedProposal.getProject() );
        assertEquals( (Freelancer) freelancer2.getType(), proposal.getFreelancer() );

        // The second project still wasn't accepted
        assertThrows( ProposalNotFoundException.class, () -> proposalService.findFreelancerAcceptedProposalFor( pendingProposal.getProject() ) );
    }

    @Test
    void setStatus() {
        double amount = 10.;
        ProposalDto dto = ProposalDto.builder()
                .amount( amount )
                .freelancerId( this.freelancer.getId() )
                .projectId( this.project.getId() )
                .build();
        proposalService.sendProposal( dto );

        var proposal = proposalService.findByFreelancer( this.freelancer ).get( 0 );

        // Move from new to something else is allowed
        proposalService.setStatus( proposal, Status.REJECTED );
        proposal = proposalRepository.findById( proposal.getId() ).orElse( null );
        assert proposal != null;
        assertEquals( Status.REJECTED, proposal.getStatus() );
        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( this.stakeholderUser.getEmail() ) ), anyString(), eq( EmailSubjectConstants.PROPOSAL_STATUS_CHANGED ), eq( EmailTemplatesConstants.PROPOSAL_STATUS_TEMPLATE ), any() );

        // Move from non-new status to something else is not allowed
        Proposal finalProposal = proposal;
        assertThrows( InvalidTransitionException.class, () -> proposalService.setStatus( finalProposal, Status.NEW ) );
        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( this.stakeholderUser.getEmail() ) ), anyString(), eq( EmailSubjectConstants.PROPOSAL_STATUS_CHANGED ), eq( EmailTemplatesConstants.PROPOSAL_STATUS_TEMPLATE ), any() );

        // Make sure status not changed
        proposal = proposalRepository.findById( proposal.getId() ).orElse( null );
        assertEquals( Status.REJECTED, proposal.getStatus() );

        // The test makes it point but it's not extensive but we will leave it for now.
    }

    @Test
    void equalsAndHashcode() {
        Proposal proposal = new Proposal();
        proposal.setAmount( 200 );

        Proposal proposal2 = new Proposal();
        proposal2.setAmount( 200 );

        Proposal biggerProposal = new Proposal();
        biggerProposal.setAmount( 300 );

        assertEquals( proposal, proposal );
        assertEquals( proposal, proposal2 );
        assertNotEquals( proposal, biggerProposal );
        assertNotEquals( proposal, null );
        assertNotEquals( null, proposal );

        assertEquals( proposal.hashCode(), proposal2.hashCode() );
        assertNotEquals( proposal.hashCode(), biggerProposal.hashCode() );
    }

    @Test
    void equalsAndHashCodeOnlyAffectedByAmount() {
        Proposal proposal = new Proposal();
        proposal.setAmount( 200 );
        proposal.setProject( this.project );
        proposal.setFreelancer( this.freelancer );

        Proposal proposal2 = new Proposal();
        proposal2.setAmount( 200 );

        assertEquals( proposal, proposal2 );
        assertEquals( proposal.hashCode(), proposal2.hashCode() );
    }
    
    private User getTestUser() {
        UserDto userDto = UserDto.builder().email( "freelancer@test.com" )
                .password( "password" )
                .passwordConfirm( "password" )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( "Freelancer" )
                .build();

        return userMapper.userDtoToUser( userDto );
    }

    private ProjectDto getTestProjectDto() {
        return ProjectDto.builder()
                .name( "new world order" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();
    }
}
