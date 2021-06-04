package org.thekiddos.faith.services;

import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.exceptions.FreelancerNotFoundException;
import org.thekiddos.faith.mappers.FreelancerMapper;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.SkillRepository;
import org.thekiddos.faith.repositories.UserRepository;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class FreelancerServiceImpl implements FreelancerService {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final FreelancerMapper freelancerMapper;

    public FreelancerServiceImpl( UserRepository userRepository, SkillRepository skillRepository, FreelancerMapper freelancerMapper ) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.freelancerMapper = freelancerMapper;
    }

    @Override
    public void updateProfile( User user, FreelancerDto dto ) {
        Freelancer originalFreelancer;
        try {
            originalFreelancer = (Freelancer) user.getType();
        }
        catch ( ClassCastException e ) {
            return;
        }

        var newFreelancer = freelancerMapper.toEntity( dto );
        originalFreelancer.setSummary( newFreelancer.getSummary() );
        originalFreelancer.setAvailable( newFreelancer.isAvailable() );
        originalFreelancer.setSkills( newFreelancer.getSkills() );

        skillRepository.saveAll( originalFreelancer.getSkills() );

        user.setType( originalFreelancer );
        userRepository.save( user );
    }

    @Override
    public List<FreelancerDto> getAvailableFreelancersDto( Project projectToFindFreelancersFor ) {
        // TODO: Implement with dedicated repo instead
        var freelancerUsers = userRepository.findAll().stream().filter( user -> user.getType() instanceof Freelancer );
        return freelancerUsers.map( user -> freelancerMapper.toDtoWithProject( (Freelancer) user.getType(), projectToFindFreelancersFor ) )
                .filter( canHireFreelancer() ).collect( Collectors.toList() );
    }

    private Predicate<FreelancerDto> canHireFreelancer() {
        return freelancerDto -> freelancerDto.isAvailable() || freelancerDto.getProjectBidAmount() > 0;
    }
    
    @Override
    public Freelancer getAvailableFreelancerById( Long id ) throws FreelancerNotFoundException {
        // TODO: Use Freelancer Repo
        for ( var user : userRepository.findAll() ) {
            try {
                var freelancer = (Freelancer) user.getType();
                if ( freelancer.getId().equals( id ) && freelancer.isAvailable() )
                    return freelancer;
            }
            catch ( Exception exception ) {
                // Ignore
            }
        }
        throw new FreelancerNotFoundException();
    }
}
