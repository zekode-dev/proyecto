package com.example.proyecto.service;

import com.example.proyecto.model.cliente;
import com.example.proyecto.repository.ClienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Optional<cliente> buscarPorDni(String dni) {

        return clienteRepository.findByDni(dni);

    }

}