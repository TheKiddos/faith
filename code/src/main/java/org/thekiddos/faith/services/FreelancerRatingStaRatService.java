package org.thekiddos.faith.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.api.Rateable;
import org.thekiddos.faith.repositories.FreelancerRatingRepository;
import org.thekiddos.faith.utils.StaRatURL;

import java.net.URI;
import java.util.Objects;

@Service
public class FreelancerRatingStaRatService implements FreelancerRatingService {
    private final FreelancerRatingRepository freelancerRatingRepository;
    private final WebClient webClient;
    
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 10;
    
    @Value( "${starat-token}" )
    private String authHeader;

    @Autowired
    public FreelancerRatingStaRatService( FreelancerRatingRepository freelancerRatingRepository, WebClient webClient ) {
        this.freelancerRatingRepository = freelancerRatingRepository;
        this.webClient = webClient;
    }

    @SneakyThrows
    @Override
    public double getRating( Freelancer freelancer ) {
        var id = getFreelancerRatingId( freelancer );
        if ( id == 0 )
            return 0;
            
        Rateable rateable = webClient.get()
                .uri( new URI( StaRatURL.getRateableURL( id ) ) )
                .header( "Authorization", authHeader )
                .accept( MediaType.APPLICATION_JSON )
                .retrieve()
                .bodyToMono( Rateable.class )
                .block();

        return Double.parseDouble( Objects.requireNonNull( rateable ).getAverageRating() );
    }
    
    private int getFreelancerRatingId( Freelancer freelancer ) {
        var freelancerRating = freelancerRatingRepository.findByFreelancer( freelancer ).orElse( null );
        if ( freelancerRating == null )
            return 0;
        
        return freelancerRating.getId();
    }
    
    @Override
    public void rate( Freelancer freelancer, int stars ) {
        var id = getFreelancerRatingId( freelancer );
        stars = Math.min( MAX_RATING, Math.max( stars, MIN_RATING ) );
        
        MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
        bodyValues.add( "rateable", "value" );
        bodyValues.add( "stars", String.valueOf( stars ) );
        
        client.post()
            .uri( new URI( StaRatURL.RATINGS_URL ) )
            .header( "Authorization", authHeader )
            .contentType( MediaType.APPLICATION_FORM_URLENCODED )
            .accept( MediaType.APPLICATION_JSON )
            .body( BodyInserters.fromFormData( bodyValues ) )
            .retrieve()
            .bodyToMono( String.class )
            .block();
    }
}
