package com.maven.fas;

import com.maven.fas.persistence.Dao;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class FooArchtectTest {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("com.maven.fas");

    @Test
    public void verificarDependeciasParaCamadaPersistencia() {

        ArchRule rule = classes()
                .that().resideInAPackage("..service..")
                .should().onlyHaveDependentClassesThat()
                .resideInAnyPackage("..persistence..", "..service..");

        rule.check(importedClasses);
    }

    @Test
    public void verificarDependeciasDaCamadaPersistencia() {

        ArchRule rule = noClasses()
                .that().resideInAPackage("..persistence..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..service..");

        rule.check(importedClasses);
    }

    @Test
    public void verificarNomesClassesDaCamadaPersistencia() {

        ArchRule rule = classes()
                .that()
                .haveSimpleNameEndingWith("Dao")
                .should()
                .resideInAPackage("..persistence..");

        rule.check(importedClasses);
    }

    @Test
    public void verificarImplementacaoDaos() {

        ArchRule rule = classes()
                .that()
                .implement(Dao.class)
                .should()
                .haveSimpleNameEndingWith("Dao");

        rule.check(importedClasses);
    }

    @Test
    public void VerificarDependenciaCiclica(){
        ArchRule rule = slices()
                .matching("com.maven.fas.(*)..")
                .should().beFreeOfCycles();

        rule.check(importedClasses);
    }

    @Test
    public void VerifciarViolacaoDeCamadas(){
       ArchRule rule = layeredArchitecture()
                .layer("Service").definedBy("..service..")
                .layer("Persistance").definedBy("..persistance..")

                .whereLayer("Persistance").mayOnlyBeAccessedByLayers("Service");
    }
}
