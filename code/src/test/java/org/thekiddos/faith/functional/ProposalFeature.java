package org.thekiddos.faith.functional;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.Utils;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.models.*;
import org.thekiddos.faith.repositories.*;
import org.thekiddos.faith.services.*;
import org.thekiddos.faith.utils.EmailSubjectConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProposalFeature {
    private final WebDriver webDriver;
    private final UserService userService;
    private final ProjectService projectService;
    private final BidService bidService;
    private final ProposalService proposalService;
    private final FreelancerService freelancerService;

    private final EmailRepository emailRepository;
    private final ProposalRepository proposalRepository;
    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private User freelancer;
    private User stakeholder;
    private Project project;
    private Bid bid;
    private Proposal proposal;

    @Autowired
    public ProposalFeature( WebDriver webDriver, UserService userService, ProjectService projectService, BidService bidService, ProposalService proposalService, FreelancerService freelancerService, EmailRepository emailRepository, ProposalRepository proposalRepository, BidRepository bidRepository, ProjectRepository projectRepository, UserRepository userRepository ) {
        this.webDriver = webDriver;
        this.userService = userService;
        this.projectService = projectService;
        this.bidService = bidService;
        this.proposalService = proposalService;
        this.freelancerService = freelancerService;
        this.emailRepository = emailRepository;
        this.proposalRepository = proposalRepository;
        this.bidRepository = bidRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    private void setUp() {
        emailRepository.deleteAll();
        proposalRepository.deleteAll();
        bidRepository.deleteAll();
        projectRepository.deleteAll();

        stakeholder = Utils.getOrCreateTestUser( userService, "stakeholder@bidding.com", "Stakeholder" );
        var projectDto = ProjectDto.builder()
                .allowBidding( true )
                .description( "Hellooooo" )
                .duration( 3 )
                .minimumQualification( 20 )
                .name( "Death" )
                .preferredBid( 200 )
                .build();
        project = projectService.createProjectFor( (Stakeholder) stakeholder.getType(), projectDto );

        freelancer = Utils.getOrCreateTestUser( userService, "freelancer@bidding.com", "Freelancer" );
        freelancerService.updateProfile( freelancer, FreelancerDto.builder().available( true ).build() );

        var bidDto = BidDto.builder()
                .amount( 200 )
                .projectId( project.getId() )
                .build();
        bidService.addBid( bidDto, (Freelancer) freelancer.getType() );
        bid = bidService.findByProject( project ).get( 0 );
    }

    @Given("Proposal Stakeholder logs in")
    public void proposalStakeholderLogsIn() {
        setUp();
        emailRepository.deleteAll();
        webDriver.manage().window().maximize();
        webDriver.get( Utils.LOGIN_PAGE );
        webDriver.findElement( By.id( "email" ) ).sendKeys( stakeholder.getEmail() );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "password" );
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @And("stakeholder goes to a project dashboard")
    public void stakeholderGoesToAProjectDashboard() {
        webDriver.get( Utils.getProjectDashboardPage( project.getId() ) );
    }

    @And("stakeholder sets his proposal amount to a freelancer")
    public void stakeholderSetsHisProposalAmountToAFreelancer() {
        webDriver.findElement( By.id( "amount-" + freelancer.getType().getId() ) ).clear();
        webDriver.findElement( By.id( "amount-" + freelancer.getType().getId() ) ).sendKeys( "200" );
    }

    @When("stakeholder clicks the send proposal button")
    public void stakeholderClicksTheSendProposalButton() {
        var element = webDriver.findElement( By.className( "send-proposal-btn" ) );
        WebDriverWait wait = new WebDriverWait( webDriver, 3 );
        wait.until( ExpectedConditions.elementToBeClickable( element ) );
        element.click();
    }

    @Then("proposal is saved")
    public void proposalIsSaved() {
        WebDriverWait wait = new WebDriverWait( webDriver, 20 );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "toast-container" ) ) );

        var proposal = proposalRepository.findByProjectAndFreelancer( project, (Freelancer) freelancer.getType() );
        assertTrue( proposal.isPresent() );
        assertEquals( 200, proposal.get().getAmount() );
    }

    @And("freelancer is informed by email that a new proposal was sent")
    public void freelancerIsInformedByEmailThatANewProposalWasSent() {
        var emails = emailRepository.findAll();
        assertEquals( 1, emails.size() );

        var email = emails.get( 0 );
        assertEquals( freelancer.getEmail(), email.getTo() );
        assertEquals( EmailSubjectConstants.NEW_PROPOSAL, email.getSubject() );
        webDriver.close();
    }

    @Given("Proposal Freelancer logs in")
    public void proposalFreelancerLogsIn() {
        setUp();
        proposalService.sendProposal(
                ProposalDto
                        .builder()
                        .freelancerId( freelancer.getType().getId() )
                        .projectId( project.getId() )
                        .amount( 200 )
                        .build()
        );
        var proposals = proposalRepository.findAll();
        assertEquals( proposals.size(), 1 );

        this.proposal = proposals.get( 0 );
        emailRepository.deleteAll();
        webDriver.manage().window().maximize();

        webDriver.get( Utils.LOGIN_PAGE );
        webDriver.findElement( By.id( "email" ) ).sendKeys( freelancer.getEmail() );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "password" );
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @And("freelancer visits my proposals page")
    public void freelancerVisitsMyProposalsPage() {
        webDriver.get( Utils.MY_PROPOSALS_PAGE );
    }

    @When("Freelancer clicks the accept button")
    public void freelancerClicksTheAcceptButton() {
        webDriver.findElement( By.className( "accept-btn" ) ).click();
    }

    @Then("proposal status is changed to accepted")
    public void proposalStatusIsChangedToAccepted() {
        var proposal = proposalRepository.findById( this.proposal.getId() );
        assertTrue( proposal.isPresent() );
        assertEquals( Status.ACCEPTED, proposal.get().getStatus() );
    }

    @And("stakeholder is informed by email that proposal was accepted")
    public void stakeholderIsInformedByEmailThatProposalWasAccepted() {
        var emails = emailRepository.findAll();
        assertEquals( 1, emails.size() );

        var email = emails.get( 0 );
        assertEquals( stakeholder.getEmail(), email.getTo() );
        assertEquals( EmailSubjectConstants.PROPOSAL_STATUS_CHANGED, email.getSubject() );
        webDriver.close();
    }


    @When("Freelancer clicks the reject button")
    public void freelancerClicksTheRejectButton() {
        webDriver.findElement( By.className( "reject-btn" ) ).click();
    }

    @Then("proposal status is changed to rejected")
    public void proposalStatusIsChangedToRejected() {
        var proposal = proposalRepository.findById( this.proposal.getId() );
        assertTrue( proposal.isPresent() );
        assertEquals( Status.REJECTED, proposal.get().getStatus() );
    }

    @And("stakeholder is informed by email that proposal was rejected")
    public void stakeholderIsInformedByEmailThatProposalWasRejected() {
        var emails = emailRepository.findAll();
        assertEquals( 1, emails.size() );

        var email = emails.get( 0 );
        assertEquals( stakeholder.getEmail(), email.getTo() );
        assertEquals( EmailSubjectConstants.PROPOSAL_STATUS_CHANGED, email.getSubject() );
        webDriver.close();
    }
}
