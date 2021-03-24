package org.thekiddos.faith.services;

import org.springframework.stereotype.Service;
import org.thekiddos.faith.models.Skill;
import org.thekiddos.faith.repositories.SkillRepository;

@Service
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    public SkillServiceImpl( SkillRepository skillRepository ) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill createSkill( String name ) {
        return skillRepository.findById( name ).orElse( skillRepository.save( Skill.of( name ) ) );
    }
}
