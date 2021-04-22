package org.thekiddos.faith.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.BidCommentDto;
import org.thekiddos.faith.exceptions.BidNotFoundException;
import org.thekiddos.faith.mappers.BidCommentMapper;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.BidComment;
import org.thekiddos.faith.repositories.BidCommentRepository;

import java.util.List;

@Service
public class BidCommentServiceImpl implements BidCommentService {
    private final BidCommentMapper bidCommentMapper;
    private final BidCommentRepository bidCommentRepository;

    @Autowired
    public BidCommentServiceImpl( BidCommentMapper bidCommentMapper, BidCommentRepository bidCommentRepository ) {
        this.bidCommentMapper = bidCommentMapper;
        this.bidCommentRepository = bidCommentRepository;
    }

    @Override
    public void addComment( BidCommentDto dto ) throws BidNotFoundException {
        BidComment comment = bidCommentMapper.toEntity( dto );
        bidCommentRepository.save( comment );
    }

    @Override
    public List<BidComment> findByBid( Bid bid ) {
        return bidCommentRepository.findByBid( bid );
    }
}
