package org.thekiddos.faith.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.BidComment;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.repositories.BidCommentRepository;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;
    private final BidCommentRepository bidCommentRepository;
    private final EmailService emailService;
    
    @Autowired
    public BidServiceImpl( EmailService emailService, BidRepository bidRepository, ProjectRepository projectRepository, BidCommentRepository bidCommentRepository ) {
        this.emailService = emailService;
        this.bidRepository = bidRepository;
        this.projectRepository = projectRepository;
        this.bidCommentRepository = bidCommentRepository;
    }

    @Override
    public void addBid( BidDto bidDto, Freelancer freelancer ) throws ProjectNotFoundException {
        var project = projectRepository.findById( bidDto.getProjectId() ).orElseThrow( ProjectNotFoundException::new );
        Bid bid = createBid( bidDto, freelancer, project );
        sendNewBidEmail( bid );

        // TODO: replace with BidCommentService
        String commentText = bidDto.getComment();
        if ( commentText != null )
            createCommentForBid( bid, commentText );
    }

    private void sendNewBidEmail( Bid bid ) {
        var toEmail = bid.getProject().getOwner().getUser().getEmail();
        Context context = new Context();
        context.setVariable( "bid", bid );
        emailService.sendTemplateMail( List.of( toEmail ), "faith@noreplay.com", EmailSubjectConstants.NEW_BID, EmailTemplatesConstants.NEW_BID_TEMPLATE, context );
    }

    // TODO: replace with mapper
    private Bid createBid( BidDto bidDto, Freelancer freelancer, Project project ) {
        Bid bid = new Bid();
        bid.setAmount( bidDto.getAmount() );
        bid.setBidder( freelancer );
        bid.setProject( project );
        bidRepository.save( bid );
        return bid;
    }

    private void createCommentForBid( Bid bid, String commentText ) {
        BidComment bidComment = new BidComment();
        bidComment.setBid( bid );
        bidComment.setText( commentText );
        bidCommentRepository.save( bidComment );
    }
}
