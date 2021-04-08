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
}
