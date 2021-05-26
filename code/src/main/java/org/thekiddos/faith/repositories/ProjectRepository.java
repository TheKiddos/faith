package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAll();
    List<Project> findByOwner( Stakeholder owner );
}
