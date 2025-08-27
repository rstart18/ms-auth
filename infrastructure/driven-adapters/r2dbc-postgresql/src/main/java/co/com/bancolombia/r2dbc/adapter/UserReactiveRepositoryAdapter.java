package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.mapper.entity.UserEntityMapper;
import co.com.bancolombia.r2dbc.repository.UserReactiveRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter
    extends ReactiveAdapterOperations<User, UserEntity, Long, UserReactiveRepository>
    implements UserRepository {

    private final UserReactiveRepository repository;
    private final UserEntityMapper entityMapper;
    private final TransactionalOperator tx;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository,
                                         org.reactivecommons.utils.ObjectMapper mapper,
                                         UserEntityMapper entityMapper,
                                         TransactionalOperator tx) {
        super(repository, mapper, entityMapper::toDomain);
        this.repository = repository;
        this.entityMapper = entityMapper;
        this.tx = tx;
    }

    @Override
    protected UserEntity toData(User domain) {
        return entityMapper.toEntity(domain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<User> save(User user) {
        UserEntity entity = toData(user);
        return tx.transactional(
            repository.save(entity)
                .map(entityMapper::toDomain)
        );
    }
}

