package com.hackyeah.sl.backend.domain.mappers;

import com.hackyeah.sl.backend.domain.Position;
import com.hackyeah.sl.backend.domain.PositionDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PositionMapper {
    Position toEntity(PositionDto positionDto);

    PositionDto toDto(Position position);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Position partialUpdate(PositionDto positionDto, @MappingTarget Position position);
}