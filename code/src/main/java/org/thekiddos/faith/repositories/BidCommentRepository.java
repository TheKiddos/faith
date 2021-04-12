package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thekiddos.faith.models.BidComment;

public interface BidCommentRepository extends JpaRepository<BidComment, Long> {
}
