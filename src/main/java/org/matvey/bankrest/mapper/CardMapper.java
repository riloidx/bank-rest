package org.matvey.bankrest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.matvey.bankrest.dto.request.CardRequestDto;
import org.matvey.bankrest.dto.response.CardResponseDto;
import org.matvey.bankrest.entity.Card;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {
    Card toEntity(CardRequestDto cardRequestDto);
    //CardResponseDto toDto(Card card);
}
