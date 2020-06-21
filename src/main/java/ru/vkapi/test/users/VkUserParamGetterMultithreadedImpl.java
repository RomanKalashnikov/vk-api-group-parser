package ru.vkapi.test.users;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.groups.UserXtrRole;
import com.vk.api.sdk.objects.groups.responses.GetMembersFieldsResponse;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.queries.groups.GroupsGetByIdQuery;
import com.vk.api.sdk.queries.groups.GroupsGetMembersQueryWithFields;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VkUserParamGetterMultithreadedImpl implements UserParamGetter {
    private static final Logger logger = LoggerFactory.getLogger(VkUserParamGetterMultithreadedImpl.class);
    private static final Integer USER_ID = 16337119;
    private static final String ACCESS_TOKEN = "649ca9e0649ca9e0649ca9e09064eee58e6649c649ca9e03a4e8a1e7164d7957913cba6";
    private static final int MAX_COUNT_MEMBERS_FOR_REQUEST = 1000;

    private ExecutorService executor ;
    private VkApiClient vkApiClient;
    private UserActor userActor;

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public VkUserParamGetterMultithreadedImpl(int numberFixedThreadPool) {
        vkApiClient = new VkApiClient(HttpTransportClient.getInstance());
        logger.info("Создано подключение к VkApiClient");
        userActor = new UserActor(USER_ID, ACCESS_TOKEN);
        logger.info("Получен UserActor");
        executor = Executors.newFixedThreadPool(numberFixedThreadPool);
    }

    @Override
    public List<User> getUserList(String group) {
        logger.info("Попытка получение ответа от VK");
        return userMapper.toDomainList(getUserXtrRoles(group));
    }

    private List<UserXtrRole> getUserXtrRoles(String group) {
//       оформляем запрос и получам количество участников группы
        int number = getMemberCountInGroup(group);
//        распределяем количество задач отталкиваясь от количества участников
        final ArrayList<Future<GetMembersFieldsResponse>> futures = prepareFutureRequest(group, number);
//        ожидаем выполнения всех задач
        waitAllTask(futures);
//        складываем результат всех задач в лист
        List<GetMembersFieldsResponse> fieldsResponseSet = getGetMembersFieldsResponses(futures);
//        завершаем работу Executor-а
        executor.shutdown();

        return getUserXtrRoles(fieldsResponseSet);
    }

    private List<UserXtrRole> getUserXtrRoles(List<GetMembersFieldsResponse> fieldsResponseList) {
        List<UserXtrRole> usersItem = new ArrayList<>();
        fieldsResponseList.stream().map(GetMembersFieldsResponse::getItems).forEach(usersItem::addAll);
        return usersItem;
    }

    private List<GetMembersFieldsResponse> getGetMembersFieldsResponses(ArrayList<Future<GetMembersFieldsResponse>> futures) {
        List<GetMembersFieldsResponse> fieldsResponseList = new ArrayList<>();
        futures.forEach(future -> {
            try {
                fieldsResponseList.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return fieldsResponseList;
    }

    private void waitAllTask(ArrayList<Future<GetMembersFieldsResponse>> futures) {
        while (futures.stream().anyMatch(f -> !f.isDone())){
            logger.info("Ожидаем выполнения всех задач futures");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<Future<GetMembersFieldsResponse>> prepareFutureRequest(String group, int number) {
        final ArrayList<Future<GetMembersFieldsResponse>> futures = new ArrayList<>();

        int numberOffsetTask = number/MAX_COUNT_MEMBERS_FOR_REQUEST;
        for (int i = 0; i <= numberOffsetTask; i++) {
            TaskMemberQuery task = new TaskMemberQuery(getMembersQueryWithFields(group, i * MAX_COUNT_MEMBERS_FOR_REQUEST));
            futures.add(executor.submit(task));
        }
        return futures;
    }

    private int getMemberCountInGroup(String group) {
        final GroupsGetByIdQuery query = vkApiClient.groups()
                .getById(userActor)
                .groupId(group)
                .fields(com.vk.api.sdk.objects.groups.Fields.MEMBERS_COUNT);
        int number = 0;

        try {
            final List<GroupFull> execute = query.execute();

             number = execute.get(0).getMembersCount();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        return number;
    }

    private GroupsGetMembersQueryWithFields getMembersQueryWithFields(String group, Integer offset) {
        return vkApiClient
                .groups()
                .getMembersWithFields(userActor, Fields.CITY)
                .groupId(group)
                .offset(offset)
                .count(MAX_COUNT_MEMBERS_FOR_REQUEST);
    }
}
