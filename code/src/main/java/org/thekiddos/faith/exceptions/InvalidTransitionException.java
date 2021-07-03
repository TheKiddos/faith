package org.thekiddos.faith.exceptions;

import org.thekiddos.faith.models.Status;

public class InvalidTransitionException extends RuntimeException {
    public InvalidTransitionException( Status from, Status to ) {
        super( "Can't switch from status " + from.name() + " to " + to.name() );
    }
}
