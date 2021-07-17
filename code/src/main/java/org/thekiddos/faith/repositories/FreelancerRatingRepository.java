package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.FreelancerRating;

import java.util.Optional;

@Repository
public interface FreelancerRatingRepository extends JpaRepository<FreelancerRating, Long> {
    Optional<FreelancerRating> findByFreelancer( Freelancer freelancer );
}
