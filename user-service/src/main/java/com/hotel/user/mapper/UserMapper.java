package com.hotel.user.mapper;

import com.hotel.common.dto.UserDto;
import com.hotel.user.entity.User;
import com.hotel.user.entity.UserRoleAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "roles", source = "roleAssignments", qualifiedByName = "mapRoles")
    UserDto toDto(User user);
    
    @Named("mapRoles")
    default Set<String> mapRoles(Set<UserRoleAssignment> roleAssignments) {
        if (roleAssignments == null) {
            return Set.of();
        }
        
        return roleAssignments.stream()
                .filter(UserRoleAssignment::getActive)
                .map(assignment -> assignment.getRole().name())
                .collect(Collectors.toSet());
    }
}
