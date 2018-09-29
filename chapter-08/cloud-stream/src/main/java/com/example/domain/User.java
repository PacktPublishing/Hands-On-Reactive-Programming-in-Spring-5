package com.example.domain;

import java.io.Serializable;

import com.mongodb.annotations.Immutable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
@Immutable
@NoArgsConstructor
@AllArgsConstructor(staticName = "of", onConstructor = @__(@PersistenceConstructor))
public class User implements Serializable {
    @Id
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String displayName;
}
