package org.thekiddos.faith.repositories;

import java.util.List;
import org.springframework.data.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thekiddos.faith.models.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAll();
}
