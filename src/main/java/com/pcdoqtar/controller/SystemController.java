package com.pcdoqtar.controller;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    @GetMapping("/")
    public String home() {

        return "PC DOQTAR Server is running.";

    }
    
    @GetMapping("/sessions")
    public Session sessions() {
    	Configuration con = new Configuration();
    	SessionFactory sf = con.buildSessionFactory();
    	Session s = sf.openSession();
    	return s;
    	
    }
}