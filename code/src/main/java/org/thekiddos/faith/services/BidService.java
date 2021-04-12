package org.thekiddos.faith.services;

import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.models.Freelancer;

public interface BidService {
    void addBid( BidDto bidDto, Freelancer freelancer );
}
