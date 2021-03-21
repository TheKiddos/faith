package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.models.Skill;

import java.util.List;

@Service
public interface SkillRepository extends JpaRepository<Skill, String> {
    List<Skill> findAll();
}
