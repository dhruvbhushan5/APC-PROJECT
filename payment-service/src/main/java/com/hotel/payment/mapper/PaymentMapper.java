package com.hotel.payment.mapper;

import com.hotel.payment.entity.Payment;
import com.hotel.common.dto.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for Payment entity and PaymentDto conversion
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PaymentMapper {
    
    /**
     * Convert Payment entity to PaymentDto
     * @param payment Payment entity
     * @return PaymentDto
     */
    PaymentDto toDto(Payment payment);
    
    /**
     * Convert PaymentDto to Payment entity
     * @param paymentDto PaymentDto
     * @return Payment entity
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Payment toEntity(PaymentDto paymentDto);
    
    /**
     * Convert list of Payment entities to list of PaymentDtos
     * @param payments List of Payment entities
     * @return List of PaymentDtos
     */
    List<PaymentDto> toDtoList(List<Payment> payments);
    
    /**
     * Convert list of PaymentDtos to list of Payment entities
     * @param paymentDtos List of PaymentDtos
     * @return List of Payment entities
     */
    List<Payment> toEntityList(List<PaymentDto> paymentDtos);
    
    /**
     * Update existing Payment entity with data from PaymentDto
     * @param paymentDto Source PaymentDto
     * @param payment Target Payment entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(PaymentDto paymentDto, @MappingTarget Payment payment);
}
