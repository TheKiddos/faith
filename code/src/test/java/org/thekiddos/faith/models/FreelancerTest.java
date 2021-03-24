package org.thekiddos.faith.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.repositories.SkillRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.FreelancerService;
import org.thekiddos.faith.services.UserService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class FreelancerTest {
    private final FreelancerService freelancerService;
    private final SkillRepository skillRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public FreelancerTest( FreelancerService freelancerService, SkillRepository skillRepository, UserService userService, UserRepository userRepository ) {
        this.freelancerService = freelancerService;
        this.skillRepository = skillRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        try {
            userRepository.deleteById( "bhbh@gmail.com" );
        }
        catch ( EmptyResultDataAccessException e ) {
            // ignore
        }
    }

    @AfterEach
    void tearDown() {
        try {
            userRepository.deleteById( "bhbh@gmail.com" );
        }
        catch ( EmptyResultDataAccessException e ) {
            // ignore
        }
    }

    @Test
    void updateFreelancerProfile() {
        User user = userService.createUser( UserDto.builder()
                .email( "bhbh@gmail.com" )
                .password( "password" )
                .nickname( "bhbhbh" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        FreelancerDto dto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        Freelancer freelancer = (Freelancer) user.getType();
        assertNotEquals( dto.getSummary(), freelancer.getSummary() );
        assertNotEquals( dto.isAvailable(), freelancer.isAvailable() );
        assertTrue( freelancer.getSkills().isEmpty() );

        freelancerService.updateProfile( user, dto );

        user = (User) userService.loadUserByUsername( user.getEmail() );
        freelancer = (Freelancer) user.getType();
        assertEquals( dto.getSummary(), freelancer.getSummary() );
        assertEquals( dto.isAvailable(), freelancer.isAvailable() );
        assertEquals( Set.of( Skill.of( "c++" ), Skill.of( "suck" ) ), freelancer.getSkills() );
    }

    @Test
    void updateFreelancerProfileForNonFreelancer() {
        User user = userService.createUser( UserDto.builder()
                .email( "bhbh@gmail.com" )
                .password( "password" )
                .nickname( "bhbhbh" )
                .type( "Stakeholder" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        FreelancerDto dto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        freelancerService.updateProfile( user, dto );

        user = (User) userService.loadUserByUsername( user.getEmail() );
        assertFalse( user.getType() instanceof Freelancer );
    }
}
