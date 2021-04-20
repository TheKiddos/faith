package org.thekiddos.faith.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.exceptions.BiddingNotAllowedException;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.mappers.BidMapper;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.BidComment;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.repositories.BidCommentRepository;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final BidCommentRepository bidCommentRepository;
    private final BidMapper bidMapper;
    private final EmailService emailService;
    
    @Autowired
    public BidServiceImpl( EmailService emailService, BidRepository bidRepository, BidCommentRepository bidCommentRepository, BidMapper bidMapper ) {
        this.emailService = emailService;
        this.bidRepository = bidRepository;
        this.bidCommentRepository = bidCommentRepository;
        this.bidMapper = bidMapper;
    }

    @Override
    public void addBid( BidDto bidDto, Freelancer freelancer ) throws ProjectNotFoundException, BiddingNotAllowedException {
        Bid bid = createBid( bidDto, freelancer );
        sendNewBidEmail( bid );

        // TODO: replace with BidCommentService
        String commentText = bidDto.getComment();
        createCommentForBid( bid, commentText );
    }

    private void sendNewBidEmail( Bid bid ) {
        var toEmail = bid.getProject().getOwner().getUser().getEmail();
        Context context = new Context();
        context.setVariable( "bid", bid );
        emailService.sendTemplateMail( List.of( toEmail ), "faith@noreplay.com", EmailSubjectConstants.NEW_BID, EmailTemplatesConstants.NEW_BID_TEMPLATE, context );
    }

    private Bid createBid( BidDto bidDto, Freelancer freelancer ) {
        Bid bid = bidMapper.toEntity( bidDto );

        Project project = bid.getProject();
        if ( !project.isAllowBidding() )
            throw new BiddingNotAllowedException( "Bidding on this project is not allowed" );

        if ( hasBidding( freelancer, project ) )
            throw new BiddingNotAllowedException( "You already submitted a bid on this project" );

        bid.setBidder( freelancer );
        bidRepository.save( bid );
        return bid;
    }

    private boolean hasBidding( Freelancer freelancer, Project project ) {
        return bidRepository.findAll().stream().filter( bid -> bid.getBidder().equals( freelancer ) )
                .anyMatch( bid -> bid.getProject().equals( project ) );
    }

    private void createCommentForBid( Bid bid, String commentText ) {
        if ( commentText == null )
            return;
        BidComment bidComment = new BidComment();
        bidComment.setBid( bid );
        bidComment.setText( commentText );
        bidCommentRepository.save( bidComment );
    }

    @Override
    public List<Bid> findByProject( Project project ) {
        return bidRepository.findByProject( project );
    }
}
