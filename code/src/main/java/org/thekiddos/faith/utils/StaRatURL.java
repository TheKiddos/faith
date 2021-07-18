package org.thekiddos.faith.utils;

public final class StaRatURL {
    public static final String STARAT_API_BASE_URL = "https://starat.herokuapp.com/api/";
    public static final String RATEABLE_BASE_URL = STARAT_API_BASE_URL + "rateables/";
    public static final String RATINGS_URL = STARAT_API_BASE_URL + "ratings/";
    
    
    public static String getRateableURL( Long id ) {
        return RATEABLE_BASE_URL + id;
    }
}
