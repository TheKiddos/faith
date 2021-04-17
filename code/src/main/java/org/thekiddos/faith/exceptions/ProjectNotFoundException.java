package org.thekiddos.faith.exceptions;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException() {
        super( "Couldn't find the selected project" );
    }
}
