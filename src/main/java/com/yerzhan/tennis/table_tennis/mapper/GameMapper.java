package com.yerzhan.tennis.table_tennis.mapper;

import com.yerzhan.tennis.table_tennis.dto.GameDTO;
import com.yerzhan.tennis.table_tennis.entity.Games;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface GameMapper {
    @Mapping(source = "player", target = "player")
    @Mapping(source = "opponent", target = "opponent")
    Games toEntity(GameDTO dto);

    @Mapping(source = "player", target = "player")
    @Mapping(source = "opponent", target = "opponent")
    GameDTO toDto(Games entity);

    List<GameDTO> toDtoList(List<Games> entities);
    
    List<Games> toEntityList(List<GameDTO> dtos);
} 