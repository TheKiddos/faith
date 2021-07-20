package org.thekiddos.faith.services;

import org.thekiddos.faith.models.Freelancer;

public interface FreelancerRatingService {
    double getRating( Freelancer freelancer );
    void rate( Freelancer freelancer, int rating );
}
