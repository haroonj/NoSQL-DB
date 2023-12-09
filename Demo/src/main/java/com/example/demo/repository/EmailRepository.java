package com.example.demo.repository;

import com.example.connector.repository.CRUDNoSQLRepository;
import com.example.demo.model.Email;
import org.springframework.stereotype.Repository;

@Repository
public class EmailRepository extends CRUDNoSQLRepository<Email, String> {
}
