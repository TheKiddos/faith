package org.thekiddos.faith.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final EmailService emailService;
    
    @Autowired
    public BidServiceImpl( EmailService emailService, BidRepository bidRepository ) {
        this.emailService = emailService;
        this.bidRepository = bidRepository;
    }

    @Override
    public void addBid( double amount, Project project, Freelancer freelancer ) {
        Bid bid = new Bid();
        bid.setAmount( amount );
        bid.setBidder( freelancer );
        bid.setProject( project );
        bidRepository.save( bid );
        
        Context context = new Context();
        context.setVariable( "bid", bid );
        var toEmail = project.getOwner().getUser().getEmail();
        emailService.sendTemplateMail( List.of( toEmail ), "faith@noreplay.com", EmailSubjectConstants.NEW_BID, EmailTemplatesConstants.NEW_BID_TEMPLATE, context );
    }
}
