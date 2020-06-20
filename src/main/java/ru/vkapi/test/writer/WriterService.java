package ru.vkapi.test.writer;

import ru.vkapi.test.users.User;

import java.util.List;

public interface WriterService {
    void writeList(List<User> userList);
}
