package org.thekiddos.faith.services;

import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.exceptions.FreelancerNotFoundException;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.exceptions.ProposalNotAllowedException;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Proposal;

import java.util.List;

public interface ProposalService  {
    void sendProposal( ProposalDto dto ) throws ProposalNotAllowedException, ProjectNotFoundException, FreelancerNotFoundException;

    List<Proposal> findByProject( Project project );

    List<Proposal> findByFreelancer( Freelancer freelancer );

    List<ProposalDto> findByFreelancerDto( Freelancer freelancer );
}
