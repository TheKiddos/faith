package org.thekiddos.faith.models.api;

import lombok.Data;

// TODO: Maybe these stuff should be Dtos?
// TODO: Define Mapper?
@Data
public class Rateable {
    private Long id;
    private String name;
    private String type;
    private String url;
    private String averageRating;
}
