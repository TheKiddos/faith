package org.thekiddos.faith.services;

import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.mappers.FreelancerMapper;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.SkillRepository;
import org.thekiddos.faith.repositories.UserRepository;

@Service
public class FreelancerServiceImpl implements FreelancerService {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public FreelancerServiceImpl( UserRepository userRepository, SkillRepository skillRepository ) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
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

        var newFreelancer = FreelancerMapper.INSTANCE.toEntity( dto );
        originalFreelancer.setSummary( newFreelancer.getSummary() );
        originalFreelancer.setAvailable( newFreelancer.isAvailable() );
        originalFreelancer.setSkills( newFreelancer.getSkills() );

        skillRepository.saveAll( originalFreelancer.getSkills() );

        user.setType( originalFreelancer );
        userRepository.save( user );
    }
}
