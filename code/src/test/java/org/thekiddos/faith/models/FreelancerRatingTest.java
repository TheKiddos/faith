package org.thekiddos.faith.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.api.Rateable;
import org.thekiddos.faith.repositories.FreelancerRatingRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.FreelancerRatingService;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.notNull;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class FreelancerRatingTest {
    private final FreelancerRatingService freelancerRatingService;
    private final UserRepository userRepository;
    private final FreelancerRatingRepository freelancerRatingRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    @MockBean
    private WebClient webClient;

    private User freelancerUser;

    @Autowired
    public FreelancerRatingTest( FreelancerRatingService freelancerRatingService, UserRepository userRepository, FreelancerRatingRepository freelancerRatingRepository ) {
        this.freelancerRatingService = freelancerRatingService;
        this.freelancerRatingRepository = freelancerRatingRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        freelancerRatingRepository.deleteAll();
        userRepository.deleteAll();
        this.freelancerUser = userRepository.save( getTestUser() );
    }

    /**
     * Test that when we try to get average rating for a freelancer that wasn't rated before we get an expected 0
     */
    @Test
    void getRatingWithNoPreviousRating() {
        assertEquals( 0.0, freelancerRatingService.getRating( (Freelancer) freelancerUser.getType() ) );
        Mockito.verify( webClient, Mockito.times( 0 ) ).get();
    }

    /**
     * Test that if we have a freelancer rating then it will be fetched using an api request
     */
    @Test
    void getRating() {
        // TODO: add more fields like url maybe?
        FreelancerRating freelancerRating = new FreelancerRating();
        freelancerRating.setId( 1L );
        freelancerRating.setFreelancer( (Freelancer) freelancerUser.getType() );
        freelancerRatingRepository.save( freelancerRating );

        Rateable rateable = new Rateable();
        rateable.setId( 1L );
        rateable.setAverageRating( "2.0" );

        // TODO: we can replace this with MockWebServer
        var uriSpecMock  = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        final var headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        final var responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when( webClient.get() ).thenReturn( uriSpecMock );
        Mockito.when( uriSpecMock.uri( ArgumentMatchers.any( URI.class ) ) ).thenReturn( headersSpecMock );
        Mockito.when( headersSpecMock.header( notNull(), notNull() ) ).thenReturn( headersSpecMock );
        Mockito.when( headersSpecMock.accept( notNull() ) ).thenReturn( headersSpecMock );
        Mockito.when( headersSpecMock.retrieve() ).thenReturn( responseSpecMock );
        Mockito.when( responseSpecMock.bodyToMono( ArgumentMatchers.<Class<Rateable>>notNull() ) )
                .thenReturn( Mono.just( rateable ) );

        assertEquals( 2.0, freelancerRatingService.getRating( (Freelancer) freelancerUser.getType() ) );
    }

    // TODO: test getRating404

    // TODO: move to utils and use in all other test with defaults and option to override them
    private User getTestUser() {
        UserDto userDto = UserDto.builder().email( "freelancer@test.com" )
                .password( "password" )
                .passwordConfirm( "password" )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( "Freelancer" )
                .build();

        return userMapper.userDtoToUser( userDto );
    }
}
