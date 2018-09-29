package com.example.service.gitter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "meta")
public class MessageResponse {
    private String id;
    private String text;
    private String html;
    private Date sent;
    private String editedAt;
    private UserResponse fromUser;

    @JsonProperty("unread")
    private Boolean unRead;
    private Long readBy;
    private List<Url> urls;
    private List<Mention> mentions;
    private List<Issue> issues;
    private List<Meta> meta;

    @JsonProperty("v")
    private Integer version;
}
