package backend.PrestaBanco.repositories;

import backend.PrestaBanco.entities.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    Optional<Solicitud> findById(Long id);

    // Obtener solicitudes por RUT del cliente
    @Query("SELECT s FROM Solicitud s WHERE s.cliente.rut = :rut")
    List<Solicitud> findByClienteRut(@Param("rut") String rut);

    @Query("SELECT s FROM Solicitud s WHERE s.estado NOT IN ('Rechazada', 'Cancelada por el Cliente', 'En Desembolso', 'Aprobada')")
    List<Solicitud> findAllExcludingEstados();
}
