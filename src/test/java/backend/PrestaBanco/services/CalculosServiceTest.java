package backend.PrestaBanco.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CalculosServiceTest {
    @Autowired
    private CalculosService calculosService;

    @Test
    public void testCalcularCuotaMensual() {
        int monto = 1000000; // Monto del préstamo en CLP
        int plazo = 20;      // Plazo en años
        double interes = 4.5; // Tasa de interés anual en %

        int cuotaMensual = calculosService.calcularCuotaMensual(monto, plazo, interes);

        assertThat(cuotaMensual).isGreaterThan(0); // Verifica que el cálculo es positivo
        assertThat(cuotaMensual).isEqualTo(6326); // Verifica el valor esperado (ajustar según el cálculo exacto)
    }

    @Test
    public void testCalcularSeguroDesgravamen() {
        int montoPrestamo = 1000000;

        int seguroDesgravamen = calculosService.calcularSeguroDesgravamen(montoPrestamo);

        assertThat(seguroDesgravamen).isGreaterThan(0); // Verifica que el cálculo es positivo
        assertThat(seguroDesgravamen).isEqualTo(300);   // 0.03% del monto del préstamo
    }

    @Test
    public void testCalcularComisionAdministracion() {
        int montoPrestamo = 1000000;

        int comision = calculosService.calcularComisionAdministracion(montoPrestamo);

        assertThat(comision).isGreaterThan(0); // Verifica que el cálculo es positivo
        assertThat(comision).isEqualTo(10000); // 1% del monto del préstamo
    }

    @Test
    public void testCalcularCostoMensual() {
        int montoPrestamo = 1000000;
        int cuotaMensual = 6327; // Resultado simulado del cálculo de la cuota mensual

        int costoMensual = calculosService.calcularCostoMensual(cuotaMensual, montoPrestamo);

        assertThat(costoMensual).isGreaterThan(cuotaMensual); // Verifica que el costo mensual incluye seguros
        assertThat(costoMensual).isEqualTo(26627); // cuotaMensual + seguroDesgravamen + seguroIncendio
    }

    @Test
    public void testCalcularCostoTotal() {
        int montoPrestamo = 1000000;
        int cuotaMensual = 6327; // Resultado simulado del cálculo de la cuota mensual
        int plazo = 20; // en años

        int costoTotal = calculosService.calcularCostoTotal(cuotaMensual, montoPrestamo, plazo);

        assertThat(costoTotal).isGreaterThan(0); // Verifica que el cálculo es positivo
        assertThat(costoTotal).isEqualTo(6400480); // Ejemplo de cálculo total, ajustar según el cálculo exacto
    }
}