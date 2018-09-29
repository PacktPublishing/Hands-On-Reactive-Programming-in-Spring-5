package com.example.service.impl.utils;

import com.example.controller.vm.UserVM;
import com.example.domain.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserVM toViewModelUnits(User domainUser) {
        if (domainUser == null) {
            return null;
        }

        return new UserVM(domainUser.getId(), domainUser.getName());
    }
}
