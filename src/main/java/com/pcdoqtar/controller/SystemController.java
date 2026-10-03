package com.pcdoqtar.controller;

import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


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
   
    @GetMapping("/getsession")
    public String getsession() {

        return UUID.randomUUID().toString();
    }
}