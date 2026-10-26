package com.pcdoqtar.application.controller;

import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
//import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pcdoqtar.application.error.ErrorResponse;
import com.pcdoqtar.application.error.ErrorType;



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
    	
    	String SessionId = UUID.randomUUID().toString();

        return SessionId;
    }
    

    @GetMapping("/test")
    public Integer test(){
        try (Session session = sessionFactory.openSession()) {

            return session.createNativeQuery("SELECT 1", Integer.class).getSingleResult();

        } catch (Exception e) {

            return 0;
        }
    	
    }
    @GetMapping("/error-test")
    public ErrorResponse errorTest() {

        return new ErrorResponse(
                ErrorType.SERVER_ERROR,
                "This is a test error response."
        );
    }
    @GetMapping("/error-trigger-test")
    public String errorTriggerTest() {
        throw new RuntimeException("Internal test exception");
    }
}