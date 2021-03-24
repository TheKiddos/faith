package org.thekiddos.faith.services;

import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.models.User;

public interface FreelancerService {
    /**
     * Update freelancer profile details, does nothing when called on a non-freelancer user
     * @param user The freelancer user to update
     * @param dto The new profile details
     */
    void updateProfile( User user, FreelancerDto dto );
}
