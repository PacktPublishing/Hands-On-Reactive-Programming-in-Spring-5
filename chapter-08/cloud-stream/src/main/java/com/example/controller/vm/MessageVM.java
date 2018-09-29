package com.example.controller.vm;

import java.util.Date;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  MessageVM {
    @NotNull
    @NonNull
    private String id;
    @NotNull
    @NonNull
    private String text;
    @NotNull
    @NonNull
    private String html;
    @NonNull
    @NotNull
    private String username;
    @NonNull
    @NotNull
    private String userAvatarUrl;
    @NotNull
    @NonNull
    private Date sent;
}
