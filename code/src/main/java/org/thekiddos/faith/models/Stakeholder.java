package org.thekiddos.faith.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Stakeholder extends UserType {
    @Override
    public String toString() {
        return "Stakeholder";
    }
}
