package ru.vkapi.test.users;

import com.vk.api.sdk.objects.groups.UserXtrRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserMapper {
    @Mapping(source = "firstName" ,target = "firstName")
    @Mapping(source = "lastName" ,target = "lastName")
    @Mapping(source = "id" ,target = "userID")
    @Mapping(source = "city.title" ,target = "cityName")
    User toDomain(UserXtrRole user);

    List<User> toDomainList(List<UserXtrRole> user);


}
