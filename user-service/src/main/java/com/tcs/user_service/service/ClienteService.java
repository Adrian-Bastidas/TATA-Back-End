package com.tcs.user_service.service;

import com.tcs.user_service.dtos.cliente.ClienteRequestDTO;
import com.tcs.user_service.dtos.cliente.ClienteResponseVo;
import com.tcs.user_service.exception.ResourceAlreadyExistException;
import com.tcs.user_service.exception.ResourceNotFoundException;
import com.tcs.user_service.mappers.ClienteMapper;
import com.tcs.user_service.model.Cliente;
import com.tcs.user_service.repository.ClienteRepository;
import com.tcs.user_service.utils.ClienteIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    @Autowired
    private ClienteIdGenerator clienteIdGenerator;


    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteResponseVo createCliente(ClienteRequestDTO dto) {
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion())) {
            throw new ResourceAlreadyExistException("Identificación ya registrada");
        }
        Cliente cliente = clienteMapper.dtoToEntity(dto);
        Long clienteId = (Long) new ClienteIdGenerator().generate(null, null);
        cliente.setClienteId(clienteId);
        return clienteMapper.entityToVo(clienteRepository.save(cliente));
    }

    public List<ClienteResponseVo> listClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("No hay clientes registrados");
        }

        return clientes.stream()
                .map(clienteMapper::entityToVo)
                .collect(Collectors.toList());
    }

    public ClienteResponseVo updateCliente(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        clienteMapper.updateEntityFromDto(dto, cliente);
        return clienteMapper.entityToVo(clienteRepository.save(cliente));
    }

    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    public ClienteResponseVo getCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        return clienteMapper.entityToVo(cliente);
    }
}
