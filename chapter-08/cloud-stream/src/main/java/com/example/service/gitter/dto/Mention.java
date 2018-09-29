package com.example.service.gitter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mention {
    private String screenName;
    private String userId;
    private List<String> userIds;
}
