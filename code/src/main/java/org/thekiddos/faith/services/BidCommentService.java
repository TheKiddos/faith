package org.thekiddos.faith.services;

import org.thekiddos.faith.dtos.BidCommentDto;
import org.thekiddos.faith.exceptions.BidNotFoundException;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.BidComment;

import java.util.List;

public interface BidCommentService {
    void addComment( BidCommentDto dto ) throws BidNotFoundException;

    List<BidComment> findByBid( Bid bid );

    List<BidCommentDto> findByBidDto( Bid bid );
}
