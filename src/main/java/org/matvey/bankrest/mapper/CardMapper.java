package org.matvey.bankrest.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.matvey.bankrest.dto.request.CardRequestDto;
import org.matvey.bankrest.dto.request.CardUpdateDto;
import org.matvey.bankrest.dto.response.CardResponseDto;
import org.matvey.bankrest.entity.Card;
import org.matvey.bankrest.util.CardUtils;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardNumber", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Card toEntity(CardRequestDto cardRequestDto);

    @Mapping(target = "maskedCardNumber", source = "cardNumber", qualifiedByName = "maskCardNumber")
    @Mapping(target = "owner", source = "owner")
    CardResponseDto toDto(Card card);

    List<CardResponseDto> toDto(List<Card> cards);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardNumber", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateEntityFromDto(CardUpdateDto cardUpdateDto, @MappingTarget Card card);

    @Named("maskCardNumber")
    default String maskCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }

        CardUtils cardUtils = new CardUtils();
        return cardUtils.maskCardNumber(cardUtils.decryptCardNumber(cardNumber));
    }
}
