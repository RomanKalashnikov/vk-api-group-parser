package ru.vkapi.test;

import ru.vkapi.test.filter.ArgumentsFilter;
import ru.vkapi.test.filter.StreamUserListFilter;
import ru.vkapi.test.filter.UserListFilter;
import ru.vkapi.test.users.User;
import ru.vkapi.test.users.UserParamGetter;
import ru.vkapi.test.users.VkUserParamGetterMultithreadedImpl;
import ru.vkapi.test.users.VkUserParamGetterSingleThreadImpl;
import ru.vkapi.test.writer.FileWriterServiceImpl;
import ru.vkapi.test.writer.WriterService;

import java.util.List;

class Main {
    private final UserParamGetter paramGetter;
    private final UserListFilter listFilter;
    private final WriterService writerService;

    private static final String GROUP = "javarush";
//        private static final String GROUP = "rostelecom.career";
    private static final String CITY_NAME = "Novosibirsk";


    private Main(UserParamGetter paramGetter, UserListFilter listFilter, WriterService writerService) {
        this.paramGetter = paramGetter;
        this.listFilter = listFilter;
        this.writerService = writerService;
    }

    public static void main(String[] args) {
        ArgumentsFilter filter = new ArgumentsFilter(args);
        filter.checkSizeArgs();
        UserParamGetter paramGetter;
        if (filter.checkMultiThreadOption()) {
            paramGetter = new VkUserParamGetterMultithreadedImpl(filter.getNumberThreadPool());
        } else {
            paramGetter = new VkUserParamGetterSingleThreadImpl();
        }
        UserListFilter listFilter = new StreamUserListFilter();
        WriterService writerService = new FileWriterServiceImpl(filter.getPathToFile());

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

