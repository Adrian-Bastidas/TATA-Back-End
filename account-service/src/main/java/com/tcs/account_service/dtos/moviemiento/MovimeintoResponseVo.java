package com.tcs.account_service.dtos.moviemiento;

import com.tcs.account_service.dtos.cliente.ClienteVo;

public class MovimeintoResponseVo {
    private Long numeroCuenta;
    private String tipo;
    private String saldoInicial;
    private Long estado;
    private Boolean Movimiento;

    public MovimeintoResponseVo() {
    }

    public Long getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(Long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(String saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public Long getEstado() {
        return estado;
    }

    public void setEstado(Long estado) {
        this.estado = estado;
    }

    public Boolean getMovimiento() {
        return Movimiento;
    }

    public void setMovimiento(Boolean movimiento) {
        Movimiento = movimiento;
    }
}
