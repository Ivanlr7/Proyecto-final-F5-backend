package dev.ivan.reviewverso_back.lists.service;

import dev.ivan.reviewverso_back.implementations.IListService;
import dev.ivan.reviewverso_back.lists.dtos.ListRequestDTO;
import dev.ivan.reviewverso_back.lists.dtos.ListResponseDTO;

import java.util.List;

public interface ListService extends IListService<ListResponseDTO, ListRequestDTO> {
    List<ListResponseDTO> getListsByUserId(Long userId);
}
