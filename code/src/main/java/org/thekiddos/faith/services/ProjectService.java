package org.thekiddos.faith.services;

import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;

import java.util.List;

public interface ProjectService {
    /**
     * Creates and save a project for the specified stakeholder
     * @param owner The owner of the project
     * @param projectDto The data to fill the created project with
     * @return The created project
     */
    Project createProjectFor( Stakeholder owner, ProjectDto projectDto );

    Project findById( Long id ) throws ProjectNotFoundException;
    
    List<Project> findAll();
}
