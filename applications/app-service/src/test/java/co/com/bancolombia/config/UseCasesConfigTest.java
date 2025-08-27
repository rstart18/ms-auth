package co.com.bancolombia.config;

import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.createuser.CreateUserUseCase;
import co.com.bancolombia.usecase.createuser.CreateUserValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {UseCasesConfig.class, UseCasesConfigTest.TestConfig.class})
@ActiveProfiles("test")
class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public UserRepository userRepository() {
            return org.mockito.Mockito.mock(UserRepository.class);
        }

        @Bean
        public CreateUserValidator createUserValidator() {
            return org.mockito.Mockito.mock(CreateUserValidator.class);
        }

        @Bean
        public CreateUserUseCase createUserUseCase(UserRepository userRepository, CreateUserValidator createUserValidator) {
            return new CreateUserUseCase(userRepository, createUserValidator);
        }
    }
}
