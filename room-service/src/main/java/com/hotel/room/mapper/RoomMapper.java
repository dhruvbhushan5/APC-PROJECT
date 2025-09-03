package com.hotel.room.mapper;

import com.hotel.room.entity.Room;
import com.hotel.common.dto.RoomDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for Room entity and RoomDto conversion
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RoomMapper {
    
    /**
     * Convert Room entity to RoomDto
     * @param room Room entity
     * @return RoomDto
     */
    RoomDto toDto(Room room);
    
    /**
     * Convert RoomDto to Room entity
     * @param roomDto RoomDto
     * @return Room entity
     */
    Room toEntity(RoomDto roomDto);
    
    /**
     * Convert list of Room entities to list of RoomDtos
     * @param rooms List of Room entities
     * @return List of RoomDtos
     */
    List<RoomDto> toDtoList(List<Room> rooms);
    
    /**
     * Convert list of RoomDtos to list of Room entities
     * @param roomDtos List of RoomDtos
     * @return List of Room entities
     */
    List<Room> toEntityList(List<RoomDto> roomDtos);
    
    /**
     * Update existing Room entity with data from RoomDto
     * @param roomDto Source RoomDto
     * @param room Target Room entity to update
     */
    void updateEntityFromDto(RoomDto roomDto, @MappingTarget Room room);
}
