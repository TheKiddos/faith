package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
public class Email {
    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;

    @NotEmpty @NotNull
    private String subject;
    @javax.validation.constraints.Email @NotEmpty @NotNull
    @Column(name = "sender_email")
    private String from;
    @javax.validation.constraints.Email @NotEmpty @NotNull
    @Column(name = "receiver_email")
    private String to;
    private String templateName;
    @NotNull
    private LocalDateTime timestamp;
}
