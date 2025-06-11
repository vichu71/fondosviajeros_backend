package com.vhs.fondosviajeros.service;

import com.vhs.fondosviajeros.entity.Fondo;
import com.vhs.fondosviajeros.repository.FondoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class FondoService {

    @Autowired
    private FondoRepository fondoRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;

    public Fondo crearFondo(String nombre) {
        Fondo fondo = new Fondo();
        fondo.setNombre(nombre);
        fondo.setCodigo(generarCodigoUnico());
        fondo.setFechaCreacion(LocalDateTime.now());
        return fondoRepository.save(fondo);
    }

    public Optional<Fondo> findByCodigo(String codigo) {
        return fondoRepository.findByCodigo(codigo);
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = generarCodigoAleatorio();
        } while (fondoRepository.findByCodigo(codigo).isPresent());
        return codigo;
    }

    private String generarCodigoAleatorio() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    public Optional<Fondo> findById(Long id) {
        return fondoRepository.findById(id);
    }

}
