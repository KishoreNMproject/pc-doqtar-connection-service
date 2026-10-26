package com.pcdoqtar.application.DAO.Implementations;

import com.pcdoqtar.application.DAO.DAO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pcdoqtar.application.controller.SystemController;

import jakarta.transaction.Transaction;

public class imp implements DAO{
	
    private final SessionFactory sessionFactory;

    public imp (SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
	@Override
	public File dataPull(double uid) {
		File Diagnostics = null;
		try(Session session = sessionFactory.openSession()){
			org.hibernate.Transaction transaction = session.beginTransaction();
			
			transaction.commit();
			
		}
		
		
		
		return Diagnostics;
		
		
		
		
		
	}

	@Override
	public void dataPush() {
		
		
		
	}

}
