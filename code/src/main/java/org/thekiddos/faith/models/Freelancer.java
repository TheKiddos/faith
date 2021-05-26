package org.thekiddos.faith.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Freelancer extends UserType {
    private String summary;
    private boolean available;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name="skill_freelancer",
            joinColumns={@JoinColumn(name="freelancer_id")},
            inverseJoinColumns={@JoinColumn(name="skill_name")})
    private Set<Skill> skills = new HashSet<>();

    @Override
    public String toString() {
        return "Freelancer";
    }
}
