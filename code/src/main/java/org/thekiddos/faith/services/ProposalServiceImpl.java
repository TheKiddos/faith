package org.thekiddos.faith.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.exceptions.FreelancerNotFoundException;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.exceptions.ProposalNotAllowedException;
import org.thekiddos.faith.mappers.ProposalMapper;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Proposal;
import org.thekiddos.faith.repositories.ProposalRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProposalServiceImpl implements ProposalService {
    private final ProposalMapper proposalMapper;
    private final ProposalRepository proposalRepository;

    @Autowired
    public ProposalServiceImpl( ProposalMapper proposalMapper, ProposalRepository proposalRepository ) {
        this.proposalMapper = proposalMapper;
        this.proposalRepository = proposalRepository;
    }

    @Override
    public void sendProposal( ProposalDto dto ) throws ProposalNotAllowedException, ProjectNotFoundException, FreelancerNotFoundException {
        Proposal proposal = proposalMapper.toEntity( dto );
        validate( proposal );
        proposalRepository.save( proposal );
    }

    private void validate( Proposal proposal ) {
        if ( !proposal.getFreelancer().isAvailable() )
            throw new FreelancerNotFoundException();

        // TODO: use java validation for unique
        if ( proposalAlreadyExists( proposal ) )
            throw new ProposalNotAllowedException();
    }

    private boolean proposalAlreadyExists( Proposal proposal ) {
        return proposalRepository.findByProjectAndFreelancer( proposal.getProject(), proposal.getFreelancer() ).isPresent();
    }

    @Override
    public List<Proposal> findByProject( Project project ) {
        return proposalRepository.findByProject( project );
    }

    @Override
    public List<Proposal> findByFreelancer( Freelancer freelancer ) {
        return proposalRepository.findByFreelancer( freelancer );
    }

    @Override
    public List<ProposalDto> findByFreelancerDto( Freelancer freelancer ) {
        return findByFreelancer( freelancer ).stream().map( proposalMapper::toDto ).collect( Collectors.toList() );
    }
}
