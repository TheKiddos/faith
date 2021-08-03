package org.thekiddos.faith.services;

import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.mappers.ProjectMapper;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.ProposalRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private static final int NUMBER_OF_FEATURED_PROJECTS = 10;
    private final ProjectMapper projectMapper = ProjectMapper.INSTANCE;
    private final ProjectRepository projectRepository;
    private final ProposalRepository proposalRepository;

    public ProjectServiceImpl( ProjectRepository projectRepository, ProposalRepository proposalRepository ) {
        this.projectRepository = projectRepository;
        this.proposalRepository = proposalRepository;
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

    @Override
    public List<ProjectDto> findAllPublicDto() {
        return projectRepository.findAllByClosedFalseAndAllowBiddingTrue().stream()
                .filter( this::projectNotAssigned )
                .map( projectMapper::projectToProjectDto )
                .collect( Collectors.toList() );
    }

    @Override
    public Project findPublicProjectById( Long id ) throws ProjectNotFoundException {
        var project = projectRepository.findByClosedFalseAndAllowBiddingTrueAndId( id ).orElseThrow( ProjectNotFoundException::new );
        if ( projectNotAssigned( project ) )
            return project;
        throw new ProjectNotFoundException();
    }

    private boolean projectNotAssigned( Project project ) {
        return proposalRepository.findByProject( project ).isEmpty();
    }

    @Override
    public List<ProjectDto> findFeaturedProjectsDto() {
        var projects = findAllPublicDto();
        int numberOfProjects = projects.size();
        return projects.subList( 0, Math.min( numberOfProjects, NUMBER_OF_FEATURED_PROJECTS ) );
    }

    @Override
    public List<ProjectDto> findByOwnerDto( Stakeholder owner ) {
        return projectRepository.findByOwner( owner ).stream().map( projectMapper::projectToProjectDto ).collect( Collectors.toList() );
    }

    @Override
    public ProjectDto findByIdForOwnerDto( Stakeholder owner, long id ) throws ProjectNotFoundException {
        var project = findById( id );
        if ( !project.getOwner().getUser().equals( owner.getUser() ) )
            throw new ProjectNotFoundException();
        return projectMapper.projectToProjectDto( project );
    }

    @Override
    public void closeProject( Project project ) {
        project.setClosed( true );
        projectRepository.save( project );
    }
}
