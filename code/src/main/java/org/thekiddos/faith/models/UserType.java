package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class UserType {
    @Id @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @OneToOne(mappedBy = "type", optional = false)
    private User user;

    // TODO: since we never use the setter here except from User no need to fix bidirectional relation but in case in future we need to this comment is here
}
