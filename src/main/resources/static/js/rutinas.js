function mostrarImagen(input) {
    if (input.files && input.files[0]) {
        const imagen = input.files[0];
        const maximo = 512 * 1024; //Se limita el tamaño a 512 Kb las imágenes.
        if (imagen.size <= maximo) {
            var lector = new FileReader();
            lector.onload = function (e) {
                $('#blah').attr('src', e.target.result).height(200);
            };
            lector.readAsDataURL(input.files[0]);
        } else {
            alert("La imagen seleccionada es muy grande... no debe superar los 512 Kb!");
        }
    }
}

//Para insertar información en el modal según el registro...
document.addEventListener('DOMContentLoaded', function () {
    const confirmModal = document.getElementById('confirmModal');
    if (!confirmModal) {
        return;
    }
    const idInput = document.getElementById('modalId');
    const submitBtn = confirmModal.querySelector('button[type="submit"]');

    confirmModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const id = button ? button.getAttribute('data-bs-id') : null;

        if (!id) {
            // No hay un id valido: no dejamos continuar la eliminacion.
            console.error('No se pudo determinar el id a eliminar; se cancela la operacion.');
            idInput.value = '';
            if (submitBtn) {
                submitBtn.disabled = true;
            }
            return;
        }

        idInput.value = id;
        document.getElementById('modalDescripcion').textContent = button.getAttribute('data-bs-descripcion');
        if (submitBtn) {
            submitBtn.disabled = false;
        }
    });

    // Ultima linea de defensa: si por cualquier motivo el id quedo vacio,
    // no dejamos que el formulario llegue al servidor.
    confirmModal.querySelector('form').addEventListener('submit', function (event) {
        if (!idInput.value) {
            event.preventDefault();
            console.error('Formulario de eliminacion bloqueado: falta idEstudiante.');
        }
    });
});

//Para quitar toast
setTimeout(() => {
    document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
}, 4000);

//para poner datos de la tabla de mensualidades en el modal de pago
function cargarDatosPago(boton) {
    const id = boton.getAttribute("data-id");
    const estudiante = boton.getAttribute("data-estudiante");
    const periodo = boton.getAttribute("data-periodo");
    const monto = boton.getAttribute("data-monto");
    document.getElementById("idMensualidad").value = id;
    document.getElementById("estudiantePago").value = estudiante;
    document.getElementById("periodoPago").value = periodo;
    document.getElementById("montoPago").value = monto;
}
