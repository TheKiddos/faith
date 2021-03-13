package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Stakeholder extends UserType {
    @Override
    public String toString() {
        return "Stakeholder";
    }
}
