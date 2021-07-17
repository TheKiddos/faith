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
        var freelancerRating = freelancerRatingRepository.findByFreelancer( freelancer ).orElse( null );
        if ( freelancerRating == null )
            return 0;

        var id = freelancerRating.getId();
        Rateable rateable = webClient.get()
                .uri( new URI( StaRatURL.getRateableURL( id ) ) )
                .header( "Authorization", authHeader )
                .accept( MediaType.APPLICATION_JSON )
                .retrieve()
                .bodyToMono( Rateable.class )
                .block();

        return Double.parseDouble( Objects.requireNonNull( rateable ).getAverageRating() );
    }
}
