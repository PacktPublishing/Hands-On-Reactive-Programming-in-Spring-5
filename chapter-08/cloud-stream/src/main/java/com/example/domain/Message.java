package com.example.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.mongodb.annotations.Immutable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@Immutable
@NoArgsConstructor
@AllArgsConstructor(staticName = "of", onConstructor = @__(@PersistenceConstructor))
public class Message implements Serializable {

    @Id
    @NonNull
    private String id;
    @NonNull
    private String text;
    @NonNull
    private String html;
    @NonNull
    private Date sent;
    @NonNull
    private User user;
    @NonNull
    private Boolean unread;
    @NonNull
    private Long readBy;
    @NonNull
    private String[] urls;
    @NonNull
    private Set<Mention> mentions;
    @NonNull
    private Set<Issue> issues;
}
