package com.example.demo.service;

import com.example.demo.model.Email;
import com.example.demo.repository.EmailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class EmailService {


    private final EmailRepository emailRepository;


        public List<Email> getAllEmails() {
            return emailRepository.getAllDocuments();
        }
        public Email addEmail(Email email){
            return emailRepository.createDocument(email);
        }


    }



