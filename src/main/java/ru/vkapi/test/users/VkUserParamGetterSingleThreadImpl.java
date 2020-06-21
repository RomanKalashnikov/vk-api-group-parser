package ru.vkapi.test.users;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.UserXtrRole;
import com.vk.api.sdk.objects.groups.responses.GetMembersFieldsResponse;
import com.vk.api.sdk.objects.users.Fields;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class VkUserParamGetterSingleThreadImpl implements UserParamGetter {
    private static final Logger logger = LoggerFactory.getLogger(VkUserParamGetterSingleThreadImpl.class);
    private static final Integer USER_ID = 16337119;
    private static final String ACCESS_TOKEN = "649ca9e0649ca9e0649ca9e09064eee58e6649c649ca9e03a4e8a1e7164d7957913cba6";
    private static final int MAX_COUNT_MEMBERS_FOR_REQUEST = 1000;
    private static final int MAX_COUNT_ERRORS = 5;

    private VkApiClient vkApiClient;
    private UserActor userActor;

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public VkUserParamGetterSingleThreadImpl() {
        vkApiClient = new VkApiClient(HttpTransportClient.getInstance());
        logger.info("Создано подключение к VkApiClient");
        userActor = new UserActor(USER_ID, ACCESS_TOKEN);
        logger.info("Получен UserActor");
    }

    @Override
    public List<User> getUserList(String group) {
        logger.info("Попытка получение ответа от VK");
        return userMapper.toDomainList(getUserXtrRoles(group));
    }

    private List<UserXtrRole> getUserXtrRoles(String group) {
        int allCountMembersInGroup = 1;
        int countErrors = 0;
        List<UserXtrRole> membersList = new ArrayList<>();
        int offset = 0;
        do {
            final GetMembersFieldsResponse userSubList = getUserList(group, offset);
            if (userSubList == null) {
                if (countErrors > MAX_COUNT_ERRORS) {
                    break;
                }
                countErrors++;
                continue;
            }

            allCountMembersInGroup = userSubList.getCount();

            membersList.addAll(userSubList.getItems());
            offset += MAX_COUNT_MEMBERS_FOR_REQUEST;
        } while (allCountMembersInGroup >= offset);
        return membersList;
    }

    private GetMembersFieldsResponse getUserList(String group, Integer offset) {
        logger.info("Попытка получение ответа от VK");
        GetMembersFieldsResponse getMembers = null;
        try {
            getMembers = vkApiClient
                    .groups()
                    .getMembersWithFields(userActor, Fields.CITY)
                    .groupId(group)
                    .offset(offset)
                    .count(MAX_COUNT_MEMBERS_FOR_REQUEST)
                    .execute();
        } catch (ApiException | ClientException e) {
            logger.info("Ошибка на стороне клиента или Api - ".concat(e.getMessage()));
        }
        return getMembers;
    }
}
