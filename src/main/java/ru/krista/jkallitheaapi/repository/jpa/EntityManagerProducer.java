package ru.krista.jkallitheaapi.repository.jpa;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Продюсер менеджера сущностей.
 */
public class EntityManagerProducer {

    /**
     * EntityManager.
     */
    @Produces
    @Dependent
    @PersistenceContext(unitName = "kalithea")
    public EntityManager entityManager;
}
