package org.thekiddos.faith.exceptions;

public class FreelancerNotFoundException extends RuntimeException {
    public FreelancerNotFoundException() {
        super( "Couldn't find the selected freelancer" );
    }
}
