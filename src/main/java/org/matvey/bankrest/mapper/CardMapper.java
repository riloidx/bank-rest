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

/**
 * Mapper для преобразования между сущностями Card и DTO.
 * Использует MapStruct для автоматической генерации кода маппинга.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface CardMapper {

    /**
     * Преобразует DTO запроса создания карты в сущность Card.
     *
     * @param cardRequestDto DTO с данными для создания карты
     * @return сущность Card
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardNumber", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Card toEntity(CardRequestDto cardRequestDto);

    /**
     * Преобразует сущность Card в DTO ответа.
     *
     * @param card сущность карты
     * @return DTO ответа с информацией о карте
     */
    @Mapping(target = "maskedCardNumber", source = "cardNumber", qualifiedByName = "maskCardNumber")
    @Mapping(target = "owner", source = "owner")
    CardResponseDto toDto(Card card);

    /**
     * Преобразует список сущностей Card в список DTO ответов.
     *
     * @param cards список сущностей карт
     * @return список DTO ответов
     */
    List<CardResponseDto> toDto(List<Card> cards);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardNumber", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateEntityFromDto(CardUpdateDto cardUpdateDto, @MappingTarget Card card);

    /**
     * Маскирует номер карты для безопасного отображения.
     *
     * @param cardNumber зашифрованный номер карты
     * @return замаскированный номер карты
     */
    @Named("maskCardNumber")
    default String maskCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }

        CardUtils cardUtils = new CardUtils();
        return cardUtils.maskCardNumber(cardUtils.decryptCardNumber(cardNumber));
    }
}
