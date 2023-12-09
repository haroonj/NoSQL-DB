package com.example.bootstrapping.repository;

import com.example.bootstrapping.model.system.User;
import com.example.bootstrapping.repository.config.CRUDNoSQLRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends CRUDNoSQLRepository<User, String> {
}
