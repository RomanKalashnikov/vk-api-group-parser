package ru.vkapi.test.filter;

import ru.vkapi.test.users.User;

import java.util.List;

public interface UserListFilter {
    List<User> byCity(List<User> userList, String cityName);
}
