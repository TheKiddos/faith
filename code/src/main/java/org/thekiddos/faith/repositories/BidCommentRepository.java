package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.BidComment;

import java.util.List;

public interface BidCommentRepository extends JpaRepository<BidComment, Long> {
    List<BidComment> findByBid( Bid bid );
}
