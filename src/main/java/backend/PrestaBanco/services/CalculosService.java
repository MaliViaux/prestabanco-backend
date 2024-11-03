package backend.PrestaBanco.services;

import org.springframework.stereotype.Service;

@Service
public class CalculosService {
    public int calcularCuotaMensual(int monto, int plazo, double interes) {
        double tasaInteresAnual = interes;
        double r = (tasaInteresAnual / 12) / 100;
        int n = plazo * 12;

        return (int) (monto * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1));
    }

    public int calcularSeguroDesgravamen(int montoPrestamo) {
        return (int) (montoPrestamo * 0.0003); // 0.03% del monto del préstamo
    }

    // Cálculo de la comisión por administración
    public int calcularComisionAdministracion(int montoPrestamo) {
        return (int) (montoPrestamo * 0.01); // 1% del monto del préstamo
    }

    // Cálculo del costo mensual (cuota más seguros)
    public int calcularCostoMensual(int cuotaMensual, int montoPrestamo) {
        int seguroIncendio = 20000;
        return (cuotaMensual + calcularSeguroDesgravamen(montoPrestamo) + seguroIncendio); // Suma de la cuota mensual y los seguros
    }

    // Cálculo del costo total del préstamo
    public int calcularCostoTotal(int cuotaMensual, int montoPrestamo, int plazo) {
        int costoMensual = calcularCostoMensual(cuotaMensual, montoPrestamo);
        int comisionAdministracion = calcularComisionAdministracion(montoPrestamo);
        int totalPagos = plazo * 12;

        return ((costoMensual * totalPagos) + comisionAdministracion); // Costo total
    }
}
