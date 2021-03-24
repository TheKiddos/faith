package org.thekiddos.faith.services;

import org.thekiddos.faith.models.Skill;

public interface SkillService {
    /**
     * Creates and save a skill object
     * @param name Name of skill
     * @return The created skill
     */
    Skill createSkill( String name );
}
