package com.example.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "issues")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of", onConstructor = @__(@PersistenceConstructor))
public class Issue implements Serializable {

    @NonNull
    private Long id;
}
