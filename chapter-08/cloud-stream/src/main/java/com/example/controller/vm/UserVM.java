package com.example.controller.vm;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVM {
    @NotNull
    @NonNull
    private String id;
    @NotNull
    @NonNull
    private String name;
}
