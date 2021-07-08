package org.thekiddos.faith.services;

import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.exceptions.*;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Proposal;
import org.thekiddos.faith.models.Status;

import java.util.List;

public interface ProposalService  {
    void sendProposal( ProposalDto dto ) throws ProposalNotAllowedException, ProjectNotFoundException, FreelancerNotFoundException;

    List<Proposal> findByProject( Project project );

    List<Proposal> findByFreelancer( Freelancer freelancer );

    List<ProposalDto> findByFreelancerDto( Freelancer freelancer );

    List<ProposalDto> findNewFreelancerProposals( Freelancer freelancer );

    Proposal findProposalFor( Freelancer freelancer, long proposalId ) throws ProposalNotFoundException;

    void setStatus( Proposal proposal, Status status ) throws InvalidTransitionException;

    Proposal findFreelancerAcceptedProposalFor( Project project ) throws ProposalNotFoundException;
}
