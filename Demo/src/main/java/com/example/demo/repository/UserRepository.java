package com.example.demo.repository;

import com.example.connector.repository.CRUDNoSQLRepository;
import com.example.demo.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends CRUDNoSQLRepository<User,String> {
}
