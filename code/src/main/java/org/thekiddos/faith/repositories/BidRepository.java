package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.Project;

import java.util.List;

@Service
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByProject( Project project );
}
