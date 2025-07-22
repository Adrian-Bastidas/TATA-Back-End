package com.tcs.user_service.repository;

import com.tcs.user_service.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {

    boolean existsByIdentificacion(String identificacion);
}
