package ru.vkapi.test.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vkapi.test.users.User;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class FileWriterServiceImpl implements WriterService {
    private static final Logger logger = LoggerFactory.getLogger(FileWriterServiceImpl.class);
    private final Path path;

    public FileWriterServiceImpl(String absolutePathToFile) {
        this.path = Paths.get(absolutePathToFile);
    }

    @Override
    public void writeList(List<User> userList) {
        userList.forEach(this::writeUser);
    }

    private void writeUser(User user) {
        try {
            Files.writeString(path, String.format("ID = %d, Name = %s, LastName = %s%n", user.getUserID(), user.getFirstName(), user.getLastName()));
        } catch (IOException e) {
            logger.error("Не удалось записать пользователя: {}", user, e);
        }
    }
}
