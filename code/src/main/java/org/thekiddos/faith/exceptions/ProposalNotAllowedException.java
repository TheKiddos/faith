package org.thekiddos.faith.exceptions;

public class ProposalNotAllowedException extends RuntimeException {
    public ProposalNotAllowedException() {
        super( "You can't propose now" );
    }
}
