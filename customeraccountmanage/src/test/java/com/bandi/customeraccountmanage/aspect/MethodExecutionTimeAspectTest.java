package com.bandi.customeraccountmanage.aspect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class MethodExecutionTimeAspectTest {

    @Configuration
    static class TestConfig {
        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    @Service
    static class TestService {
        public void testMethod() throws InterruptedException {
            // Simulate some work
            Thread.sleep(100);
        }
    }

    @Autowired
    private TestService testService;

    @Test
    void testMethodExecutionTimeLogging() {
        assertDoesNotThrow(() -> {
            testService.testMethod();
        }, "Method execution with AOP logging should not throw any exceptions");
    }
}