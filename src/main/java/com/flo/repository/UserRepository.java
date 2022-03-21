package com.flo.repository;

import com.flo.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long>
{
    public List<User> findByCognomeIgnoreCase(String cognome);
    public List<User> findByNomeIgnoreCase(String nome);
    public List<User> findByNomeIgnoreCaseAndAndCognomeIgnoreCase(String nome, String cognome);
}
