package org.thekiddos.faith.services;

import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.mappers.ProjectMapper;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.repositories.ProjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper = ProjectMapper.INSTANCE;
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl( ProjectRepository projectRepository ) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project createProjectFor( Stakeholder owner, ProjectDto projectDto ) {
        Project project = projectMapper.projectDtoToProject( projectDto );
        project.setOwner( owner );
        return projectRepository.save( project );
    }

    @Override
    public Project findById( Long id ) throws ProjectNotFoundException {
        return projectRepository.findById( id ).orElseThrow( ProjectNotFoundException::new );
    }
    
    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }
    
    @Override
    public List<ProjectDto> findAllDto() {
        return findAll().stream().map( projectMapper::projectToProjectDto ).collect( Collectors.toList() );
    }
}
