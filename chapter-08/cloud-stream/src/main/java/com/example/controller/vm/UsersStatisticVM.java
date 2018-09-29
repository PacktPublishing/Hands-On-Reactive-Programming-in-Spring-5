package com.example.controller.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersStatisticVM {
    private UserVM mostActive;

    private UserVM mostMentioned;
}
