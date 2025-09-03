package com.hotel.room.mapper;

import com.hotel.room.entity.Booking;
import com.hotel.common.dto.BookingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for Booking entity and BookingDto conversion
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BookingMapper {
    
    /**
     * Convert Booking entity to BookingDto
     * @param booking Booking entity
     * @return BookingDto
     */
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.roomNumber", target = "roomNumber")
    @Mapping(source = "room.roomType", target = "roomType")
    BookingDto toDto(Booking booking);
    
    /**
     * Convert BookingDto to Booking entity
     * @param bookingDto BookingDto
     * @return Booking entity
     */
    @Mapping(target = "room", ignore = true) // Room will be set by service layer
    @Mapping(target = "numberOfNights", ignore = true) // Calculated in @PrePersist
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Booking toEntity(BookingDto bookingDto);
    
    /**
     * Convert list of Booking entities to list of BookingDtos
     * @param bookings List of Booking entities
     * @return List of BookingDtos
     */
    List<BookingDto> toDtoList(List<Booking> bookings);
    
    /**
     * Convert list of BookingDtos to list of Booking entities
     * @param bookingDtos List of BookingDtos
     * @return List of Booking entities
     */
    List<Booking> toEntityList(List<BookingDto> bookingDtos);
    
    /**
     * Update existing Booking entity with data from BookingDto
     * @param bookingDto Source BookingDto
     * @param booking Target Booking entity to update
     */
    @Mapping(target = "room", ignore = true) // Room should not be updated via this method
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(BookingDto bookingDto, @MappingTarget Booking booking);
}
