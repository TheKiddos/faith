package org.thekiddos.faith.exceptions;

public class ProposalNotFoundException extends RuntimeException {
    public ProposalNotFoundException() {
        super( "Couldn't find the selected proposal" );
    }
}
