package de.holisticon.tldrdatareader.infrastructure.rest.mapper;

import de.holisticon.tldrdatareader.domain.PartList;
import de.holisticon.tldrdatareaderinfrastructure.rest.dto.PartListDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PartListMapper {

    PartListMapper INSTANCE = Mappers.getMapper(PartListMapper.class);

    PartList toDomain(PartListDto dto);

    PartListDto toDto(PartList domain);

}
