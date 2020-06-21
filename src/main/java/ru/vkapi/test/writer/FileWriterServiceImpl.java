package ru.vkapi.test.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vkapi.test.users.User;

import java.io.*;
import java.util.List;

public class FileWriterServiceImpl implements WriterService {
    private static final Logger logger = LoggerFactory.getLogger(FileWriterServiceImpl.class);
    private File file;

    public FileWriterServiceImpl(String absolutePathToFile) {
        this.file = new File(absolutePathToFile.endsWith(".txt") ? absolutePathToFile : absolutePathToFile + ".txt");
    }

    @Override
    public void writeList(List<User> userList) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (User user : userList) {
                writeUser(user,writer);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error("Ошибка записи ", e);
        }
    }

    private void writeUser(User user, Writer writer) throws IOException {
        writer.write(String.format("ID = %d, Name = %s, LastName = %s%n", user.getUserID(), user.getFirstName(), user.getLastName()));
    }
}
