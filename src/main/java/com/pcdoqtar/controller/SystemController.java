package com.pcdoqtar.controller;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String sessions() {

        try (Session session = sessionFactory.openSession()) {

            session.createNativeQuery("SELECT 1", Integer.class).getSingleResult();

            return "Hibernate database connection successful.";

        } catch (Exception e) {

            return "Hibernate database connection failed.";

        }
    }
}