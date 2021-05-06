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
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.repositories.EmailRepository;
import org.thekiddos.faith.services.BidCommentService;
import org.thekiddos.faith.services.BidService;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;
import org.thekiddos.faith.utils.EmailSubjectConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BiddingFeature {
    private final WebDriver webDriver;
    private final ProjectService projectService;
    private final BidService bidService;
    private final BidCommentService bidCommentService;
    private final UserService userService;
    private final BidRepository bidRepository;
    private final EmailRepository emailRepository;

    private User stakeholder;
    private Project project;
    private User freelancer;

    @Autowired
    public BiddingFeature( WebDriver webDriver,
                           ProjectService projectService,
                           BidService bidService,
                           BidCommentService bidCommentService,
                           UserService userService,
                           BidRepository bidRepository,
                           EmailRepository emailRepository ) {
        this.webDriver = webDriver;
        this.projectService = projectService;
        this.bidService = bidService;
        this.bidCommentService = bidCommentService;
        this.userService = userService;
        this.bidRepository = bidRepository;
        this.emailRepository = emailRepository;
        setUp();
    }

    void setUp() {
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
    }

    @Given( "Bidding Freelancer logins" )
    public void freelancerLogins() {
        emailRepository.deleteAll();
        bidRepository.deleteAll();

        webDriver.get( Utils.LOGIN_PAGE );
        webDriver.findElement( By.id( "email" ) ).sendKeys( freelancer.getEmail() );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "password" );
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @And( "user visits a public project page" )
    public void freelancerVisitsAPublicProjectPage() {
        webDriver.get( Utils.getProjectDetailsPage( project.getId() ) );
    }

    @And( "Freelancer adds his bid" )
    public void freelancerAddsHisBid() {
        webDriver.findElement( By.id( "amount" ) ).clear();
        webDriver.findElement( By.id( "amount" ) ).sendKeys( "200" );
    }

    @When( "Freelancer clicks the Bid button" )
    public void freelancerClicksTheBidButton() {
        webDriver.findElement( By.id( "add-bid-btn" ) ).click();
    }

    @Then( "Bid is added to project" )
    public void bidIsAddedToProject() {
        WebDriverWait wait = new WebDriverWait( webDriver, 20 );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "toast-container" ) ) );

        var bids = bidRepository.findAll();
        assertEquals( 1, bids.size() );

        var bid = bids.get( 0 );
        assertEquals( 200, bid.getAmount() );
        assertEquals( project, bid.getProject() );
        assertEquals( freelancer, bid.getBidder().getUser() );
    }

    @And( "Project's owner is notified by email" )
    public void projectSOwnerIsNotifiedByEmail() {
        var emails = emailRepository.findAll();
        assertEquals( 1, emails.size() );

        var email = emails.get( 0 );
        assertEquals( stakeholder.getEmail(), email.getTo() );
        assertEquals( EmailSubjectConstants.NEW_BID, email.getSubject() );
    }

    @And( "Freelancer adds a comment" )
    public void freelancerAddsAComment() {
        webDriver.findElement( By.id( "comment" ) ).sendKeys( "Hehe" );
    }

    @And( "Comment is added" )
    public void commentIsAdded() {
        var comments = bidCommentService.findByBid( bidRepository.findAll().get( 0 ) );
        assertEquals( 1, comments.size() );
        assertEquals( "Hehe", comments.get( 0 ).getText() );
    }

    @Given( "Bidding Stakeholder logins" )
    public void stakeholderLogins() {
        BidDto bidDto = BidDto.builder()
                .projectId( project.getId() )
                .amount( 100 )
                .build();
        bidService.addBid( bidDto, (Freelancer) freelancer.getType() );

        webDriver.get( Utils.LOGIN_PAGE );
        webDriver.findElement( By.id( "email" ) ).sendKeys( stakeholder.getEmail() );
        webDriver.findElement( By.id( "password" ) ).sendKeys( "password" );
        webDriver.findElement( By.id( "submit" ) ).click();
    }

    @And( "stakeholder types a comment on a bidding" )
    public void stakeholderTypesACommentOnABidding() {
        webDriver.findElement( By.className( "show-comments-btn" ) ).click();

        WebDriverWait wait = new WebDriverWait( webDriver, 20 );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "new-comment-text" ) ) );

        webDriver.findElement( By.id( "new-comment-text" ) ).sendKeys( "Diee" );
    }

    @When( "stakeholder clicks add comment button" )
    public void stakeholderClicksAddCommentButton() {
        webDriver.findElement( By.id( "add-comment-btn" ) ).click();
        WebDriverWait wait = new WebDriverWait( webDriver, 20 );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "toast-container" ) ) );
    }

    @Then( "comment is saved" )
    public void commentIsSaved() {
        assertTrue( bidCommentService.findByBid( bidRepository.findByBidderAndProject( (Freelancer) freelancer.getType(), project ).orElseThrow() ).stream().anyMatch( bidComment -> bidComment.getText().equals( "Diee" ) ) );
    }

    @And( "Bidding Close Browser" )
    public void biddingCloseBrowser() {
        webDriver.close();
    }
}
