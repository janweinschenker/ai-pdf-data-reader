package org.weinschenker.tldrdatareader.infrastructure.rest.mapper;

import org.weinschenker.tldrdatareader.domain.PartList;
import org.weinschenker.tldrdatareader.infrastructure.rest.dto.PartListDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PartListMapper {

    PartListMapper INSTANCE = Mappers.getMapper(PartListMapper.class);

    PartListDto toDto(PartList domain);

}
