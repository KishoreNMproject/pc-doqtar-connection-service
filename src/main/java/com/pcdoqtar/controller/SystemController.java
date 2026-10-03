package com.pcdoqtar.controller;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@RestController
public class SystemController {

    @GetMapping("/")
    public String home() {

        return "PC DOQTAR Server is running.";

    }
    
    private final SessionFactory sessionFactory;

    public SystemController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @GetMapping("/sessions")
    public boolean sessions() {

        try (Session session = sessionFactory.openSession()) {

            session.createNativeQuery("SELECT 1", Integer.class).getSingleResult();

            return true;

        } catch (Exception e) {

            return false;

        }
    }
    @PersistenceContext
    private EntityManager entityManager;
    
    @GetMapping("/getsession")
    @Transactional(readOnly = true)
    public Long getsession() {
        // Execute a native query to pull the connection ID
        return ((Number) entityManager.createNativeQuery("SELECT CONNECTION_ID()")
                .getSingleResult()).longValue();
    }
}