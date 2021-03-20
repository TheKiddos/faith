package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.Entity;
import java.util.Set;

@Data
@Entity
public class Freelancer extends UserType {
    private String summary;
    private boolean available;
    private Set<Skill> skills;

    @Override
    public String toString() {
        return "Freelancer";
    }
}
