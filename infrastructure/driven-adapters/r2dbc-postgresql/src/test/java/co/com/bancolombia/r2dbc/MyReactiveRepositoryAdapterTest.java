package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.adapter.UserReactiveRepositoryAdapter;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.mapper.entity.UserEntityMapper;
import co.com.bancolombia.r2dbc.repository.UserReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository repository;

    @Mock
    private UserEntityMapper entityMapper;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator tx;

    private UserReactiveRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        lenient()
            .when(tx.transactional(any(Mono.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        lenient()
            .when(tx.transactional(any(Flux.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        adapter = new UserReactiveRepositoryAdapter(repository, mapper, entityMapper, tx);
    }

    @Test
    void save_successfully_maps_and_persists_user() {
        // Given
        User inputUser = User.builder()
            .name("Juan")
            .lastname("Pérez")
            .email("juan@example.com")
            .baseSalary(1_000_000)
            .roleId(1L)
            .build();

        UserEntity entityToPersist = UserEntity.builder()
            .name("Juan")
            .lastname("Pérez")
            .email("juan@example.com")
            .baseSalary(1_000_000)
            .roleId(1L)
            .build();

        UserEntity persistedEntity = UserEntity.builder()
            .id(10L)
            .name("Juan")
            .lastname("Pérez")
            .email("juan@example.com")
            .baseSalary(1_000_000)
            .roleId(1L)
            .build();

        User expectedUser = inputUser.toBuilder().id(10L).build();

        // When
        when(entityMapper.toEntity(inputUser)).thenReturn(entityToPersist);
        when(repository.save(entityToPersist)).thenReturn(Mono.just(persistedEntity));
        when(entityMapper.toDomain(persistedEntity)).thenReturn(expectedUser);

        // Then
        StepVerifier.create(adapter.save(inputUser))
            .expectNext(expectedUser)
            .verifyComplete();

        // Verify
        verify(entityMapper).toEntity(inputUser);
        verify(repository).save(entityToPersist);
        verify(entityMapper).toDomain(persistedEntity);
        verify(tx).transactional(any(Mono.class));
    }

    @Test
    void save_should_emit_error_when_repository_fails() {
        User inputUser = User.builder()
            .name("Juan")
            .lastname("Pérez")
            .email("juan@example.com")
            .baseSalary(1_000_000)
            .roleId(1L)
            .build();
        UserEntity entityToPersist = UserEntity.builder()
            .name("Juan")
            .lastname("Pérez")
            .email("juan@example.com")
            .baseSalary(1_000_000)
            .roleId(1L)
            .build();
        when(entityMapper.toEntity(inputUser)).thenReturn(entityToPersist);
        when(repository.save(entityToPersist)).thenReturn(Mono.error(new RuntimeException("DB error")));
        StepVerifier.create(adapter.save(inputUser))
            .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("DB error"))
            .verify();
        verify(entityMapper).toEntity(inputUser);
        verify(repository).save(entityToPersist);
        verify(tx).transactional(any(Mono.class));
    }

    @Test
    void existsByEmail_should_return_true_when_email_exists() {
        String email = "test@correo.com";
        when(repository.existsByEmail(email)).thenReturn(Mono.just(true));
        StepVerifier.create(adapter.existsByEmail(email))
            .expectNext(true)
            .verifyComplete();
        verify(repository).existsByEmail(email);
    }

    @Test
    void existsByEmail_should_return_false_when_email_not_exists() {
        String email = "noexiste@correo.com";
        when(repository.existsByEmail(email)).thenReturn(Mono.just(false));
        StepVerifier.create(adapter.existsByEmail(email))
            .expectNext(false)
            .verifyComplete();
        verify(repository).existsByEmail(email);
    }

    @Test
    void save_should_emit_error_when_user_is_null() {
        StepVerifier.create(adapter.save(null))
            .expectError(NullPointerException.class)
            .verify();
    }

    @Test
    void save_should_emit_error_when_mapper_fails() {
        User inputUser = User.builder()
            .name("Juan")
            .lastname("Pérez")
            .email("juan@example.com")
            .baseSalary(1_000_000)
            .roleId(1L)
            .build();
        when(entityMapper.toEntity(inputUser)).thenThrow(new IllegalArgumentException("Mapping error"));
        StepVerifier.create(adapter.save(inputUser))
            .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().equals("Mapping error"))
            .verify();
        verify(entityMapper).toEntity(inputUser);
    }
}
