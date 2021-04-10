package org.thekiddos.faith.services;

import org.thekiddos.faith.models.*;

public interface BidService {
    void addBid( double amount, Project project, Freelancer freelancer );
}
