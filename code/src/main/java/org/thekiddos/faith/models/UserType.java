package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class UserType {
    @Id @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @OneToOne(mappedBy = "type", optional = false)
    private User user;

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        UserType userType = (UserType) o;
        return user.equals( userType.user );
    }

    @Override
    public int hashCode() {
        return Objects.hash( user );
    }


    // TODO: since we never use the setter here except from User no need to fix bidirectional relation but in case in future we need to this comment is here
}
