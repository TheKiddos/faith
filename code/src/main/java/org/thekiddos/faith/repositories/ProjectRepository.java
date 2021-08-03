package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAll();

    List<Project> findByOwner( Stakeholder owner );

    List<Project> findAllByClosedFalseAndAllowBiddingTrue();

    Optional<Project> findByClosedFalseAndAllowBiddingTrueAndId( Long id );
}
