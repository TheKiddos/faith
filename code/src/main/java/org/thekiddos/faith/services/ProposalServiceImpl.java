package org.thekiddos.faith.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.exceptions.*;
import org.thekiddos.faith.mappers.ProposalMapper;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Proposal;
import org.thekiddos.faith.models.Status;
import org.thekiddos.faith.repositories.ProposalRepository;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProposalServiceImpl implements ProposalService {
    private final ProposalMapper proposalMapper;
    private final ProposalRepository proposalRepository;
    private final EmailService emailService;

    @Autowired
    public ProposalServiceImpl( ProposalMapper proposalMapper, ProposalRepository proposalRepository, EmailService emailService ) {
        this.proposalMapper = proposalMapper;
        this.proposalRepository = proposalRepository;
        this.emailService = emailService;
    }

    @Override
    public void sendProposal( ProposalDto dto ) throws ProposalNotAllowedException, ProjectNotFoundException, FreelancerNotFoundException {
        Proposal proposal = proposalMapper.toEntity( dto );
        validate( proposal );
        proposalRepository.save( proposal );

        Context context = new Context();
        context.setVariable( "proposal", proposal );
        emailService.sendTemplateMail( List.of( proposal.getFreelancer().getUser().getEmail() ), "admin@faith.com", EmailSubjectConstants.NEW_PROPOSAL, EmailTemplatesConstants.NEW_PROPOSAL_TEMPLATE, context );
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

    @Override
    public List<ProposalDto> findNewFreelancerProposals( Freelancer freelancer ) {
        return findByFreelancerDto( freelancer ).stream()
                .filter( proposalDto -> proposalDto.getStatus().equals( Status.NEW.name() ) )
                .collect( Collectors.toList() );
    }

    @Override
    public Proposal findProposalFor( Freelancer freelancer, long proposalId ) throws ProposalNotFoundException {
        var proposal = proposalRepository.findById( proposalId ).orElseThrow( ProposalNotFoundException::new );

        if ( !proposal.getFreelancer().equals( freelancer ) ) {
            throw new ProposalNotFoundException();
        }

        return proposal;
    }

    @Override
    public void setStatus( Proposal proposal, Status status ) throws InvalidTransitionException {
        if ( !proposal.getStatus().equals( Status.NEW ) ) {
            throw new InvalidTransitionException( Status.NEW, status );
        }

        proposal.setStatus( status );
        proposalRepository.save( proposal );
    }

    @Override
    public Proposal findFreelancerAcceptedProposalFor( Project project ) throws ProposalNotFoundException {
        return findByProject( project ).stream().filter( proposal -> proposal.getStatus().equals( Status.ACCEPTED ) )
                .findAny().orElseThrow( ProposalNotFoundException::new );
    }
}
