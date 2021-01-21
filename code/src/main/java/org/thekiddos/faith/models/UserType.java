package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "type")
    private User user;
}
