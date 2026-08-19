package com.taru.service;

import com.taru.domain.Mensualidad;
import com.taru.domain.Pago;
import com.taru.repository.MensualidadRepository;
import com.taru.repository.PagoRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final MensualidadRepository mensualidadRepository;
    private final FirebaseStorageService firebaseStorageService;


    @Transactional
    public void guardar(Pago pago) {
        pagoRepository.save(pago);
    }

    @Transactional(readOnly = true)
    public List<Pago> getPagos() {
        return pagoRepository.findAll();
    }

    @Transactional
    public void enviarRecordatorios() {

    }
    
       
     //Registra el pago de una mensualidad y cambia su estado a Pagada.
   
    @Transactional
    public void registrarPago(
            Integer idMensualidad,
            LocalDate fechaPago,
            String metodoPago,
            String observaciones,
            MultipartFile reciboFile) throws IOException {

        // Buscar la mensualidad
        Mensualidad mensualidad = mensualidadRepository
                .findById(idMensualidad)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La mensualidad no existe"));

        // Verificar que no exista ya un pago
        if (pagoRepository
                .findByMensualidad_IdMensualidad(idMensualidad)
                .isPresent()) {

            throw new IllegalStateException(
                    "Esta mensualidad ya tiene un pago registrado");
        }

        // Crear el pago
        Pago pago = new Pago();

        pago.setMensualidad(mensualidad);
        pago.setFechaPago(fechaPago);

        // El monto viene directamente de la mensualidad
        pago.setMonto(mensualidad.getMonto());

        pago.setMetodoPago(metodoPago);
        pago.setObservaciones(observaciones);

        // Guardar el recibo en Firebase
        if (reciboFile != null && !reciboFile.isEmpty()) {

            String ruta = firebaseStorageService.uploadImage(
                    reciboFile,
                    "recibos",
                    idMensualidad
            );

            pago.setRutaRecibo(ruta);
        }

        // Guardar el pago
        pagoRepository.save(pago);

        // Cambiar estado de la mensualidad
        mensualidad.setEstado(
                Mensualidad.EstadoMensualidad.Pagada
        );

        mensualidadRepository.save(mensualidad);
    }

}
