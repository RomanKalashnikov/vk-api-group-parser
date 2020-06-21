package ru.vkapi.test.users;

import com.vk.api.sdk.objects.groups.responses.GetMembersFieldsResponse;
import com.vk.api.sdk.queries.groups.GroupsGetMembersQueryWithFields;

import java.util.concurrent.Callable;

class TaskMemberQuery implements Callable<GetMembersFieldsResponse> {
    private final GroupsGetMembersQueryWithFields membersQueryWithFields;

    public TaskMemberQuery(GroupsGetMembersQueryWithFields membersQueryWithFields) {

        this.membersQueryWithFields = membersQueryWithFields;
    }

    @Override
    public GetMembersFieldsResponse call() throws Exception {
        return membersQueryWithFields.execute();
    }


}
