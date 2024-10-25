package backend.PrestaBanco.controllers;

import backend.PrestaBanco.services.CalculosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calculos")
@CrossOrigin("*")
public class CalculosController {
    @Autowired
    private CalculosService calculosService;

    // Método para calcular la cuota mensual
    @PostMapping("/cuotaMensual")
    public int calcularCuotaMensual(@RequestParam double monto, @RequestParam int plazo, @RequestParam String tipoCredito) {
        return calculosService.calcularCuotaMensual(monto, plazo, tipoCredito);
    }

    // Método para calcular el seguro desgravamen
    @PostMapping("/seguroDesgravamen")
    public int calcularSeguroDesgravamen(@RequestParam double monto) {
        return calculosService.calcularSeguroDesgravamen(monto);
    }

    // Método para calcular la comisión de administración
    @PostMapping("/comisionAdministracion")
    public int calcularComisionAdministracion(@RequestParam double monto) {
        return calculosService.calcularComisionAdministracion(monto);
    }

    // Método para calcular el costo mensual (cuota + seguros)
    @PostMapping("/costoMensual")
    public int calcularCostoMensual(@RequestParam double monto, @RequestParam int plazo, @RequestParam String tipoCredito) {
        int cuotaMensual = calculosService.calcularCuotaMensual(monto, plazo, tipoCredito);
        return calculosService.calcularCostoMensual(cuotaMensual, monto);
    }

    // Método para calcular el costo total del préstamo
    @PostMapping("/costoTotal")
    public int calcularCostoTotal(@RequestParam double monto, @RequestParam int plazo, @RequestParam String tipoCredito) {
        int cuotaMensual = calculosService.calcularCuotaMensual(monto, plazo, tipoCredito);
        return calculosService.calcularCostoTotal(cuotaMensual, monto, plazo);
    }
}
