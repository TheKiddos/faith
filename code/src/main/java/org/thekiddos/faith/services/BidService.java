package org.thekiddos.faith.services;

import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.exceptions.BidNotFoundException;
import org.thekiddos.faith.exceptions.BiddingNotAllowedException;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;

import java.util.List;

public interface BidService {
    void addBid( BidDto bidDto, Freelancer freelancer ) throws ProjectNotFoundException, BiddingNotAllowedException;

    List<Bid> findByProject( Project project );

    Bid findById( Long id ) throws BidNotFoundException;
}
