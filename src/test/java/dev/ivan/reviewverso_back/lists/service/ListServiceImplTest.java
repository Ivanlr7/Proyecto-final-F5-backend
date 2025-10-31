package dev.ivan.reviewverso_back.lists.service;

import dev.ivan.reviewverso_back.lists.ListEntity;
import dev.ivan.reviewverso_back.lists.ListRepository;
import dev.ivan.reviewverso_back.lists.dtos.*;
import dev.ivan.reviewverso_back.lists.exceptions.ListNotFoundException;
import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ListServiceImplTest {

    @Mock
    private ListRepository listRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ListMapper listMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ListServiceImpl listService;

    private UserEntity testUser;
    private ListEntity testList;
    private ListRequestDTO testRequestDTO;
    private ListResponseDTO testResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");

        testUser = UserEntity.builder()
                .idUser(1L)
                .userName("testuser")
                .build();

        testList = ListEntity.builder()
                .idList(1L)
                .user(testUser)
                .title("Mi Lista")
                .description("Descripción de prueba")
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ListItemDTO itemDTO = new ListItemDTO(ContentType.MOVIE, "550", ApiSource.TMDB);
        testRequestDTO = new ListRequestDTO(
                "Mi Lista",
                "Descripción de prueba",
                List.of(itemDTO)
        );

        ListItemResponseDTO itemResponseDTO = new ListItemResponseDTO(1L, ContentType.MOVIE, "550", ApiSource.TMDB, 0);
        testResponseDTO = new ListResponseDTO(
                1L,
                1L,
                "testuser",
                "Mi Lista",
                "Descripción de prueba",
                List.of(itemResponseDTO),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("createEntity crea y retorna la lista correctamente")
    void createEntity_createsListSuccessfully() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(testUser));
        when(listMapper.listRequestDtoToListEntity(testRequestDTO, testUser)).thenReturn(testList);
        when(listRepository.save(testList)).thenReturn(testList);
        when(listMapper.listEntityToListResponseDto(testList)).thenReturn(testResponseDTO);

        ListResponseDTO result = listService.createEntity(testRequestDTO);

        assertThat(result, is(testResponseDTO));
        assertThat(result.title(), is("Mi Lista"));
        assertThat(result.userName(), is("testuser"));
        verify(listRepository).save(testList);
        verify(listMapper).listEntityToListResponseDto(testList);
    }

    @Test
    @DisplayName("createEntity lanza UserNotFoundException si el usuario no existe")
    void createEntity_throwsUserNotFoundException() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> listService.createEntity(testRequestDTO));
        verify(listRepository, never()).save(any());
    }

    @Test
    @DisplayName("getEntities retorna lista de todas las listas")
    void getEntities_returnsAllLists() {
        List<ListEntity> lists = List.of(testList);
        when(listRepository.findAll()).thenReturn(lists);
        when(listMapper.listEntityToListResponseDto(testList)).thenReturn(testResponseDTO);

        List<ListResponseDTO> result = listService.getEntities();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(testResponseDTO));
        verify(listRepository).findAll();
    }

    @Test
    @DisplayName("getByID retorna la lista correctamente")
    void getByID_returnsListSuccessfully() {
        when(listRepository.findById(1L)).thenReturn(Optional.of(testList));
        when(listMapper.listEntityToListResponseDto(testList)).thenReturn(testResponseDTO);

        ListResponseDTO result = listService.getByID(1L);

        assertThat(result, is(testResponseDTO));
        assertThat(result.idList(), is(1L));
        verify(listRepository).findById(1L);
    }

    @Test
    @DisplayName("getByID lanza ListNotFoundException si la lista no existe")
    void getByID_throwsListNotFoundException() {
        when(listRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ListNotFoundException.class, () -> listService.getByID(99L));
        assertThat(ex.getMessage(), containsString("Lista no encontrada con id: 99"));
    }

    @Test
    @DisplayName("updateEntity actualiza la lista correctamente")
    void updateEntity_updatesListSuccessfully() {
        ListRequestDTO updateDTO = new ListRequestDTO(
                "Lista Actualizada",
                "Nueva descripción",
                new ArrayList<>()
        );

        when(listRepository.findById(1L)).thenReturn(Optional.of(testList));
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(testUser));
        when(listRepository.save(testList)).thenReturn(testList);
        when(listMapper.listEntityToListResponseDto(testList)).thenReturn(testResponseDTO);

        ListResponseDTO result = listService.updateEntity(1L, updateDTO);

        assertThat(result, is(notNullValue()));
        assertThat(testList.getTitle(), is("Lista Actualizada"));
        assertThat(testList.getDescription(), is("Nueva descripción"));
        verify(listRepository).save(testList);
    }

    @Test
    @DisplayName("updateEntity lanza ListNotFoundException si la lista no existe")
    void updateEntity_throwsListNotFoundException() {
        when(listRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ListNotFoundException.class, 
                () -> listService.updateEntity(99L, testRequestDTO));
        assertThat(ex.getMessage(), containsString("Lista no encontrada con id: 99"));
    }

    @Test
    @DisplayName("updateEntity lanza RuntimeException si el usuario no es el propietario")
    void updateEntity_throwsRuntimeExceptionIfNotOwner() {
        UserEntity otherUser = UserEntity.builder()
                .idUser(2L)
                .userName("otheruser")
                .build();

        when(listRepository.findById(1L)).thenReturn(Optional.of(testList));
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(otherUser));

        Exception ex = assertThrows(RuntimeException.class, 
                () -> listService.updateEntity(1L, testRequestDTO));
        assertThat(ex.getMessage(), containsString("No tienes permiso para editar esta lista"));
        verify(listRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateEntity actualiza items correctamente")
    void updateEntity_updatesItemsCorrectly() {
        ListItemDTO newItem = new ListItemDTO(ContentType.SERIES, "1399", ApiSource.TMDB);
        ListRequestDTO updateDTO = new ListRequestDTO(
                "Mi Lista",
                "Descripción de prueba",
                List.of(newItem)
        );

        when(listRepository.findById(1L)).thenReturn(Optional.of(testList));
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(testUser));
        when(listRepository.save(testList)).thenReturn(testList);
        when(listMapper.listEntityToListResponseDto(testList)).thenReturn(testResponseDTO);

        listService.updateEntity(1L, updateDTO);

        assertThat(testList.getItems(), hasSize(1));
        assertThat(testList.getItems().get(0).getContentType(), is(ContentType.SERIES));
        assertThat(testList.getItems().get(0).getContentId(), is("1399"));
        verify(listRepository).save(testList);
    }

    @Test
    @DisplayName("deleteEntity elimina la lista correctamente")
    void deleteEntity_deletesListSuccessfully() {
        when(listRepository.findById(1L)).thenReturn(Optional.of(testList));
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(testUser));

        listService.deleteEntity(1L);

        verify(listRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteEntity lanza ListNotFoundException si la lista no existe")
    void deleteEntity_throwsListNotFoundException() {
        when(listRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ListNotFoundException.class, () -> listService.deleteEntity(99L));
        assertThat(ex.getMessage(), containsString("Lista no encontrada con id: 99"));
        verify(listRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteEntity lanza RuntimeException si el usuario no es el propietario")
    void deleteEntity_throwsRuntimeExceptionIfNotOwner() {
        UserEntity otherUser = UserEntity.builder()
                .idUser(2L)
                .userName("otheruser")
                .build();

        when(listRepository.findById(1L)).thenReturn(Optional.of(testList));
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(otherUser));

        Exception ex = assertThrows(RuntimeException.class, () -> listService.deleteEntity(1L));
        assertThat(ex.getMessage(), containsString("No tienes permiso para eliminar esta lista"));
        verify(listRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getListsByUserId retorna listas del usuario")
    void getListsByUserId_returnsUserLists() {
        List<ListEntity> userLists = List.of(testList);
        when(listRepository.findByUserId(1L)).thenReturn(userLists);
        when(listMapper.listEntityToListResponseDto(testList)).thenReturn(testResponseDTO);

        List<ListResponseDTO> result = listService.getListsByUserId(1L);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).userId(), is(1L));
        verify(listRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("getListsByUserId retorna lista vacía si el usuario no tiene listas")
    void getListsByUserId_returnsEmptyListIfNoLists() {
        when(listRepository.findByUserId(99L)).thenReturn(new ArrayList<>());

        List<ListResponseDTO> result = listService.getListsByUserId(99L);

        assertThat(result, is(empty()));
        verify(listRepository).findByUserId(99L);
    }
}
