package es.um.atica.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.properties.HasName;
import com.tngtech.archunit.core.importer.ClassFileImporter;

@SpringBootTest
public class ExampleArchUnitBase {
    
    private final String basePackage = getClass().getPackage().getName();
    private final JavaClasses devClasses = new ClassFileImporter().importPackages(basePackage);

    private String myPackage(String pack) {
        return basePackage + pack;
    }


    @Test
    @DisabledIf(expression = "${fdwjs.ignore.archunit.basic.tests:false}",loadContext = true)
    void onion_architecture_valid() {
        // Valida reglas básicas de arquitectura hexagonal https://www.archunit.org/userguide/html/000_Index.html#_onion_architecture
        onionArchitecture()
            .domainModels(myPackage(".domain.model.."),
                myPackage(".domain.exception.."),
                myPackage(".domain.event.."),
                myPackage(".domain.factory.."))
            .domainServices(myPackage(".domain.service.."))
            .applicationServices(myPackage(".application.port.."),
                myPackage(".application.service..")
                ,myPackage(".application.usecase.."))
            .adapter("api", myPackage(".adapters.api.."))
            .adapter("persistence", myPackage(".adapters.persistence.."))
            .adapter("provider", myPackage(".adapters.providers.."))
            .withOptionalLayers(true)
            .check(devClasses);
    }

    @Test
    @DisabledIf(expression = "${fdwjs.ignore.archunit.advanced.tests:false}",loadContext = true)
    void no_classes_outside_onion_architecture() {
        classes()
            .that()
            .resideOutsideOfPackages(myPackage(".domain.."),myPackage(".application.."),myPackage(".adapters.."))
            .should()
            .beAnnotatedWith(SpringBootApplication.class)
            .orShould()
            .beAnnotatedWith(SpringBootTest.class)
            .because("Todas las clases dentro de la arquitectura, salvo los tests y el arranque de spring application.")
            .check(devClasses);
    }

    @Test
    @DisabledIf(expression = "${fdwjs.ignore.archunit.advanced.tests:false}",loadContext = true)
    void ports_are_only_interfaces() {
        classes()
            .that()
            .resideInAPackage(myPackage(".application.port.."))
            .should()
            .beInterfaces()
            .because("Los puertos solo pueden ser interfaces.")
            .check(devClasses);
    }

    @Test
    @DisabledIf(expression = "${fdwjs.ignore.archunit.advanced.tests:false}",loadContext = true)
    void usecases_are_commands_or_query() {
        classes()
            .that()
            .resideInAPackage(myPackage(".application.usecase.."))
            .should()
            .haveSimpleNameEndingWith("Command")
            .orShould()
            .haveSimpleNameEndingWith("CommandHandler")
            .orShould()
            .haveSimpleNameEndingWith("Query")
            .orShould()
            .haveSimpleNameEndingWith("QueryHandler")
            .because("En los paquetes de casos de uso solo puedes poner Query o Command")
            .check(devClasses);
    }

    @Test
    @DisabledIf(expression = "${fdwjs.ignore.archunit.advanced.tests:false}",loadContext = true)
    void domain_model_has_private_constructor() {
        classes()
            .that()
            .resideInAPackage(myPackage(".domain.model.."))
            .should()
            .haveOnlyPrivateConstructors()
            .because("No puedes tener constructores públicos en las clases del modelo de dominio, utiliza factorias o builders")
            .check(devClasses);
    }

    
    @Test
    @DisabledIf(expression = "${fdwjs.ignore.archunit.advanced.tests:false}",loadContext = true)
    void repository_has_anotation() {
        classes()
            .that()
            .resideInAPackage(myPackage(".adapters.persistence.."))
            .and()
            .areAnnotatedWith(Repository.class)
            .should()
            .haveSimpleNameEndingWith("Adapter")
            .andShould()
            .implement(resideInAPackage(myPackage(".application.port..")).and(HasName.Predicates.nameEndingWith("Repository")))
            .because("Los adaptadores de persistencia de la capa de infraestructura deben implementar los repositorios en los puertos de la capa de aplicación.")
            .check(devClasses);
    }

}
