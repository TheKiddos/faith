package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.models.Bid;

@Service
public interface BidRepository extends JpaRepository<Bid, Long> {
}
