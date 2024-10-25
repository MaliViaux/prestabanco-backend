package backend.PrestaBanco.services;

import org.springframework.stereotype.Service;

@Service
public class CalculosService {
    public int calcularCuotaMensual(double monto, int plazo, String tipoCredito) {
        double tasaInteresAnual = obtenerTasaInteres(tipoCredito);
        double r = (tasaInteresAnual / 12) / 100;
        int n = plazo * 12;

        return (int) (monto * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1));
    }

    private double obtenerTasaInteres(String tipoCredito) {
        switch (tipoCredito) {
            case "Primera Vivienda":
                return 4.25; // Promedio de 3.5% - 5.0%
            case "Segunda Vivienda":
                return 5.0; // Promedio de 4.0% - 6.0%
            case "Propiedades Comerciales":
                return 6.0; // Promedio de 5.0% - 7.0%
            case "Remodelacion":
                return 5.25; // Promedio de 4.5% - 6.0%
            default:
                throw new IllegalArgumentException("Tipo de crédito no válido.");
        }
    }

    public int calcularSeguroDesgravamen(double montoPrestamo) {
        return (int) (montoPrestamo * 0.0003); // 0.03% del monto del préstamo
    }

    // Cálculo de la comisión por administración
    public int calcularComisionAdministracion(double montoPrestamo) {
        return (int) (montoPrestamo * 0.01); // 1% del monto del préstamo
    }

    // Cálculo del costo mensual (cuota más seguros)
    public int calcularCostoMensual(double cuotaMensual, double montoPrestamo) {
        double seguroIncendio = 20000;
        return (int) (cuotaMensual + calcularSeguroDesgravamen(montoPrestamo) + seguroIncendio); // Suma de la cuota mensual y los seguros
    }

    // Cálculo del costo total del préstamo
    public int calcularCostoTotal(double cuotaMensual, double montoPrestamo, int plazo) {
        double costoMensual = calcularCostoMensual(cuotaMensual, montoPrestamo);
        double comisionAdministracion = calcularComisionAdministracion(montoPrestamo);
        int totalPagos = plazo * 12;

        return (int) ((costoMensual * totalPagos) + comisionAdministracion); // Costo total
    }

}
