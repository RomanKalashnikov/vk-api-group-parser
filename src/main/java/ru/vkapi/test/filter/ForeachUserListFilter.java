package ru.vkapi.test.filter;

import ru.vkapi.test.users.User;

import java.util.ArrayList;
import java.util.List;

public class ForeachUserListFilter implements UserListFilter {

    @Override
    public List<User> byCity(List<User> userList, String cityName) {
        List<User> list = new ArrayList<>();
        for (User user : userList) {
            if (cityName.equalsIgnoreCase(user.getCityName()) && user.getFirstName() != null && user.getLastName() != null) {
                list.add(user);
            }
        }
        return list;
    }
}
