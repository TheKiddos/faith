package org.thekiddos.faith.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.FreelancerRating;
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

    @Value( "${starat-token:Token}" )
    private String authHeader;

    @Autowired
    public FreelancerRatingStaRatService( FreelancerRatingRepository freelancerRatingRepository, WebClient webClient ) {
        this.freelancerRatingRepository = freelancerRatingRepository;
        this.webClient = webClient;
    }

    // TODO: if errors occurs return something
    @SneakyThrows
    @Override
    public double getRating( Freelancer freelancer ) {
        var id = getFreelancerRatingId( freelancer );
        if ( id == 0L )
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

    private Long getFreelancerRatingId( Freelancer freelancer ) {
        var freelancerRating = freelancerRatingRepository.findByFreelancer( freelancer ).orElse( null );
        if ( freelancerRating == null )
            return 0L;

        return freelancerRating.getId();
    }

    @SneakyThrows
    @Override
    public void rate( Freelancer freelancer, int stars ) {
        var id = getFreelancerRatingId( freelancer );
        if ( id == 0L ) {
            MultiValueMap<String, String> rateableBody = new LinkedMultiValueMap<>();
            rateableBody.add( "name", freelancer.getUser().getNickname() );
            rateableBody.add( "type", "freelancer" );

            var rateable = webClient.post()
                    .uri( new URI( StaRatURL.RATINGS_URL ) )
                    .header( "Authorization", authHeader )
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                    .accept( MediaType.APPLICATION_JSON )
                    .body( BodyInserters.fromFormData( rateableBody ) )
                    .retrieve()
                    .bodyToMono( Rateable.class )
                    .block();

            // TODO: Creating Logic should be handled by something else
            saveRatingInfo( freelancer, rateable );
        }

        stars = Math.min( MAX_RATING, Math.max( stars, MIN_RATING ) );

        MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
        bodyValues.add( "rateable", String.valueOf( id ) );
        bodyValues.add( "stars", String.valueOf( stars ) );

        webClient.post()
                .uri( new URI( StaRatURL.RATINGS_URL ) )
                .header( "Authorization", authHeader )
                .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                .accept( MediaType.APPLICATION_JSON )
                .body( BodyInserters.fromFormData( bodyValues ) )
                .retrieve()
                .bodyToMono( String.class )
                .block();
    }

    private void saveRatingInfo( Freelancer freelancer, Rateable rateable ) {
        FreelancerRating freelancerRating = new FreelancerRating();
        freelancerRating.setFreelancer( freelancer );
        freelancerRating.setId( rateable.getId() );
        freelancerRatingRepository.save( freelancerRating );
    }
}
