package com.tcs.account_service.dtos.cliente;

public class ClienteVo {
    private Long clienteId;
    private String nombre;
    public String identificacion;
    private String email;
    private Boolean estado;

    public ClienteVo() {}

    // Getters y Setters
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
