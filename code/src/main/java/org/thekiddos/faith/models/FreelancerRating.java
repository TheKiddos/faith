package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class FreelancerRating {
    @Id
    private Long id;
    @OneToOne
    private Freelancer freelancer;
}
