package dev.example;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import es.um.atica.archunit.ExampleArchUnitBase;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class ExampleArchUnitTests extends ExampleArchUnitBase {
    
}
