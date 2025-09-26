package org.example.meetingplanner.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JpaManager {
    private static final Logger log = LogManager.getLogger(JpaManager.class);
    private static final EntityManagerFactory entityManagerFactory;

    static {
        try {
            log.info("Initializing JPA EntityManagerFactory");
            entityManagerFactory = Persistence.createEntityManagerFactory("meetingplanner-pu");
            log.info("JPA EntityManagerFactory initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize JPA EntityManagerFactory", e);
            throw new RuntimeException("Failed to initialize JPA", e);
        }
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            log.info("Closing JPA EntityManagerFactory");
            entityManagerFactory.close();
        }
    }
}