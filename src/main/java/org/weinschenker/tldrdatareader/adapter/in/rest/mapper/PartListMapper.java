package org.weinschenker.tldrdatareader.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.weinschenker.tldrdatareader.adapter.in.rest.dto.gen.PartListDto;
import org.weinschenker.tldrdatareader.domain.PartList;

@Mapper
public interface PartListMapper {

    PartListMapper INSTANCE = Mappers.getMapper(PartListMapper.class);

    PartListDto toDto(PartList domain);

}
