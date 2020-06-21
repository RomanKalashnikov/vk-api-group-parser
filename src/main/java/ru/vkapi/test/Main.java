package ru.vkapi.test;

import ru.vkapi.test.filter.StreamUserListFilter;
import ru.vkapi.test.filter.UserListFilter;
import ru.vkapi.test.users.User;
import ru.vkapi.test.users.UserParamGetter;
import ru.vkapi.test.users.VkUserParamGetterImpl;
import ru.vkapi.test.writer.FileWriterServiceImpl;
import ru.vkapi.test.writer.WriterService;

import java.util.List;

public class Main {
    private UserParamGetter paramGetter;
    private UserListFilter listFilter;
    private WriterService writerService;

//            private static final String GROUP = "javarush";

    private static final String GROUP = "rostelecom.career";
    private static final String CITY_NAME = "Novosibirsk";

    private Main(UserParamGetter paramGetter, UserListFilter listFilter, WriterService writerService) {
        this.paramGetter = paramGetter;
        this.listFilter = listFilter;
        this.writerService = writerService;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("Длинна аргумента неверная");
        }
        final String pathToFile = args[0];
        UserParamGetter paramGetter = new VkUserParamGetterImpl();
        UserListFilter listFilter = new StreamUserListFilter();
        WriterService writerService = new FileWriterServiceImpl(pathToFile);

        new Main(paramGetter, listFilter, writerService).run();

    }

    private void run() {
        final long start = System.currentTimeMillis();
        List<User> userList = paramGetter.getUserList(GROUP);
        final long stop = System.currentTimeMillis();
        final List<User> filteredUsersByCity = listFilter.byCity(userList, CITY_NAME);

        writerService.writeList(filteredUsersByCity);
        System.out.println(stop - start);
    }

}

