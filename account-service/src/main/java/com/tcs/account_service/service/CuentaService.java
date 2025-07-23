package com.tcs.account_service.service;

import com.tcs.account_service.dtos.cliente.ClienteVo;
import com.tcs.account_service.dtos.cuenta.CuentaRequestDTO;
import com.tcs.account_service.dtos.cuenta.CuentaResponseVo;
import com.tcs.account_service.exception.ClienteNoEncontradoException;
import com.tcs.account_service.exception.CuentaNoEncontradaException;
import com.tcs.account_service.mappers.CuentaMapper;
import com.tcs.account_service.model.Cuenta;
import com.tcs.account_service.repository.CuentaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaService {
    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private CuentaMapper cuentaMapper;

    private final SecureRandom random = new SecureRandom();
    private static final Logger logger = LoggerFactory.getLogger(CuentaService.class);


    @Transactional
    public CuentaResponseVo crearCuenta(CuentaRequestDTO cuentaDTO) {
        logger.info("Iniciando creación de cuenta para cliente ID: {}", cuentaDTO.getClienteId());

        ClienteVo cliente;
        try {
            cliente = userServiceClient.obtenerCliente(cuentaDTO.getClienteId());
            if (cliente == null) {
                logger.warn("Cliente con ID {} no encontrado", cuentaDTO.getClienteId());
                throw new ClienteNoEncontradoException("Cliente con ID " + cuentaDTO.getClienteId() + " no encontrado");
            }
            logger.info("Cliente encontrado: {}", cliente.getClienteId());
        } catch (Exception e) {
            logger.error("Error al verificar cliente: {}", e.getMessage(), e);
            throw new ClienteNoEncontradoException("Error al verificar cliente: " + e.getMessage());
        }

        if (!cliente.getEstado()) {
            logger.info("Cliente inactivo. Procediendo a activarlo: {}", cliente.getClienteId());
            userServiceClient.activarCliente(cliente.getClienteId());
        }

        Cuenta cuenta = cuentaMapper.dtoToEntity(cuentaDTO);
        cuenta.setNumeroCuenta(generarNumeroCuenta());
        logger.debug("Número de cuenta generado: {}", cuenta.getNumeroCuenta());

        cuenta = cuentaRepository.save(cuenta);
        logger.info("Cuenta guardada con ID: {}", cuenta.getCuentaId());

        try {
            userServiceClient.notificarCuentaCreada(cliente.getClienteId(), cuenta.getCuentaId());
            logger.info("Notificación enviada a user-service para cliente ID: {}", cliente.getClienteId());
        } catch (Exception e) {
            logger.warn("Error al notificar creación de cuenta: {}", e.getMessage(), e);
        }

        CuentaResponseVo cuentaVO = cuentaMapper.entityToVO(cuenta);
        cuentaVO.setCliente(cliente);

        logger.info("Cuenta creada exitosamente: {}", cuentaVO.getCuentaId());
        return cuentaVO;
    }

    public CuentaResponseVo obtenerCuenta(Long cuentaId) {
        logger.info("Buscando cuenta con ID: {}", cuentaId);
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> {
                    logger.warn("Cuenta con ID {} no encontrada", cuentaId);
                    return new CuentaNoEncontradaException("Cuenta no encontrada");
                });

        CuentaResponseVo cuentaVO = cuentaMapper.entityToVO(cuenta);

        try {
            ClienteVo cliente = userServiceClient.obtenerCliente(cuenta.getClienteId());
            cuentaVO.setCliente(cliente);
            logger.info("Información de cliente agregada a cuenta ID: {}", cuentaId);
        } catch (Exception e) {
            logger.warn("Error al obtener información del cliente para cuenta {}: {}", cuentaId, e.getMessage());
        }

        return cuentaVO;
    }

    public List<CuentaResponseVo> obtenerCuentasPorCliente(Long clienteId) {
        logger.info("Buscando cuentas activas para cliente ID: {}", clienteId);
        try {
            ClienteVo cliente = userServiceClient.obtenerCliente(clienteId);
            if (cliente == null) {
                logger.warn("Cliente con ID {} no encontrado", clienteId);
                throw new ClienteNoEncontradoException("Cliente no encontrado");
            }

            List<Cuenta> cuentas = cuentaRepository.findByClienteIdAndEstado(clienteId, true);
            logger.info("Se encontraron {} cuentas activas para el cliente {}", cuentas.size(), clienteId);

            return cuentas.stream()
                    .map(cuenta -> {
                        CuentaResponseVo vo = cuentaMapper.entityToVO(cuenta);
                        vo.setCliente(cliente);
                        return vo;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error al obtener cuentas del cliente {}: {}", clienteId, e.getMessage(), e);
            throw new ClienteNoEncontradoException("Error al obtener cuentas del cliente: " + e.getMessage());
        }
    }

    @Transactional
    public void desactivarCuenta(Long cuentaId) {
        logger.info("Desactivando cuenta con ID: {}", cuentaId);
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> {
                    logger.warn("Cuenta con ID {} no encontrada para desactivación", cuentaId);
                    return new CuentaNoEncontradaException("Cuenta no encontrada");
                });

        cuenta.setEstado(false);
        cuentaRepository.save(cuenta);
        logger.info("Cuenta desactivada correctamente: {}", cuentaId);
    }

    private String generarNumeroCuenta() {
        String numeroCuenta;
        do {
            numeroCuenta = "ACC" + String.format("%010d", Math.abs(random.nextLong()) % 10000000000L);
        } while (cuentaRepository.existsByNumeroCuenta(numeroCuenta));
        logger.debug("Número de cuenta único generado: {}", numeroCuenta);
        return numeroCuenta;
    }

}
