package com.example.cuenta.domain.port.in;

import com.example.cuenta.domain.model.Cuenta;
import java.util.List;

public interface GestionarCuentaUseCase {

    Cuenta crear(Cuenta cuenta);

    List<Cuenta> listarTodas();

    Cuenta buscarPorId(Long id);

    Cuenta actualizar(Long id, Cuenta cuenta);

    Cuenta actualizarParcial(Long id, Cuenta cambios);

    void eliminar(Long id);
}
