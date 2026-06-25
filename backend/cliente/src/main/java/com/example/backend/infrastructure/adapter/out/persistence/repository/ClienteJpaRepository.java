package com.example.backend.infrastructure.adapter.out.persistence.repository;

import com.example.backend.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByClienteId(String clienteId);

    boolean existsByClienteId(String clienteId);
}
