package org.thekiddos.faith.exceptions;

public class BidNotFoundException extends RuntimeException {
    public BidNotFoundException() {
        super( "No Such Bid" );
    }
}
