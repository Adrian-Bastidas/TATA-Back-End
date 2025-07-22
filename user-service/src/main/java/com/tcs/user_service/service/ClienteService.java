package com.tcs.user_service.service;

import com.tcs.user_service.dtos.cliente.ClienteRequestDTO;
import com.tcs.user_service.dtos.cliente.ClienteResponseVo;
import com.tcs.user_service.mappers.ClienteMapper;
import com.tcs.user_service.model.Cliente;
import com.tcs.user_service.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteResponseVo createCliente(ClienteRequestDTO dto) {
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion())) {
            throw new IllegalArgumentException("Identificación ya registrada");
        }
        Cliente cliente = clienteMapper.dtoToEntity(dto);
        return clienteMapper.entityToVo(clienteRepository.save(cliente));
    }

    public List<ClienteResponseVo> listClientes() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::entityToVo)
                .collect(Collectors.toList());
    }

    public ClienteResponseVo updateCliente(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        clienteMapper.updateEntityFromDto(dto, cliente);
        return clienteMapper.entityToVo(clienteRepository.save(cliente));
    }

    public void deleteCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    public ClienteResponseVo getCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        return clienteMapper.entityToVo(cliente);
    }
}
