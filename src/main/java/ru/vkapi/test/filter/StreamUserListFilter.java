package ru.vkapi.test.filter;

import ru.vkapi.test.users.User;

import java.util.List;
import java.util.stream.Collectors;

public class StreamUserListFilter implements UserListFilter {

    @Override
    public List<User> byCity(List<User> userList, String cityName) {
        return userList.stream()
                .filter(user -> cityName.equalsIgnoreCase(user.getCityName()) && user.getFirstName() != null && user.getLastName() != null)
                .collect(Collectors.toList());
    }
}
