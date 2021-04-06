package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Stakeholder extends UserType {
    @OneToMany( cascade = CascadeType.ALL, mappedBy = "owner" )
    private Set<Project> projects = new HashSet<>();

    @Override
    public String toString() {
        return "Stakeholder";
    }
}
