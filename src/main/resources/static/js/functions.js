// ===============================
// ESPERAR DOM
// ===============================
document.addEventListener("DOMContentLoaded", function () {

    console.log("JS cargado correctamente");

    // ACTIVAR VALIDACIONES
    activarValidaciones();

});

// =========================================================================
// 1. GUARDAR USUARIO (CON VALIDACIONES COMPLETAS Y CONTROL DE DNI DUPLICADO)
// =========================================================================
document.addEventListener("submit", function (e) {

    if (e.target.id === "formUsuario") {

        e.preventDefault();

        let form = e.target;

        // Captura y limpieza de campos obligatorios
        const dni = form.querySelector('[name="dni"]')?.value.trim();
        const nombres = form.querySelector('[name="nombres"]')?.value.trim();
        const usuario = form.querySelector('[name="usuario"]')?.value.trim();
        const correo = form.querySelector('[name="correo"]')?.value.trim();
        const telefono = form.querySelector('[name="telefono"]')?.value.trim();

        // Validación 1: Evitar campos vacíos o con puros espacios en blanco
        if (!dni || !nombres || !usuario || !correo || !telefono) {
            Swal.fire('Campos Incompletos', 'Por favor, complete todos los campos del formulario.', 'warning');
            return;
        }

        // Validación 2: Formato exacto de DNI peruano (8 dígitos numéricos)
        if (dni.length !== 8 || isNaN(dni)) {
            Swal.fire('DNI Inválido', 'El número de DNI debe contener exactamente 8 dígitos numéricos.', 'warning');
            return;
        }

        // Validación 3: Formato exacto de Teléfono (9 dígitos numéricos)
        if (telefono.length !== 9 || isNaN(telefono)) {
            Swal.fire('Teléfono Inválido', 'El número de teléfono debe contener exactamente 9 dígitos numéricos.', 'warning');
            return;
        }

        // Validación 4: Restricción explícita de correo Gmail
        const regexGmail = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
        if (!regexGmail.test(correo)) {
            Swal.fire('Correo Inválido', 'El correo electrónico ingresado debe ser una cuenta válida de Gmail (@gmail.com).', 'warning');
            return;
        }

        let formData = new FormData(form);

        fetch("/usuarios/guardar", {
            method: "POST",
            body: formData
        })
        .then(res => {
            if (!res.ok) throw new Error("Error HTTP " + res.status);
            return res.text();
        })
        .then(data => {

            if (data === "ok") {
                Swal.fire({
                    icon: 'success',
                    title: 'Correcto',
                    text: 'Usuario registrado con éxito',
                    timer: 1500,
                    showConfirmButton: false
                });

                // Cerrar modal de forma segura
                let modalElement = document.getElementById('modalUsuario');
                let modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
                modal.hide();

                // Limpiar formulario y refrescar la tabla SPA
                form.reset();
                cargarVista('/usuarios');

            } else if (data === "dni_existe") {
                // Alerta específica si el controlador detecta que el DNI ya está en PostgreSQL
                Swal.fire({
                    icon: 'error',
                    title: 'DNI Duplicado',
                    text: 'El número de DNI ingresado ya se encuentra registrado en el sistema.',
                });

            } else {
                throw new Error("Respuesta inválida del servidor");
            }

        })
        .catch(err => {
            console.error(err);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudo procesar el registro del usuario.'
            });
        });
    }
});

// =========================================================================
// 2. ACTUALIZAR USUARIO (VALIDACIONES DE CAMPOS MODIFICABLES)
// =========================================================================
document.addEventListener("submit", function (e) {

    if (e.target.id === "formEditarUsuario") {

        e.preventDefault();

        let form = e.target;

        // Captura de los datos modificables en la edición
        const correo = document.getElementById("edit_correo").value.trim();
        const telefono = document.getElementById("edit_telefono").value.trim();

        // Validación 1: Que no dejen vacíos los campos de contacto
        if (!correo || !telefono) {
            Swal.fire('Campos vacíos', 'No puede dejar en blanco los campos de Correo o Teléfono.', 'warning');
            return;
        }

        // Validación 2: Teléfono de 9 dígitos en la actualización
        if (telefono.length !== 9 || isNaN(telefono)) {
            Swal.fire('Teléfono Inválido', 'El teléfono modificado debe contener exactamente 9 números.', 'warning');
            return;
        }

        // Validación 3: Formato Gmail en la actualización
        const regexGmail = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
        if (!regexGmail.test(correo)) {
            Swal.fire('Correo Inválido', 'El correo editado requiere una terminación válida "@gmail.com".', 'warning');
            return;
        }

        let formData = new FormData(form);

        fetch("/usuarios/actualizar", {
            method: "POST",
            body: formData
        })
        .then(res => {
            if (!res.ok) throw new Error("Error al actualizar");
            return res.text();
        })
        .then(data => {

            if (data === "ok") {
                Swal.fire({
                    icon: 'success',
                    title: 'Actualizado',
                    text: 'Usuario actualizado correctamente',
                    timer: 1500,
                    showConfirmButton: false
                });

                // Cerrar modal de edición de forma segura
                let modalElement = document.getElementById('modalEditarUsuario');
                let modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
                modal.hide();

                // Recargar tabla SPA
                cargarVista('/usuarios');

            } else {
                throw new Error("Error lógico en el servidor");
            }

        })
        .catch(err => {
            console.error(err);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudieron guardar los cambios del usuario.'
            });
        });
    }
});

// =========================================================================
// 3. CONTROL DE VISTA: CARGAR MODAL DE EDICIÓN CON DATOS
// =========================================================================
function editarUsuario(btn) {

    document.getElementById("edit_id").value = btn.dataset.id;
    document.getElementById("edit_dni").value = btn.dataset.dni;
    document.getElementById("edit_nombres").value = btn.dataset.nombres;
    document.getElementById("edit_usuario").value = btn.dataset.usuario;
    document.getElementById("edit_correo").value = btn.dataset.correo;
    document.getElementById("edit_telefono").value = btn.dataset.telefono;
    document.getElementById("edit_rol").value = btn.dataset.rol;

    // Inicialización explícita compatible con entornos SPA dinámicos
    var modalElement = document.getElementById('modalEditarUsuario');
    var modalInstance = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
    modalInstance.show();
}
// ===============================
// ===============================
// ELIMINAR USUARIO
// ===============================

function eliminarUsuario(btn) {

    let id = btn.dataset.id;

    Swal.fire({
        title: '¿Eliminar usuario?',
        text: 'El usuario será desactivado',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {

        if (result.isConfirmed) {

            let formData = new FormData();

            formData.append("idUsuario", id);

            fetch("/usuarios/eliminar", {
                method: "POST",
                body: formData
            })
            .then(res => res.text())
            .then(data => {

                // ===============================
                // ELIMINADO CORRECTAMENTE
                // ===============================

                if (data === "ok") {

                    Swal.fire({
                        icon: 'success',
                        title: 'Eliminado',
                        text: 'Usuario desactivado',
                        timer: 1500,
                        showConfirmButton: false
                    });

                    cargarVista('/usuarios');

                    return;
                }

                // ===============================
                // ADMIN PRINCIPAL
                // ===============================

                if (data === "admin_no_eliminar") {

                    Swal.fire({
                        icon: 'warning',
                        title: 'No permitido',
                        text: 'No puedes eliminar el administrador principal'
                    });

                    return;
                }

                // ===============================
                // SIN PERMISOS
                // ===============================

                if (data === "sin_permiso") {

                    Swal.fire({
                        icon: 'error',
                        title: 'Sin permisos',
                        text: 'No tienes permisos para eliminar usuarios'
                    });

                    return;
                }

                // ===============================
                // ERROR GENERAL
                // ===============================

                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'No se pudo eliminar'
                });

            })
            .catch(err => {

                console.error(err);

                Swal.fire({
                    icon: 'error',
                    title: 'Error servidor',
                    text: 'Ocurrió un problema'
                });

            });

        }

    });

}

//ACTIVAR USUARIO
function reactivarUsuario(btn) {

    let id = btn.dataset.id;

    Swal.fire({
        title: '¿Reactivar usuario?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí, reactivar'
    }).then((result) => {

        if (result.isConfirmed) {

            let formData = new FormData();
            formData.append("idUsuario", id);

            fetch("/usuarios/reactivar", {
                method: "POST",
                body: formData
            })
            .then(res => res.text())
            .then(() => {
                Swal.fire("Activado", "", "success");
                cargarVista('/usuarios');
            });

        }

    });

}

// ================================
//ROLES COMPLETO
//===================================
// ================= GUARDAR =================
document.addEventListener("submit", function (e) {

    if (e.target.id === "formRol") {

        e.preventDefault();

        let formData = new FormData(e.target);

        fetch("/roles/guardar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(() => {

            Swal.fire("Guardado", "", "success");

            cerrarModal("modalRol");

            cargarVista('/roles');
        });
    }
});

// ================= EDITAR =================
function editarRol(btn) {

    document.getElementById("edit_idRol").value = btn.dataset.id;
    document.getElementById("edit_nombre").value = btn.dataset.nombre;

    new bootstrap.Modal(document.getElementById('modalEditarRol')).show();
}

document.addEventListener("submit", function (e) {

    if (e.target.id === "formEditarRol") {

        e.preventDefault();

        let formData = new FormData(e.target);

        fetch("/roles/actualizar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(() => {

            Swal.fire("Actualizado", "", "success");

            cerrarModal("modalEditarRol");

            cargarVista('/roles');
        });
    }
});

// ================= ELIMINAR ROL =================
function eliminarRol(btn) {

    let id = btn.dataset.id;

    Swal.fire({
        title: '¿Eliminar rol?',
        text: 'El rol será desactivado',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {

        if (result.isConfirmed) {

            let formData = new FormData();
            formData.append("idRol", id);

            fetch("/roles/eliminar", {
                method: "POST",
                body: formData
            })
            .then(async res => {
                // Si viene un error 400 del servidor, capturamos el texto del backend
                if (!res.ok) {
                    let textErr = await res.text();
                    throw new Error(textErr);
                }
                return res.text();
            })
            .then(data => {
                if (data === "ok") {
                    Swal.fire({
                        icon: 'success',
                        title: 'Eliminado',
                        text: 'Rol desactivado con éxito.',
                        timer: 1500,
                        showConfirmButton: false
                    });

                    cargarVista('/roles');
                } else {
                    throw new Error("Error lógico");
                }
            })
            .catch(err => {
                console.error(err);
                // Si el error coincide con nuestra restricción personalizada
                if (err.message === "RESTRICCION_USUARIOS") {
                    Swal.fire({
                        icon: 'error',
                        title: 'No se puede inactivar',
                        text: '¡Atención, mano! No puedes desactivar este rol porque tiene usuarios activos asignados.',
                        confirmButtonColor: '#111'
                    });
                } else {
                    Swal.fire("Error", "No se pudo completar la operación.", "error");
                }
            });
        }
    });
}
// ================= REACTIVAR ROL =================
function reactivarRol(btn) {

    let id = btn.dataset.id;

    Swal.fire({
        title: '¿Reactivar rol?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí, reactivar'
    }).then((result) => {

        if (result.isConfirmed) {

            let formData = new FormData();
            formData.append("idRol", id);

            fetch("/roles/reactivar", {
                method: "POST",
                body: formData
            })
            .then(res => res.text())
            .then(() => {

                Swal.fire({
                    icon: 'success',
                    title: 'Activado',
                    timer: 1500,
                    showConfirmButton: false
                });

                cargarVista('/roles');
            });

        }

    });
}

// ================= PERMISOS =================
function verPermisos(btn) {

    let id = btn.dataset.id;

    document.getElementById("permiso_idRol").value = id;

    fetch("/roles/permisos/" + id)
    .then(res => res.json())
    .then(data => {

        let html = "";

        data.todos.forEach(p => {

            let checked = data.asignados.includes(p.idPermiso) ? "checked" : "";

            html += `
                <div>
                    <input type="checkbox" name="permisos" value="${p.idPermiso}" ${checked}>
                    ${p.nombre}
                </div>
            `;
        });

        document.getElementById("listaPermisos").innerHTML = html;

        new bootstrap.Modal(document.getElementById('modalPermisos')).show();
    });
}

// GUARDAR PERMISOS
document.addEventListener("submit", function (e) {

    if (e.target.id === "formPermisos") {

        e.preventDefault();

        let formData = new FormData(e.target);

        fetch("/roles/permisos/guardar", {
            method: "POST",
            body: formData
        })
        .then(() => {

            Swal.fire("Permisos guardados", "", "success");

            cerrarModal("modalPermisos");
        });
    }
});

function cerrarModal(id) {

    let modal = bootstrap.Modal.getInstance(document.getElementById(id));

    if (modal) modal.hide();

    document.querySelectorAll(".modal-backdrop").forEach(el => el.remove());

    document.body.classList.remove("modal-open");
}


// ===============================
// PERMISOS CRUD
// ===============================

// GUARDAR
document.addEventListener("submit", function (e) {

    if (e.target.id === "formPermiso") {

        e.preventDefault();

        let form = e.target;
        let formData = new FormData(form);

        fetch("/permisos/guardar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(data => {

            if (data === "ok") {

                Swal.fire({
                    icon: 'success',
                    title: 'Guardado',
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal = bootstrap.Modal.getInstance(document.getElementById('modalPermiso'));
                modal.hide();

                form.reset();

                cargarVista('/permisos');

            }

        });

    }
});

// EDITAR VIEW
function editarPermiso(btn) {

    document.getElementById("edit_idPermiso").value = btn.dataset.id;
    document.getElementById("edit_nombrePermiso").value = btn.dataset.nombre;

    new bootstrap.Modal(document.getElementById('modalEditarPermiso')).show();
}

// ACTUALIZAR
document.addEventListener("submit", function (e) {

    if (e.target.id === "formEditarPermiso") {

        e.preventDefault();

        let formData = new FormData(e.target);

        fetch("/permisos/actualizar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(data => {

            if (data === "ok") {

                Swal.fire({
                    icon: 'success',
                    title: 'Actualizado',
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarPermiso'));
                modal.hide();

                cargarVista('/permisos');
            }

        });

    }
});

// ELIMINAR
function eliminarPermiso(btn) {

    let id = btn.dataset.id;

    Swal.fire({
        title: '¿Eliminar permiso?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí'
    }).then((r) => {

        if (r.isConfirmed) {

            let formData = new FormData();
            formData.append("idPermiso", id);

            fetch("/permisos/eliminar", {
                method: "POST",
                body: formData
            })
            .then(res => res.text())
            .then(() => {
                Swal.fire("Eliminado", "", "success");
                cargarVista('/permisos');
            });

        }

    });
}

// REACTIVAR
function reactivarPermiso(btn) {

    let id = btn.dataset.id;

    let formData = new FormData();
    formData.append("idPermiso", id);

    fetch("/permisos/reactivar", {
        method: "POST",
        body: formData
    })
    .then(() => {
        Swal.fire("Activado", "", "success");
        cargarVista('/permisos');
    });
}


// ==================================
//CATEGORIAS COMPLETO
// ==================================


// GUARDAR
document.addEventListener("submit", function (e) {

    if (e.target.id === "formCategoria") {

        e.preventDefault();

        let form = e.target;
        let formData = new FormData(form);

        fetch("/categorias/guardar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(data => {

            if (data === "ok") {

                Swal.fire({
                    icon: 'success',
                    title: 'Guardado',
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal = bootstrap.Modal.getInstance(document.getElementById('modalCrear'));
                modal.hide();

                form.reset();

                cargarVista('/categorias');

            }

        });

    }
});

// EDITAR VIEW
function editarCategoria(btn) {

    document.getElementById("edit_idCategoria").value = btn.dataset.id;
    document.getElementById("edit_nombreCategoria").value = btn.dataset.nombre;
    document.getElementById("edit_descripcion").value = btn.dataset.descripcion;

    new bootstrap.Modal(document.getElementById('modalEditarCategoria')).show();
}

// ACTUALIZAR
document.addEventListener("submit", function (e) {

    if (e.target.id === "formEditarCategoria") {

        e.preventDefault();

        let formData = new FormData(e.target);

        fetch("/categorias/actualizar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(data => {

            if (data === "ok") {

                Swal.fire({
                    icon: 'success',
                    title: 'Actualizado',
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarCategoria'));
                modal.hide();

                cargarVista('/categorias');
            }

        });

    }
});

// ELIMINAR
// ELIMINAR
function eliminarCategoria(btn) {

    let id = btn.dataset.id;

    Swal.fire({
        title: '¿Eliminar categoria?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#111',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar'
    }).then((r) => {

        if (r.isConfirmed) {

            let formData = new FormData();
            formData.append("idCategoria", id);

            fetch("/categorias/eliminar", {
                method: "POST",
                body: formData
            })
            .then(async res => {
                // Si la respuesta no es OK (código 400), capturamos el texto del error
                if (!res.ok) {
                    let textErr = await res.text();
                    throw new Error(textErr);
                }
                return res.text();
            })
            .then(() => {
                Swal.fire("Eliminado", "La categoría se inactivó correctamente.", "success");
                cargarVista('/categorias');
            })
            .catch(err => {
                // Evaluamos si el error viene por la restricción de base de datos
                if (err.message === "RESTRICCION_PRODUCTOS") {
                    Swal.fire({
                        icon: 'error',
                        title: 'No se puede inactivar',
                        text: '¡Atención, mano! Esta categoría tiene productos asignados actualmente en el sistema.',
                        confirmButtonColor: '#111'
                    });
                } else {
                    Swal.fire("Error", "Ocurrió un problema inesperado.", "error");
                }
            });
        }
    });
}
// REACTIVAR
function reactivarCategoria(btn) {

    let id = btn.dataset.id;

    let formData = new FormData();
    formData.append("idCategoria", id);

    fetch("/categorias/reactivar", {
        method: "POST",
        body: formData
    })
    .then(() => {
        Swal.fire("Activado", "", "success");
        cargarVista('/categorias');
    });
}


// ======================================
// PRODUCTOS COMPLETO
// ======================================

// =========================================================================
// FUNCIÓN DE ASISTENCIA: EVITAR QUE EL STOCK MÍNIMO EX_CEDA AL STOCK NORMAL
// =========================================================================
function validarStockMinimo(inputMinimo) {
    let fila = inputMinimo.closest('.row');
    let inputStock = fila.querySelector('input[name="stockDetalle"]');

    let stockNormal = parseInt(inputStock.value) || 0;
    let stockMinimo = parseInt(inputMinimo.value) || 0;

    // Si el mínimo supera al stock normal, lo igualamos al stock máximo permitido
    if (stockMinimo > stockNormal) {
        inputMinimo.value = stockNormal;
    }
}


// ======================================
// GUARDAR PRODUCTO (CON VALIDACIÓN)
// ======================================
document.addEventListener("submit", function (e) {
    if (e.target.id === "formProducto") {
        e.preventDefault();
        let form = e.target;

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        let detalles = form.querySelectorAll("#contenedorDetalles .detalle-item");

        if (detalles.length === 0) {
            Swal.fire('Falta información', 'Debes agregar al menos una variante (talla) al producto.', 'warning');
            return;
        }

        for (let detalle of detalles) {
            let selectTalla = detalle.querySelector('select[name="talla"]').value;
            let inputColor = detalle.querySelector('input[name="color"]').value.trim();
            let inputStock = detalle.querySelector('input[name="stockDetalle"]').value;
            let inputMinimo = detalle.querySelector('input[name="stockMinimoDetalle"]').value;

            if (!selectTalla || inputColor === "" || inputStock === "" || inputMinimo === "") {
                Swal.fire('Campos vacíos', 'Por favor, completa todos los campos de las variantes agregadas.', 'warning');
                return;
            }

            if (parseInt(inputMinimo) > parseInt(inputStock)) {
                Swal.fire('Error de Stock', `En la variante color "${inputColor}", el stock mínimo no puede ser mayor que el stock normal.`, 'warning');
                return;
            }
        }

        let formData = new FormData(form);

        fetch("/productos/guardar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(data => {
            if (data === "ok") {
                Swal.fire({ icon: 'success', title: 'Producto registrado', timer: 1500, showConfirmButton: false });
                bootstrap.Modal.getInstance(document.getElementById('modalProducto')).hide();
                form.reset();
                document.getElementById("previewImagenes").innerHTML = "";
                document.getElementById("contenedorDetalles").innerHTML = "";
                cargarVista('/productos');
            }
        });
    }
});

// =========================================================================
// PREVISUALIZAR IMAGENES
// =========================================================================
document.addEventListener("change", function (e) {
    if (e.target.id === "imagenesProducto") {
        let preview = document.getElementById("previewImagenes");
        preview.innerHTML = "";
        let archivos = e.target.files;

        for (let file of archivos) {
            let reader = new FileReader();
            reader.onload = function (ev) {
                preview.innerHTML += `
                    <img src="${ev.target.result}" width="100" height="100" style="object-fit:cover; border-radius:10px; border:1px solid #ddd;">
                `;
            };
            reader.readAsDataURL(file);
        }
    }
});
// =========================================================================
// EDITAR PRODUCTO (CON PROTOCOLO SEGURO ANTI-FALLOS DE KARDEX)
// =========================================================================
function editarProducto(btn) {
    document.getElementById("edit_idProducto").value = btn.dataset.id;
    document.getElementById("edit_nombre").value = btn.dataset.nombre;
    document.getElementById("edit_descripcion").value = btn.dataset.descripcion;
    document.getElementById("edit_precio").value = btn.dataset.precio;
    document.getElementById("edit_categoria").value = btn.dataset.categoria;

    let contenedor = document.getElementById("imagenesActuales");
    contenedor.innerHTML = "";

    if (btn.dataset.imagen) {
        let imagenes = btn.dataset.imagen.split(",");
        imagenes.forEach((img, index) => {
            contenedor.innerHTML += `
                <div class="col-md-4 text-center">
                    <img src="${img}" class="img-fluid shadow-sm" style="height:140px; width:100%; object-fit:cover; border-radius:12px; border:1px solid #ddd;">
                    <small class="d-block mt-2 fw-semibold">Imagen ${index + 1}</small>
                </div>
            `;
        });
    }

    let contenedorDetalles = document.getElementById("contenedorDetallesEditar");
    contenedorDetalles.innerHTML = "";

    if (btn.dataset.detalles) {
        let detalles = btn.dataset.detalles.split(";");

        detalles.forEach(d => {
            let item = d.split("|");

            // Si falta el dato por un string corrupto, se autoprotege con un valor numérico por defecto
            let stockMinVal = item[4] ? item[4] : 0;

            contenedorDetalles.innerHTML += `
                <div class="card border mb-3 detalle-item shadow-sm">
                    <div class="card-body">
                        <input type="hidden" name="idDetalle" value="${item[0]}">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <label>Talla</label>
                                <select name="talla" class="form-select">
                                    <option value="S" ${item[1] === 'S' ? 'selected' : ''}>S</option>
                                    <option value="M" ${item[1] === 'M' ? 'selected' : ''}>M</option>
                                    <option value="L" ${item[1] === 'L' ? 'selected' : ''}>L</option>
                                    <option value="XL" ${item[1] === 'XL' ? 'selected' : ''}>XL</option>
                                </select>
                            </div>

                            <div class="col-md-3">
                                <label>Color</label>
                                <input type="text" name="color" class="form-control" value="${item[2]}" onkeypress="return soloLetras(event)">
                            </div>

                            <div class="col-md-3">
                                <label>Stock (Histórico)</label>
                                <input type="number"
                                       name="stockDetalle"
                                       class="form-control bg-light text-muted fw-bold"
                                       value="${item[3]}"
                                       min="0"
                                       style="pointer-events: none; tab-index: -1;">
                            </div>

                            <div class="col-md-3">
                                <label>Stock mínimo</label>
                                <input type="number" name="stockMinimoDetalle" class="form-control" value="${stockMinVal}" min="0">
                            </div>

                            <div class="col-md-12 text-end">
                                <button type="button" class="btn btn-danger btn-sm" onclick="eliminarDetalle(this)">
                                    <i class="bi bi-trash"></i> Eliminar
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });
    }

    new bootstrap.Modal(document.getElementById("modalEditarProducto")).show();
}

document.addEventListener("submit", function (e) {
    if (e.target.id === "formEditarProducto") {
        e.preventDefault();
        let form = e.target;

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        let detalles = form.querySelectorAll("#contenedorDetallesEditar .detalle-item");

        if (detalles.length === 0) {
            Swal.fire('Falta información', 'El producto no puede quedarse sin variantes. Agrega al menos una.', 'warning');
            return;
        }

        for (let detalle of detalles) {
            let selectTalla = detalle.querySelector('select[name="talla"]').value;
            let inputColor = detalle.querySelector('input[name="color"]').value.trim();
            let inputStock = detalle.querySelector('input[name="stockDetalle"]').value;
            let inputMinimo = detalle.querySelector('input[name="stockMinimoDetalle"]').value;

            if (!selectTalla || inputColor === "" || inputStock === "" || inputMinimo === "") {
                Swal.fire('Campos incompletos', 'Hay campos vacíos en las variantes del producto. Revísalos antes de actualizar.', 'warning');
                return;
            }

            if (parseInt(inputMinimo) > parseInt(inputStock)) {
                Swal.fire('Error de Stock', `En la variante de color "${inputColor}", el stock mínimo no puede superar al stock normal.`, 'warning');
                return;
            }
        }

        let formData = new FormData(form);

        if (document.getElementById("edit_imagen1").files.length === 0) formData.delete("imagen1");
        if (document.getElementById("edit_imagen2").files.length === 0) formData.delete("imagen2");
        if (document.getElementById("edit_imagen3").files.length === 0) formData.delete("imagen3");

        fetch("/productos/actualizar", {
            method: "POST",
            body: formData
        })
        .then(res => {
            if (!res.ok) throw new Error("Error HTTP: " + res.status);
            return res.text();
        })
        .then(data => {
            if (data === "ok") {
                Swal.fire({ icon: 'success', title: 'Producto actualizado', timer: 1500, showConfirmButton: false });
                bootstrap.Modal.getInstance(document.getElementById("modalEditarProducto")).hide();
                cargarVista('/productos');
            }
        })
        .catch(err => {
            console.error(err);
            Swal.fire('Error de Servidor', 'Ocurrió un fallo de consistencia al intentar procesar la transacción.', 'error');
        });
    }
});

// =========================================================================
// AGREGAR DETALLE EDITAR
// =========================================================================
function agregarDetalleEditar() {
    let contenedor = document.getElementById("contenedorDetallesEditar");
    contenedor.innerHTML += `
        <div class="card border mb-3 detalle-item shadow-sm">
            <div class="card-body">
                <div class="row g-3">
                    <div class="col-md-3">
                        <label>Talla</label>
                        <select name="talla" class="form-select">
                            <option value="S">S</option>
                            <option value="M">M</option>
                            <option value="L">L</option>
                            <option value="XL">XL</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label>Color</label>
                        <input type="text" name="color" class="form-control" onkeypress="return soloLetras(event)">
                    </div>
                    <div class="col-md-3">
                        <label>Stock</label>
                        <input type="number" name="stockDetalle" class="form-control" min="0" value="0">
                    </div>
                    <div class="col-md-3">
                        <label>Stock mínimo</label>
                        <input type="number" name="stockMinimoDetalle" class="form-control" min="0" value="0">
                    </div>
                    <div class="col-md-12 text-end">
                        <button type="button" class="btn btn-danger btn-sm" onclick="eliminarDetalle(this)">
                            <i class="bi bi-trash"></i> Eliminar
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
}
// =========================================================================
// AGREGAR DETALLE NUEVO
// =========================================================================
function agregarDetalle() {
    let contenedor = document.getElementById("contenedorDetalles");
    contenedor.innerHTML += `
        <div class="card border mb-3 detalle-item shadow-sm">
            <div class="card-body">
                <div class="row g-3">
                    <div class="col-md-3">
                        <label class="fw-semibold">Talla</label>
                        <select name="talla" class="form-select" required>
                            <option value="S">S</option>
                            <option value="M">M</option>
                            <option value="L">L</option>
                            <option value="XL">XL</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="fw-semibold">Color</label>
                        <input type="text" name="color" class="form-control" placeholder="Negro" required onkeypress="return soloLetras(event)">
                    </div>
                    <div class="col-md-3">
                        <label class="fw-semibold">Stock</label>
                        <input type="number" name="stockDetalle" class="form-control" required min="0">
                    </div>
                    <div class="col-md-3">
                        <label class="fw-semibold">Stock Mínimo</label>
                        <input type="number" name="stockMinimoDetalle" class="form-control" required min="0">
                    </div>
                    <div class="col-md-12 text-end">
                        <button type="button" class="btn btn-danger btn-sm" onclick="eliminarDetalle(this)">
                            <i class="bi bi-trash"></i> Eliminar
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
}
// ======================================
// ELIMINAR DETALLE
// ======================================

function eliminarDetalle(btn){
    btn.closest(".detalle-item").remove();
}
// ======================================
// ELIMINAR PRODUCTO
// ======================================

function eliminarProducto(btn) {
    let id = btn.dataset.id;
    Swal.fire({
        title: '¿Eliminar producto?',
        text: 'El producto cambiará a estado inactivo para proteger su historial de Kardex.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, inactivar'
    })
    .then((r) => {
        if (r.isConfirmed) {
            let formData = new FormData();
            formData.append("idProducto", id);

            fetch("/productos/eliminar", {
                method: "POST",
                body: formData
            })
            .then(res => res.text())
            .then(data => {
                if (data === "ok") {
                    Swal.fire({ icon: 'success', title: 'Producto inactivado', timer: 1500, showConfirmButton: false });
                    cargarVista('/productos');
                }
            });
        }
    });
}
// ======================================
// REACTIVAR PRODUCTO
// ======================================

function reactivarProducto(btn) {

    let id = btn.dataset.id;

    let formData = new FormData();

    formData.append("idProducto", id);

    fetch("/productos/reactivar", {
        method: "POST",
        body: formData
    })
    .then(res => res.text())
    .then(data => {

        if (data === "ok") {

            Swal.fire({
                icon: 'success',
                title: 'Producto reactivado',
                timer: 1500,
                showConfirmButton: false
            });

            cargarVista('/productos');

        }

    });

}

// ======================================
// VER IMAGENES
// ======================================

function verImagenes(btn) {

    let contenedor =
        document.getElementById("contenedorImagenes");

    contenedor.innerHTML = "";

    let idProducto = btn.dataset.id;

    if (btn.dataset.imagenes) {

        let imagenes = btn.dataset.imagenes.split(",");

        imagenes.forEach((img, index) => {

            contenedor.innerHTML += `

                <div class="text-center">

                    <img src="${img}"
                         width="250"
                         class="shadow mb-2"
                         style="
                            border-radius:12px;
                            object-fit:cover;
                            height:250px;
                         ">

                    ${
                        index === 0
                        ?
                        `<div class="badge bg-success">
                            PORTADA ACTUAL
                        </div>`
                        :
                        `
                        <button class="btn btn-dark btn-sm"
                                onclick="hacerPortada(
                                    ${idProducto},
                                    '${img}'
                                )">

                            Hacer portada

                        </button>
                        `
                    }

                </div>

            `;
        });

    }

    new bootstrap.Modal(
        document.getElementById("modalImagenes")
    ).show();
}

//HACER PORTADA
function hacerPortada(idProducto, imagen) {

    let formData = new FormData();
    formData.append("idProducto", idProducto);
    formData.append("imagen", imagen);

    fetch("/productos/portada", {
        method: "POST",
        body: formData
    })
    .then(res => res.text())
    .then(data => {

        if (data === "ok") {

            Swal.fire({
                icon: 'success',
                title: 'Nueva portada actualizada',
                timer: 1200,
                showConfirmButton: false
            });

            cerrarModalImagenes();

            setTimeout(() => {
                cargarVista('/productos');
            }, 300);
        }
    });
}
function cerrarModalImagenes() {

    let modalEl = document.getElementById("modalImagenes");

    let modal = bootstrap.Modal.getInstance(modalEl);

    if (modal) {
        modal.hide();
    }


    document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
    document.body.classList.remove('modal-open');
    document.body.style.overflow = '';
}

// ==========================================
// GUARDAR SLIDER
// ==========================================

document.addEventListener("submit", function (e) {

    if (e.target.id === "formSlider") {

        e.preventDefault();

        let formData =
            new FormData(e.target);

        fetch("/configuracion/slider/guardar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(data => {

            if (data === "ok") {

                Swal.fire({
                    icon: "success",
                    title: "Slider registrado",
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal =
                    bootstrap.Modal.getInstance(
                        document.getElementById("modalSlider")
                    );

                modal.hide();

                volverTabSliders();

            }

        });

    }

});

// ==========================================
// DESACTIVAR SLIDER
// ==========================================

function desactivarSlider(btn) {

    let formData = new FormData();

    formData.append(
        "idSlider",
        btn.dataset.id
    );

    fetch("/configuracion/slider/desactivar", {
        method: "POST",
        body: formData
    })
    .then(res => res.text())
    .then(data => {

        if (data === "ok") {

            volverTabSliders();

        }

    });

}

// ==========================================
// ACTIVAR SLIDER
// ==========================================

function activarSlider(btn) {

    let formData = new FormData();

    formData.append(
        "idSlider",
        btn.dataset.id
    );

    fetch("/configuracion/slider/activar", {
        method: "POST",
        body: formData
    })
    .then(res => res.text())
    .then(data => {

        if (data === "ok") {

            volverTabSliders();

        }

    });

}

// ======================================
// ABRIR EDITAR SLIDER
// ======================================

function editarSlider(btn){

    document.getElementById("edit_idSlider").value =
        btn.dataset.id;

    document.getElementById("edit_titulo").value =
        btn.dataset.titulo;

    document.getElementById("edit_subtitulo").value =
        btn.dataset.subtitulo;

    document.getElementById("previewSlider").src =
        btn.dataset.imagen;
    document.getElementById("edit_textoBoton").value =
    btn.dataset.textoboton;

    document.getElementById("edit_linkBoton").value =
    btn.dataset.linkboton;
    new bootstrap.Modal(
        document.getElementById("modalEditarSlider")
    ).show();

}

// ======================================
// ABRIR EDITAR LOGO
// ======================================

function editarLogo(btn){

    document.getElementById("edit_idLogo").value =
        btn.dataset.id;

    document.getElementById("edit_nombreLogo").value =
        btn.dataset.nombre;

    document.getElementById("previewLogo").src =
        btn.dataset.imagen;

    new bootstrap.Modal(
        document.getElementById("modalEditarLogo")
    ).show();

}

// ==========================================
// ACTUALIZAR SLIDER
// ==========================================

document.addEventListener("submit", function (e) {

    if (e.target.id === "formEditarSlider") {

        e.preventDefault();

        let formData =
            new FormData(e.target);

        fetch("/configuracion/slider/actualizar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(data => {

            if (data === "ok") {

                Swal.fire({
                    icon: "success",
                    title: "Slider actualizado",
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal =
                    bootstrap.Modal.getInstance(
                        document.getElementById(
                            "modalEditarSlider"
                        )
                    );

                modal.hide();

                volverTabSliders();

            }

        });

    }

});

// ==========================================
// GUARDAR LOGO
// ==========================================

document.addEventListener("submit", function (e) {

    if (e.target.id === "formLogo") {

        e.preventDefault();

        let formData =
            new FormData(e.target);

        fetch("/configuracion/logo/guardar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(data => {

            if (data === "ok") {

                Swal.fire({
                    icon: "success",
                    title: "Logo registrado",
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal =
                    bootstrap.Modal.getInstance(
                        document.getElementById("modalLogo")
                    );

                modal.hide();

                volverTabLogos();

            }

        });

    }

});

// ==========================================
// ACTUALIZAR LOGO
// ==========================================

document.addEventListener("submit", function (e) {

    if (e.target.id === "formEditarLogo") {

        e.preventDefault();

        let formData =
            new FormData(e.target);

        fetch("/configuracion/logo/actualizar", {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(data => {

            if (data === "ok") {

                Swal.fire({
                    icon: "success",
                    title: "Logo actualizado",
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal =
                    bootstrap.Modal.getInstance(
                        document.getElementById(
                            "modalEditarLogo"
                        )
                    );

                modal.hide();

                volverTabLogos();

            }

        });

    }

});
// ==========================================
   // ACTIVAR LOGO
   // ==========================================

   function activarLogo(btn) {

       let formData = new FormData();

       formData.append(
           "idLogo",
           btn.dataset.id
       );

       fetch("/configuracion/logo/activar", {
           method: "POST",
           body: formData
       })
       .then(res => res.text())
       .then(data => {

           if (data === "ok") {

               Swal.fire({
                   icon: "success",
                   title: "Logo activado",
                   timer: 1200,
                   showConfirmButton: false
               });

               volverTabLogos();

               // =====================================
               // VOLVER A TAB LOGOS
               // =====================================

               setTimeout(() => {

                   let trigger =
                       document.querySelector(
                           '[data-bs-target="#tabLogos"]'
                       );

                   if(trigger){

                       new bootstrap.Tab(trigger).show();

                   }

               }, 300);

           }

       });

   }
// ==========================================
// DESACTIVAR LOGO
// ==========================================

function desactivarLogo(btn) {

    let formData = new FormData();

    formData.append(
        "idLogo",
        btn.dataset.id
    );

    fetch("/configuracion/logo/desactivar", {
        method: "POST",
        body: formData
    })
    .then(res => res.text())
    .then(data => {

        if (data === "ok") {

            Swal.fire({
                icon: "success",
                title: "Logo desactivado",
                timer: 1200,
                showConfirmButton: false
            });

           volverTabLogos();

            // =====================================
            // VOLVER A TAB LOGOS
            // =====================================

            setTimeout(() => {

                let trigger =
                    document.querySelector(
                        '[data-bs-target="#tabLogos"]'
                    );

                if(trigger){

                    new bootstrap.Tab(trigger).show();

                }

            }, 300);

        }

    });

}

// ==========================================
// VOLVER A TAB LOGOS
// ==========================================

function volverTabLogos(){

    cargarVista('/configuracion');

    setTimeout(() => {

        let trigger =
            document.querySelector(
                '[data-bs-target="#tabLogos"]'
            );

        if(trigger){

            new bootstrap.Tab(trigger).show();

        }

    }, 300);

}

// ==========================================
// VOLVER A TAB SLIDERS
// ==========================================

function volverTabSliders(){

    cargarVista('/configuracion');

    setTimeout(() => {

        let trigger =
            document.querySelector(
                '[data-bs-target="#tabSliders"]'
            );

        if(trigger){

            new bootstrap.Tab(trigger).show();

        }

    }, 300);

}


// =====================================
// GUARDAR EMPRESA
// =====================================

$(document).on("submit", "#formEmpresa", function(e){

    e.preventDefault();

    let formData =
        new FormData(this);

    $.ajax({

        url: "/configuracion/empresa/guardar",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,

        success: function(){

            Swal.fire({

                icon: "success",
                title: "Éxito",
                text: "Empresa actualizada correctamente",
                confirmButtonColor: "#111"

            }).then(() => {

                location.reload();

            });

        },

        error: function(){

            Swal.fire({

                icon: "error",
                title: "Oops...",
                text: "Ocurrió un error al guardar",
                confirmButtonColor: "#111"

            });

        }

    });

});


// =====================================
// PREVIEW CATEGORIAS
// =====================================

$("input[name='imagenCategorias']").change(function(e){

    let reader = new FileReader();

    reader.onload = function(event){

        $("#previewCategorias")
            .attr("src", event.target.result)
            .show();

    }

    reader.readAsDataURL(
        e.target.files[0]
    );

});


// =====================================
// PREVIEW BANNER
// =====================================

$("input[name='bannerImagen']").change(function(e){

    let reader = new FileReader();

    reader.onload = function(event){

        $("#previewBanner")
            .attr("src", event.target.result)
            .show();

    }

    reader.readAsDataURL(
        e.target.files[0]
    );

});


// =====================================
// PREVIEW CATEGORIAS
// =====================================

$("input[name='imagenCategorias']").change(function(e){

    let reader = new FileReader();

    reader.onload = function(event){

        $("#previewCategorias")
            .attr("src", event.target.result)
            .show();

    }

    reader.readAsDataURL(
        e.target.files[0]
    );

});

// =====================================
// PREVIEW BANNER
// =====================================

$("input[name='bannerImagen']").change(function(e){

    let reader = new FileReader();

    reader.onload = function(event){

        $("#previewBanner")
            .attr("src", event.target.result)
            .show();

    }

    reader.readAsDataURL(
        e.target.files[0]
    );

});

// ===============================
// DATATABLE
// ===============================
function iniciarDataTable(idTabla) {

    if (!document.querySelector(idTabla)) return;

    if ($.fn.DataTable.isDataTable(idTabla)) {
        $(idTabla).DataTable().destroy();
    }

    $(idTabla).DataTable({
        responsive: true
    });
}


// ===============================
// VALIDACIONES
// ===============================
function soloNumeros(input) {
    input.addEventListener("input", function () {
        this.value = this.value.replace(/[^0-9]/g, '');
    });
}

function activarValidaciones() {

    document.querySelectorAll('input[name="dni"]').forEach(i => soloNumeros(i));
    document.querySelectorAll('input[name="telefono"]').forEach(i => soloNumeros(i));

}
// =========================================
// VARIABLES GLOBALES Y CARRITO
// =========================================
let carritoVenta = [];

$(document).ready(function () {
    inicializarFormaPago();
    validarInputEnteroPositivo("#cantidadVenta");
    validarInputEnteroPositivo("#cuotas");
    restringirFechasPasadas();
});

// =========================================
// BLOQUEAR FECHAS ANTERIORES DESDE LA PC
// =========================================
function restringirFechasPasadas() {
    let hoy = new Date();
    let dd = String(hoy.getDate()).padStart(2, '0');
    let mm = String(hoy.getMonth() + 1).padStart(2, '0');
    let yyyy = hoy.getFullYear();
    let fechaMinima = yyyy + '-' + mm + '-' + dd;
    $("#fechaPrimerVencimiento").attr("min", fechaMinima);
}

// =========================================
// AGREGAR PRODUCTO AL CARRITO
// =========================================
function agregarProductoVenta() {
    let producto = document.getElementById("productoVenta");
    let talla = document.getElementById("tallaVenta");
    let cantidad = parseInt(document.getElementById("cantidadVenta").value) || 0;

    if (producto.value === "") {
        Swal.fire("Seleccione un producto", "", "warning");
        return;
    }
    if (talla.value === "") {
        Swal.fire("Seleccione una talla", "", "warning");
        return;
    }

    let precio = parseFloat(document.getElementById("precioVenta").value);
    let stock = parseInt(document.getElementById("stockVenta").value);

    if (cantidad <= 0) {
        Swal.fire("Cantidad inválida", "", "warning");
        return;
    }
    if (cantidad > stock) {
        Swal.fire("Stock insuficiente", "", "warning");
        return;
    }

    let idDetalle = talla.value;
    let existe = carritoVenta.find(item => item.idDetalle == idDetalle);

    if (existe) {
        let nuevaCantidad = existe.cantidad + cantidad;
        if (nuevaCantidad > stock) {
            Swal.fire("Supera el stock disponible", "", "warning");
            return;
        }
        existe.cantidad = nuevaCantidad;
        existe.subtotal = existe.cantidad * existe.precio;
    } else {
        carritoVenta.push({
            idDetalle: idDetalle,
            producto: producto.selectedOptions[0].text,
            talla: talla.selectedOptions[0].text,
            precio: precio,
            textTalla: talla.selectedOptions[0].text,
            cantidad: cantidad,
            subtotal: precio * cantidad
        });
    }

    renderizarCarrito();

    producto.value = "";
    talla.innerHTML = `<option value="">Seleccione talla</option>`;
    document.getElementById("stockVenta").value = "";
    document.getElementById("precioVenta").value = "";
    document.getElementById("cantidadVenta").value = 1;
    producto.focus();
}

function renderizarCarrito(){
    let body = document.getElementById("detalleVentaBody");
    body.innerHTML = "";
    let total = 0;

    carritoVenta.forEach((item, index) => {
        total += item.subtotal;
        body.innerHTML += `
            <tr>
                <td>${item.producto}</td>
                <td>${item.talla}</td>
                <td>S/ ${item.precio.toFixed(2)}</td>
                <td>${item.cantidad}</td>
                <td>S/ ${item.subtotal.toFixed(2)}</td>
                <td>
                    <button class="btn btn-danger btn-sm" onclick="eliminarItem(${index})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    });

    let subtotal = total / 1.18;
    let igv = total - subtotal;

    document.getElementById("subtotalVenta").innerText = subtotal.toFixed(2);
    document.getElementById("igvVenta").innerText = igv.toFixed(2);
    document.getElementById("totalVentaTexto").innerText = total.toFixed(2);
    document.getElementById("totalVenta").value = total.toFixed(2);

    calcularCuota();
}

function eliminarItem(index){
    carritoVenta.splice(index, 1);
    renderizarCarrito();
}

// ============================================
// PRODUCTO -> CARGAR TALLAS
// ============================================
document.addEventListener("change", function(e){
    if(e.target.id === "productoVenta"){
        let idProducto = e.target.value;
        let tallaSelect = document.getElementById("tallaVenta");

        tallaSelect.innerHTML = `<option value="">Seleccione talla</option>`;
        document.getElementById("stockVenta").value = "";
        document.getElementById("precioVenta").value = "";

        if(idProducto === "") return;

        fetch("/ventas/detalles-producto/" + idProducto)
        .then(res => res.json())
        .then(data => {
            data.forEach(detalle => {
                let option = document.createElement("option");
                option.value = detalle.idDetalle;
                option.textContent = detalle.talla + " - " + detalle.color;
                option.dataset.stock = detalle.stock;
                tallaSelect.appendChild(option);
            });
            tallaSelect.dispatchEvent(new Event("change"));
        })
        .catch(err => {
            console.error(err);
            Swal.fire("Error", "No se pudo cargar tallas", "error");
        });
    }
});

// ============================================
// TALLA -> STOCK + PRECIO
// ============================================
document.addEventListener("change", function(e){
    if(e.target.id === "tallaVenta"){
        let option = e.target.selectedOptions[0];
        if(!option || option.value === "") return;

        document.getElementById("stockVenta").value = option.dataset.stock;
        let producto = document.getElementById("productoVenta");
        let precio = producto.selectedOptions[0].dataset.precio;
        document.getElementById("precioVenta").value = precio;
    }
});

// ======================================
// CONFIGURACIÓN PASARELA DE PAGOS
// ======================================
function inicializarFormaPago() {
    $("#tipoPago").prop("disabled", true).val("CONTADO");
    $("#estadoPago").val("APROBADO");
    $("#divCodigoOperacion").addClass("d-none");
    $("#codigoOperacion").val("");
    $("#divCuotas, #divMontoCuota, #divFechaVencimiento").addClass("d-none");
    $("#cuotas").prop("disabled", true).val(1);
    $("#montoCuota").val("");
    $("#fechaPrimerVencimiento").prop("disabled", true).val("");
}

$(document).on("change", "#metodoPago", function () {
    let metodo = $(this).val();
    switch (metodo) {
        case "EFECTIVO":
            $("#tipoPago").val("CONTADO").prop("disabled", true);
            $("#estadoPago").val("APROBADO");
            ocultarCodigoOperacion();
            ocultarCredito();
            break;
        case "YAPE":
        case "PLIN":
        case "TRANSFERENCIA":
            $("#tipoPago").val("CONTADO").prop("disabled", true);
            $("#estadoPago").val("APROBADO");
            mostrarCodigoOperacion();
            ocultarCredito();
            break;
        case "TARJETA":
            $("#tipoPago").prop("disabled", false);
            if ($("#tipoPago").val() === "CREDITO") {
                $("#estadoPago").val("PENDIENTE");
                mostrarCredito();
                ocultarCodigoOperacion();
            } else {
                $("#estadoPago").val("APROBADO");
                mostrarCodigoOperacion();
                ocultarCredito();
            }
            break;
        default:
            ocultarCodigoOperacion();
            ocultarCredito();
            $("#tipoPago").val("CONTADO").prop("disabled", true);
            $("#estadoPago").val("APROBADO");
    }
});

$(document).on("change", "#tipoPago", function () {
    let tipo = $(this).val();
    let metodo = $("#metodoPago").val();

    if (tipo === "CREDITO") {
        $("#estadoPago").val("PENDIENTE");
        mostrarCredito();
        if (metodo === "TARJETA") ocultarCodigoOperacion();
    } else {
        $("#estadoPago").val("APROBADO");
        ocultarCredito();
        if (metodo === "TARJETA") mostrarCodigoOperacion();
    }
});

function mostrarCredito() {
    $("#divCuotas, #divMontoCuota, #divFechaVencimiento").removeClass("d-none");
    $("#cuotas, #fechaPrimerVencimiento").prop("disabled", false);
    calcularCuota();
    $("#cuotas").trigger("input");
    restringirFechasPasadas();
}

function ocultarCredito() {
    $("#divCuotas, #divMontoCuota, #divFechaVencimiento").addClass("d-none");
    $("#cuotas").prop("disabled", true).val(1);
    $("#montoCuota, #fechaPrimerVencimiento").val("");
}

function mostrarCodigoOperacion() {
    $("#divCodigoOperacion").removeClass("d-none");
}

function ocultarCodigoOperacion() {
    $("#divCodigoOperacion").addClass("d-none");
    $("#codigoOperacion").val("");
}

$(document).on("input", "#cuotas", function () {
    calcularCuota();
});

function calcularCuota() {
    let total = parseFloat($("#totalVenta").val()) || 0;
    let cuotas = parseInt($("#cuotas").val()) || 1;
    if (cuotas < 1) {
        cuotas = 1;
        $("#cuotas").val(1);
    }
    let monto = total / cuotas;
    $("#montoCuota").val(monto.toFixed(2));
}

// ======================================
// CONSULTA CLIENTE DNI (ESTRICTO)
// ======================================
function buscarCliente() {
    let dni = $("#dniCliente").val().trim();
    if (dni.length !== 8) {
        Swal.fire("DNI inválido", "Debe tener 8 dígitos", "warning");
        return;
    }

    fetch("/clientes/buscar/" + dni)
    .then(res => res.json())
    .then(data => {
        if (data.error) {
            Swal.fire("No encontrado", data.error, "warning");
            return;
        }
        $("#nombreCliente").val(data.nombres);
    })
    .catch(error => {
        console.error(error);
        Swal.fire("Error", "No se pudo consultar el DNI", "error");
    });
}

$(document).on("input", "#dniCliente", function () {
    let dni = $(this).val().replace(/\D/g, '');
    if (dni.length > 8) dni = dni.slice(0, 8);
    $(this).val(dni);
    if (dni.length === 8) buscarCliente();
});

$(document).on("input keyup", "#cantidadVenta", function () {
    let cantidad = parseInt($(this).val()) || 0;
    let stock = parseInt($("#stockVenta").val()) || 0;

    if (cantidad <= 0) {
        $(this).val(1);
        return;
    }
    if (stock > 0 && cantidad > stock) {
        Swal.fire({
            title: "Límite de stock",
            text: `Solo quedan ${stock} unidades en almacén.`,
            icon: "info",
            timer: 1800,
            showConfirmButton: false
        });
        $(this).val(stock);
    }
});

// ======================================
// ENVIAR / GUARDAR VENTA ASÍNCRONA
// ======================================
function guardarVenta() {
    let dni = $("#dniCliente").val().trim();
    let nombreCliente = $("#nombreCliente").val().trim();
    let metodoPago = $("#metodoPago").val();
    let tipoPago = $("#tipoPago").val();
    let codigoOperacion = $("#codigoOperacion").val().trim();
    let fechaVenc = $("#fechaPrimerVencimiento").val();
    let cuotas = parseInt($("#cuotas").val()) || 0;

    if (dni.length !== 8) {
        Swal.fire("Faltan Datos", "Debe ingresar un DNI válido de 8 dígitos.", "warning");
        return;
    }
    if (nombreCliente === "") {
        Swal.fire("Faltan Datos", "Debe buscar o asignar un cliente válido antes de proceder.", "warning");
        return;
    }
    if (carritoVenta.length === 0) {
        Swal.fire("Carrito Vacío", "Debe agregar al menos un producto a la lista de venta.", "warning");
        return;
    }
    if (!metodoPago || metodoPago === "") {
        Swal.fire("Forma de Pago Requerida", "Por favor, seleccione un método de pago.", "warning");
        return;
    }

    if ((metodoPago === "YAPE" || metodoPago === "PLIN" || metodoPago === "TRANSFERENCIA" || (metodoPago === "TARJETA" && tipoPago === "CONTADO")) && codigoOperacion === "") {
        Swal.fire("Código de Operación", "Este método de pago requiere registrar el código de operación.", "warning");
        return;
    }

    if (tipoPago === "CREDITO") {
        if (!fechaVenc || fechaVenc === "") {
            Swal.fire("Fecha requerida", "Por favor seleccione la fecha del primer vencimiento.", "warning");
            return;
        }
        if (cuotas <= 0) {
            Swal.fire("Cuotas inválidas", "El número de cuotas del crédito debe ser 1 o más.", "warning");
            return;
        }
    }

    let venta = {
        dni: dni,
        nombreCliente: nombreCliente,
        subtotal: parseFloat($("#subtotalVenta").text()) || 0,
        igv: parseFloat($("#igvVenta").text()) || 0,
        total: parseFloat($("#totalVenta").val()) || 0,
        metodoPago: metodoPago,
        tipoPago: tipoPago,
        codigoOperacion: tipoPago === "CREDITO" ? "" : codigoOperacion,
        cuotas: tipoPago === "CREDITO" ? cuotas : 0,
        montoCuota: tipoPago === "CREDITO" ? parseFloat($("#montoCuota").val()) : 0.0,
        fechaPrimerVencimiento: tipoPago === "CREDITO" ? fechaVenc : null,
        detalles: carritoVenta.map(item => ({
            idDetalle: parseInt(item.idDetalle),
            cantidad: parseInt(item.cantidad),
            precio: parseFloat(item.precio),
            subtotal: parseFloat(item.subtotal)
        }))
    };

    const btnGuardar = document.querySelector("button[onclick='guardarVenta()']");
    if(btnGuardar) btnGuardar.disabled = true;

    fetch("/ventas/guardar", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(venta)
    })
    .then(res => {
        if (!res.ok) return res.text().then(text => { throw new Error(text) });
        return res.json();
    })
    .then((data) => {
        Swal.fire({
            title: "¡Venta Registrada!",
            text: "El registro y sus cuotas se procesaron con éxito.",
            icon: "success",
            confirmButtonText: "Imprimir Voucher"
        }).then(() => {
            imprimirVoucherTermico(venta, data);
            let modalElement = document.getElementById("modalVenta");
            let modalInstance = bootstrap.Modal.getInstance(modalElement);
            if (modalInstance) modalInstance.hide();
            limpiarVenta();
            cargarVista('/ventas');
        });
    })
    .catch(error => {
        console.error(error);
        Swal.fire("Error", "No se pudo registrar la venta: " + error.message, "error");
        if(btnGuardar) btnGuardar.disabled = false;
    });
}

// ======================================
// GENERADOR DE IMPRESIÓN DE VOUCHER
// ======================================
function imprimirVoucherTermico(venta, respuestaBackend) {
    let ticketId = respuestaBackend.idVenta || Math.floor(100000 + Math.random() * 900000);
    let fechaActual = new Date().toLocaleString();
    let ventanaImpresion = window.open("", "_blank", "width=300,height=600");

    let htmlVoucher = `
    <html>
    <head>
        <title>Ticket Nro ${ticketId}</title>
        <style>
            @page { size: auto; margin: 0mm; }
            body { font-family: 'Courier New', monospace; font-size: 11px; line-height: 1.3; width: 260px; margin: 10px; }
            .text-center { text-align: center; }
            .text-right { text-align: right; }
            .linea { border-bottom: 1px dashed #000; margin: 8px 0; }
            table { width: 100%; border-collapse: collapse; }
            th { text-align: left; }
            .total-box { font-weight: bold; font-size: 12px; }
        </style>
    </head>
    <body>
        <div class="text-center">
            <strong>SISTEMA DE VENTAS</strong><br>Chiclayo, Perú<br>
            ================================<br>
            <strong>TICKET DE VENTA</strong><br>Nro: TKT-${ticketId}<br>Fecha: ${fechaActual}<br>
        </div>
        <div class="linea"></div>
        <div>
            <strong>Cliente:</strong> ${venta.nombreCliente}<br>
            <strong>DNI:</strong> ${venta.dni}<br>
            <strong>Condición:</strong> ${venta.tipoPago}<br>
            <strong>Método:</strong> ${venta.metodoPago}<br>
            ${venta.codigoOperacion ? `<strong>Op:</strong> ${venta.codigoOperacion}<br>` : ''}
        </div>
        <div class="linea"></div>
        <table>
            <thead><tr><th>Cant</th><th>Descripción</th><th class="text-right">Total</th></tr></thead>
            <tbody>
                ${carritoVenta.map(item => `
                    <tr>
                        <td>${item.cantidad}</td>
                        <td>${item.producto} (${item.talla})</td>
                        <td class="text-right">S/ ${item.subtotal.toFixed(2)}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
        <div class="linea"></div>
        <table class="total-box">
            <tr><td>Subtotal:</td><td class="text-right">S/ ${venta.subtotal.toFixed(2)}</td></tr>
            <tr><td>IGV (18%):</td><td class="text-right">S/ ${venta.igv.toFixed(2)}</td></tr>
            <tr><td>TOTAL:</td><td class="text-right">S/ ${venta.total.toFixed(2)}</td></tr>
        </table>
        ${venta.tipoPago === "CREDITO" ? `
            <div class="linea"></div>
            <div class="text-center">
                <strong>DETALLE DE CRÉDITO</strong><br>
                Nro Cuotas: ${venta.cuotas}<br>
                Monto Cuota: S/ ${venta.montoCuota.toFixed(2)}<br>
                1er Vencimiento: ${venta.fechaPrimerVencimiento}<br>
            </div>
        ` : ''}
        <div class="linea"></div>
        <div class="text-center"><br>¡Gracias por su compra!<br><br><br></div>
        <script>
            window.onload = function() { window.print(); setTimeout(function() { window.close(); }, 300); }
        <\/script>
    </body>
    </html>
    `;
    ventanaImpresion.document.write(htmlVoucher);
    ventanaImpresion.document.close();
}

function limpiarVenta() {
    carritoVenta = [];
    renderizarCarrito();
    $("#dniCliente").val("");
    $("#nombreCliente").val("");
    $("#productoVenta").val("");
    $("#tallaVenta").html('<option value="">Seleccione talla</option>');
    $("#stockVenta").val("");
    $("#precioVenta").val("");
    $("#cantidadVenta").val(1);
    $("#metodoPago").val("");
    ocultarCredito();
    ocultarCodigoOperacion();
    inicializarFormaPago();
}

function validarInputEnteroPositivo(selector) {
    $(selector).on("input keyup paste", function () {
        let valor = $(this).val().replace(/\D/g, '');
        if (valor === "" || parseInt(valor) <= 0) valor = 1;
        $(this).val(valor);
    });
}

// ==========================================================
// ACCIÓN 1: OJITO - VISUALIZAR VOUCHER IN SITU
// ==========================================================
function verVoucher(idVenta) {
    fetch("/ventas/buscar/" + idVenta)
    .then(res => {
        if(!res.ok) throw new Error("No se encontraron los detalles");
        return res.json();
    })
    .then(data => {
        let ventaEstructura = {
            nombreCliente: data.clienteNombre,
            dni: data.clienteDni,
            tipoPago: data.tipoPago,
            metodoPago: data.metodoPago,
            codigoOperacion: data.codigoOperacion,
            subtotal: data.subtotal,
            igv: data.igv,
            total: data.total,
            cuotas: data.cuotas,
            montoCuota: data.montoCuota,
            fechaPrimerVencimiento: data.fechaPrimerVencimiento
        };
        let respuestaMock = { idVenta: data.idVenta };
        let temporalCarrito = carritoVenta;

        carritoVenta = data.detalles.map(d => ({
            cantidad: d.cantidad,
            producto: d.producto,
            talla: d.talla,
            subtotal: d.subtotal
        }));

        imprimirVoucherTermico(ventaEstructura, respuestaMock);
        carritoVenta = temporalCarrito;
    })
    .catch(err => {
        console.error(err);
        Swal.fire("Error", "No se pudo cargar el voucher: " + err.message, "error");
    });
}

// ==========================================================
// ACCIÓN 2: CONTROL CRONOGRAMA DINÁMICO EN MULTI-CARDS
// ==========================================================
function verCuotasVenta(idVenta) {
    let contenedor = document.getElementById("contenedorCardsCuotas");
    contenedor.innerHTML = `<div class="col-12 text-center text-muted my-3"><div class="spinner-border spinner-border-sm text-primary" role="status"></div> Buscando estado de cuenta...</div>`;

    let miModal = new bootstrap.Modal(document.getElementById('modalVerCuotas'));
    miModal.show();

    fetch("/ventas/cuotas/" + idVenta)
    .then(res => res.json())
    .then(cuotas => {
        contenedor.innerHTML = "";

        if(cuotas.length === 0) {
            contenedor.innerHTML = `<div class="col-12 text-center text-danger py-3">No existen cuotas asociadas.</div>`;
            return;
        }

        document.getElementById("titularCuotaNombre").innerText = cuotas[0].clienteNombre;
        document.getElementById("titularCuotaDni").innerText = cuotas[0].clienteDni;

        let yaHabiliteUna = false;

        cuotas.forEach(c => {
            let esPagada = (c.estado === 'PAGADO');
            let botonHtml = "";
            let cardBordeClase = "";
            let bgHeaderClase = "";

            if (esPagada) {
                cardBordeClase = "border-success shadow-sm";
                bgHeaderClase = "bg-success text-white";
                botonHtml = `
                    <button class="btn btn-sm btn-outline-success w-100 fw-bold" disabled>
                        <i class="bi bi-check2-all"></i> Pagada
                    </button>
                `;
            } else {
                if (!yaHabiliteUna) {
                    yaHabiliteUna = true;
                    cardBordeClase = "border-warning shadow";
                    bgHeaderClase = "bg-warning text-dark";
                    botonHtml = `
                        <button class="btn btn-sm btn-warning w-100 fw-bold shadow-sm" onclick="procesarPagoCuota(${c.idCuota}, ${idVenta})">
                            <i class="bi bi-cash-coin"></i> Registrar Pago
                        </button>
                    `;
                } else {
                    cardBordeClase = "border-secondary opacity-75";
                    bgHeaderClase = "bg-secondary text-white";
                    botonHtml = `
                        <button class="btn btn-sm btn-light w-100 text-muted border" disabled title="Debe pagar la cuota anterior primero">
                            <i class="bi bi-lock-fill"></i> Bloqueado
                        </button>
                    `;
                }
            }

            contenedor.innerHTML += `
                <div class="col-md-6">
                    <div class="card h-100 ${cardBordeClase}">
                        <div class="card-header py-1 px-3 fw-bold d-flex justify-content-between align-middle ${bgHeaderClase}">
                            <span>CUOTA N° ${c.nroCuota}</span>
                            <small class="badge bg-dark rounded-pill">${c.estado}</small>
                        </div>
                        <div class="card-body p-3 d-flex flex-column justify-content-between bg-white">
                            <div class="mb-2">
                                <span class="text-muted small d-block">Código de Cuota de Pago:</span>
                                <span class="fw-mono text-dark d-block mb-2">#CUO-00${c.idCuota}</span>

                                <div class="d-flex justify-content-between align-items-center bg-light p-2 rounded mb-2">
                                    <span class="text-muted small">Importe:</span>
                                    <span class="fs-5 fw-bold text-success">S/ ${c.monto.toFixed(2)}</span>
                                </div>

                                <div class="small text-muted">
                                    <i class="bi bi-calendar-event text-danger"></i> Vence: <strong>${c.fechaVencimiento}</strong>
                                </div>
                            </div>
                            <div class="mt-2">
                                ${botonHtml}
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });
    })
    .catch(err => {
        console.error(err);
        contenedor.innerHTML = `<div class="col-12 text-center text-danger py-3">Error de comunicación.</div>`;
    });
}

// ==========================================================
// FUNCIÓN AUXILIAR: COBRAR LA CUOTA SECUENCIAL ACTIVA
// ==========================================================
function procesarPagoCuota(idCuota, idVenta) {
    Swal.fire({
        title: '¿Confirmar cobro?',
        text: `Se registrará el pago correspondiente al identificador #CUO-00${idCuota}.`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#198754',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, cobrar ahora',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch("/ventas/cuotas/pagar/" + idCuota, { method: "POST" })
            .then(res => {
                if(!res.ok) return res.text().then(text => { throw new Error(text) });
                return res.json();
            })
            .then(data => {
                if(data.success) {
                    Swal.fire({
                        title: '¡Pago Exitoso!',
                        text: data.message,
                        icon: 'success',
                        timer: 1500,
                        showConfirmButton: false
                    }).then(() => {
                        verCuotasVenta(idVenta);
                    });
                }
            })
            .catch(err => {
                console.error(err);
                Swal.fire("Error", err.message || "No se pudo completar la transacción.", "error");
            });
        }
    });
}

// ==========================================================
// ACCIÓN 3: ELIMINAR VENTA (ANULACIÓN MAESTRA)
// ==========================================================
function eliminarVenta(idVenta) {
    Swal.fire({
        title: '¿Anular venta seleccionada?',
        text: "La venta pasará a estado Inactivo y se restablecerán los productos al stock original.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#e3342f',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, anular',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch("/ventas/eliminar/" + idVenta, { method: "POST" })
            .then(res => res.json())
            .then(data => {
                if(data.success) {
                    Swal.fire({
                        title: '¡Anulada!',
                        text: 'El estado de la venta se actualizó a Inactivo.',
                        icon: 'success',
                        timer: 1500,
                        showConfirmButton: false
                    }).then(() => {
                        cargarVista('/ventas');
                    });
                } else {
                    Swal.fire("Error", "Operación fallida: " + data.message, "error");
                }
            })
            .catch(err => {
                console.error(err);
                Swal.fire("Error", "Error interno al conectar con el servidor.", "error");
            });
        }
    });
}

// ============================================
// MOSTRAR EL MODAL Y CARGAR LAS CUOTAS
// ============================================
function verCuotasVenta(idVenta) {
    // Escondemos el panel de pago por si se quedó abierto de otra consulta
    $("#seccionPagarCuota").addClass("d-none");
    $("#modalCodigoOpCuota").val("").addClass("d-none");

    fetch("/ventas/cuotas/" + idVenta)
    .then(res => res.json())
    .then(data => {
        // Asumiendo que el backend responde un objeto con los datos del cliente y su lista de cuotas
        $("#titularCuotaNombre").text(data.clienteNombre);
        $("#titularCuotaDni").text(data.clienteDni);

        let contenedor = document.getElementById("contenedorCardsCuotas");
        contenedor.innerHTML = "";

        data.cuotas.forEach(cuota => {
            let esPagado = cuota.estado === "PAGADO";
            let colorCard = esPagado ? "border-success bg-success-subtle" : "border-warning bg-white";

            let botonAccion = esPagado
                ? `<span class="badge bg-success w-100 p-2"><i class="bi bi-check-all"></i> Pagado (Op: ${cuota.codigoOperacion || 'Efectivo'})</span>`
                : `<button class="btn btn-warning btn-sm w-100 fw-bold" onclick="prepararFormularioPago(${cuota.idCuota}, ${cuota.nroCuota})">💸 Registrar Pago</button>`;

            contenedor.innerHTML += `
                <div class="col-md-6">
                    <div class="card ${colorCard} shadow-sm border-2">
                        <div class="card-body p-3">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <h6 class="fw-bold mb-0 text-dark">Cuota N° ${cuota.nroCuota}</h6>
                                <span class="badge ${esPagado ? 'bg-success' : 'bg-warning text-dark'}">${cuota.estado}</span>
                            </div>
                            <p class="mb-1 text-muted small">Monto: <strong class="text-dark">S/ ${cuota.monto.toFixed(2)}</strong></p>
                            <p class="mb-2 text-muted small">Vence: <strong>${cuota.fechaVencimiento}</strong></p>
                            ${cuota.fechaPago ? `<p class="mb-2 text-success small">Pagado el: ${cuota.fechaPago}</p>` : ''}
                            ${botonAccion}
                        </div>
                    </div>
                </div>
            `;
        });

        let modal = new bootstrap.Modal(document.getElementById('modalVerCuotas'));
        modal.show();
    })
    .catch(err => {
        console.error(err);
        Swal.fire("Error", "No se pudieron cargar las cuotas de la venta.", "error");
    });
}

// ============================================
// MOSTRAR INTERFAZ DE REGISTRO DE PAGO
// ============================================
function prepararFormularioPago(idCuota, nroCuota) {
    $("#idCuotaAProcesar").val(idCuota);
    $("#txtNroCuota").text(nroCuota);
    $("#seccionPagarCuota").removeClass("d-none");
    $("#modalMetodoPagoCuota").val("EFECTIVO").trigger("change");
}

// Controlar si el método elegido en la cuota requiere código de operación
$(document).on("change", "#modalMetodoPagoCuota", function() {
    let metodo = $(this).val();
    if(metodo === "EFECTIVO") {
        $("#modalCodigoOpCuota").addClass("d-none").val("");
    } else {
        $("#modalCodigoOpCuota").removeClass("d-none").focus();
    }
});

// ============================================
// ENVIAR EL PAGO AL BACKEND
// ============================================
function procesarPagoCuotaIndividual() {
    let idCuota = $("#idCuotaAProcesar").val();
    let metodoPago = $("#modalMetodoPagoCuota").val();
    let codigoOperacion = $("#modalCodigoOpCuota").val();

    // Estructuramos el payload en formato JSON para el @RequestBody del controlador
    let payload = {
        idCuota: parseInt(idCuota),
        metodoPago: metodoPago,
        codigoOperacion: codigoOperacion
    };

    fetch("/ventas/cuotas/pagar", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
    .then(res => {
        if (!res.ok) return res.text().then(text => { throw new Error(text) });
        return res.json();
    })
    .then(data => {
        if (data.success) {
            Swal.fire({
                title: "¡Pago Registrado!",
                text: data.message,
                icon: "success",
                confirmButtonText: "Imprimir Ticket Cuota"
            }).then(() => {
                // 1. DISPARAMOS EL NUEVO VOUCHER INDEPENDIENTE
                if (data.ticket) {
                    imprimirVoucherCuota(data.ticket);
                }

                // 2. Cerramos el modal de cuotas de forma limpia
                let modalCuotas = bootstrap.Modal.getInstance(document.getElementById('modalVerCuotas'));
                if (modalCuotas) modalCuotas.hide();

                // 3. Refrescamos la vista con la arquitectura SPA sin romper la navegación
                cargarVista('/ventas');
            });
        }
    })
    .catch(err => {
        console.error(err);
        Swal.fire("Error", err.message || "No se pudo procesar el cobro de la cuota.", "error");
    });
}
// ==========================================================
// MOSTRAR EL MODAL Y CARGAR LAS CUOTAS (CON REIMPRESIÓN DE VOUCHER)
// ==========================================================
function verCuotasVenta(idVenta) {
    $("#seccionPagarCuota").addClass("d-none");
    $("#modalCodigoOpCuota").val("").addClass("d-none");

    fetch("/ventas/cuotas/" + idVenta)
    .then(res => res.json())
    .then(data => {
        $("#titularCuotaNombre").text(data.clienteNombre);
        $("#titularCuotaDni").text(data.clienteDni);

        let contenedor = document.getElementById("contenedorCardsCuotas");
        contenedor.innerHTML = "";

        let bloquearSiguientes = false;

        // Calculamos totales acumulados en JS rápido para armar las reimpresiones de vouchers viejos
        let totalVentaMaestra = data.cuotas.reduce((acc, c) => acc + c.monto, 0);
        let totalPagadoHastaHoy = 0;

        data.cuotas.forEach(cuota => {
            let esPagado = cuota.estado === "PAGADO";
            let colorCard = "";
            let botonAccion = "";

            if (esPagado) {
                totalPagadoHastaHoy += cuota.monto;
                let saldoRestante = totalVentaMaestra - totalPagadoHastaHoy;

                colorCard = "border-success bg-success-subtle opacity-90";

                // Creamos un objeto JSON en texto plano seguro para pasarlo al botón de reimpresión
                let ticketObjeto = JSON.stringify({
                    idCuota: cuota.idCuota,
                    nroCuota: cuota.nroCuota,
                    montoPagado: cuota.monto,
                    fechaPago: cuota.fechaPago,
                    metodoPago: cuota.metodoPago || "EFECTIVO",
                    codigoOperacion: cuota.codigoOperacion || "EFECTIVO",
                    serieNumeroVenta: "REF-" + idVenta, // Identificador de referencia rápido
                    clienteNombre: data.clienteNombre,
                    clienteDni: data.clienteDni,
                    totalPagadoHastaHoy: totalPagadoHastaHoy,
                    saldoRestante: Math.max(0, saldoRestante),
                    totalVentaMaestra: totalVentaMaestra
                }).replace(/"/g, '&quot;');

                botonAccion = `
                    <div class="d-flex flex-column gap-1">
                        <span class="badge bg-success p-2"><i class="bi bi-check-all"></i> Pagado (${cuota.metodoPago || 'EFECTIVO'})</span>
                        <button class="btn btn-info btn-xs text-white fw-bold py-1" onclick="imprimirVoucherCuota(${ticketObjeto})">
                            <i class="bi bi-printer-fill"></i> Reimprimir Voucher
                        </button>
                    </div>`;
            } else {
                if (!bloquearSiguientes) {
                    colorCard = "border-warning bg-white shadow-sm";
                    botonAccion = `<button class="btn btn-warning btn-sm w-100 fw-bold shadow-sm" onclick="prepararFormularioPago(${cuota.idCuota}, ${cuota.nroCuota})">💸 Registrar Pago</button>`;
                    bloquearSiguientes = true;
                } else {
                    colorCard = "border-secondary bg-light text-muted opacity-50";
                    botonAccion = `<button class="btn btn-secondary btn-sm w-100 fw-bold" disabled><i class="bi bi-lock-fill"></i> Bloqueado</button>`;
                }
            }

            contenedor.innerHTML += `
                <div class="col-md-6">
                    <div class="card ${colorCard} border-2 mb-2">
                        <div class="card-body p-3">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <h6 class="fw-bold mb-0 text-dark">Cuota N° ${cuota.nroCuota}</h6>
                                <span class="badge ${esPagado ? 'bg-success' : (colorCard.includes('bg-light') ? 'bg-secondary' : 'bg-warning text-dark')}">${cuota.estado}</span>
                            </div>
                            <p class="mb-1 text-muted small">Monto: <strong class="text-dark">S/ ${cuota.monto.toFixed(2)}</strong></p>
                            <p class="mb-2 text-muted small">Vence: <strong>${cuota.fechaVencimiento}</strong></p>
                            ${cuota.fechaPago ? `<p class="mb-1 text-success small">Pagado el: ${cuota.fechaPago}</p>` : ''}
                            ${botonAccion}
                        </div>
                    </div>
                </div>
            `;
        });

        let modal = new bootstrap.Modal(document.getElementById('modalVerCuotas'));
        modal.show();
    })
    .catch(err => {
        console.error(err);
        Swal.fire("Error", "No se pudieron cargar las cuotas de la venta.", "error");
    });
}

// ==========================================================
// RENDER TÉRMICO COMPARTIDO (Misma tiquetera de 80mm)
// ==========================================================
function imprimirVoucherCuota(ticket) {
    const ventanaImpresion = window.open('', '_blank', 'width=300,height=600');
    ventanaImpresion.document.write(`
        <html>
        <head>
            <title>Recibo de Caja - Cuota N° ${ticket.nroCuota}</title>
            <style>
                @page { margin: 0; }
                body { font-family: 'Courier New', Courier, monospace; font-size: 12px; line-height: 1.4; width: 260px; margin: 10px auto; color: #000; }
                .text-center { text-align: center; }
                .text-right { text-align: right; }
                .bold { font-weight: bold; }
                .linea-divisoria { border-top: 1px dashed #000; margin: 8px 0; }
                .titulo-empresa { font-size: 16px; font-weight: bold; }
                .resumen-tabla { width: 100%; border-collapse: collapse; }
                .resumen-tabla td { padding: 2px 0; }
            </style>
        </head>
        <body>
            <div class="text-center">
                <div class="titulo-empresa">SISTEMA VENTAS</div>
                <p style="margin: 0;">Chiclayo, Perú</p>
                <p style="margin: 2px 0 0 0; font-weight:bold;">COMPROBANTE DE ABONO</p>
            </div>
            <div class="linea-divisoria"></div>
            <div>
                <strong>RECIBO CUOTA :</strong> #CUO-00${ticket.idCuota}<br>
                <strong>REF. INTERNA:</strong> ${ticket.serieNumeroVenta}<br>
                <strong>FECHA PAGO  :</strong> ${ticket.fechaPago}<br>
                <strong>MÉTODO PAGO :</strong> ${ticket.metodoPago}<br>
                <strong>CÓDIGO OP.  :</strong> ${ticket.codigoOperacion}<br>
            </div>
            <div class="linea-divisoria"></div>
            <div>
                <strong>CLIENTE:</strong> ${ticket.clienteNombre}<br>
                <strong>DNI:</strong> ${ticket.clienteDni}<br>
            </div>
            <div class="linea-divisoria"></div>
            <div class="text-center bold" style="font-size: 14px; margin: 5px 0;">
                MONTO PAGADO: S/ ${ticket.montoPagado.toFixed(2)}
            </div>
            <div class="linea-divisoria"></div>
            <table class="resumen-tabla">
                <tr><td>Total Crédito:</td><td class="text-right">S/ ${ticket.totalVentaMaestra.toFixed(2)}</td></tr>
                <tr><td>Total Amortizado:</td><td class="text-right" style="color: green;">S/ ${ticket.totalPagadoHastaHoy.toFixed(2)}</td></tr>
                <tr class="bold"><td>Saldo Pendiente:</td><td class="text-right">S/ ${ticket.saldoRestante.toFixed(2)}</td></tr>
            </table>
            <div class="linea-divisoria"></div>
            <div class="text-center" style="margin-top: 15px; font-size: 10px;">
                <p>¡Gracias por mantener tu cuenta al día!</p>
            </div>
            <script>
                window.onload = function() { window.print(); setTimeout(function() { window.close(); }, 300); };
            <\/script>
        </body>
        </html>
    `);
    ventanaImpresion.document.close();
}


// ==========================================================
// CONTROLADOR DE FILTROS EN TIEMPO REAL (FECHAS + SEARCH)
// ==========================================================
$(document).on("input change", "#filtroFechaDesde, #filtroFechaHasta, #filtroBuscadorGeneral", function() {
    filtrarTablaVentasMaster();
});

function filtrarTablaVentasMaster() {
    let fechaDesdeVal = $("#filtroFechaDesde").val(); // Formato: YYYY-MM-DD
    let fechaHastaVal = $("#filtroFechaHasta").val(); // Formato: YYYY-MM-DD
    let textoBusqueda = $("#filtroBuscadorGeneral").val().toLowerCase().trim();

    $(".fila-venta-registro").each(function() {
        let fila = $(this);

        // Captura de datos de las celdas
        let fechaCelda = fila.find("td:nth-child(2)").attr("data-fecha"); // Extrae "yyyy-MM-dd"
        let textoFila = fila.text().toLowerCase();

        let cumpleFecha = true;
        let cumpleTexto = true;

        // 1. Filtrado por rango de fechas
        if (fechaCelda) {
            if (fechaDesdeVal && fechaCelda < fechaDesdeVal) {
                cumpleFecha = false;
            }
            if (fechaHastaVal && fechaCelda > fechaHastaVal) {
                cumpleFecha = false;
            }
        }

        // 2. Filtrado por cadena de texto (Search general)
        if (textoBusqueda && !textoFila.includes(textoBusqueda)) {
            cumpleTexto = false;
        }

        // Mostrar u ocultar la fila en base al cumplimiento de ambas condiciones
        if (cumpleFecha && cumpleTexto) {
            fila.show();
        } else {
            fila.hide();
        }
    });
}

function limpiarFiltrosVentas() {
    $("#filtroFechaDesde").val("");
    $("#filtroFechaHasta").val("");
    $("#filtroBuscadorGeneral").val("");
    $(".fila-venta-registro").show(); // Restablece todas las filas visibles
}

// =========================================================================
// 1. CARGA DE DATOS EN EL MODAL DE EDICIÓN
// =========================================================================
function editarCliente(btn) {
    var id = btn.getAttribute('data-id');
    var dni = btn.getAttribute('data-dni');
    var nombres = btn.getAttribute('data-nombres');
    var apaterno = btn.getAttribute('data-apaterno');
    var amaterno = btn.getAttribute('data-amaterno');
    var telefono = btn.getAttribute('data-telefono');
    var correo = btn.getAttribute('data-correo');
    var direccion = btn.getAttribute('data-direccion');

    document.getElementById('edit_id_cliente').value = id;
    document.getElementById('edit_dni').value = dni;
    document.getElementById('edit_nombres').value = nombres;
    document.getElementById('edit_apellido_paterno').value = apaterno;
    document.getElementById('edit_apellido_materno').value = amaterno;
    document.getElementById('edit_telefono').value = telefono;
    document.getElementById('edit_correo').value = correo;
    document.getElementById('edit_direccion').value = direccion;

    var modal = new bootstrap.Modal(document.getElementById('modalEditarCliente'));
    modal.show();
}

// =========================================================================
// 2. AGREGAR NUEVO CLIENTE (ESTILO SPA CON SWEETALERT)
// =========================================================================
document.addEventListener("submit", function (e) {
    if (e.target.id === "formCliente") {
        e.preventDefault();

        let form = e.target;
        const dni = document.getElementById('reg_dni').value.trim();
        const telefono = form.querySelector('input[name="telefono"]').value.trim();
        const correo = form.querySelector('input[name="correo"]').value.trim();
        const direccion = form.querySelector('input[name="direccion"]').value.trim();

        // Validaciones previas
        if (!dni || !telefono || !correo || !direccion) {
            Swal.fire('Campos Incompletos', 'Por favor, complete todos los campos obligatorios.', 'warning');
            return;
        }

        const regexTelefono = /^[0-9]{9}$/;
        if (!regexTelefono.test(telefono)) {
            Swal.fire('Teléfono Inválido', 'Debe contener exactamente 9 números.', 'warning');
            return;
        }

        const regexGmail = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
        if (!regexGmail.test(correo)) {
            Swal.fire('Correo Inválido', 'Debe usar una cuenta válida de @gmail.com', 'warning');
            return;
        }

        let formData = new FormData(form);

        fetch("/clientes/guardar", {
            method: "POST",
            body: formData
        })
        .then(res => {
            if (!res.ok) throw new Error("Error HTTP " + res.status);
            return res.text(); // Leemos texto plano como en tu ejemplo de usuarios
        })
        .then(data => {
            if (data === "ok") {
                Swal.fire({
                    icon: 'success',
                    title: 'Correcto',
                    text: 'Cliente registrado con éxito',
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal = bootstrap.Modal.getInstance(document.getElementById('modalCliente'));
                modal?.hide();
                form.reset();
                cargarVista('/clientes'); // Recarga tu tabla asíncronamente
            } else if (data === "dni_existe") {
                Swal.fire('DNI Duplicado', 'El número de DNI ya está registrado.', 'error');
            } else {
                throw new Error("Respuesta inválida");
            }
        })
        .catch(err => {
            console.error(err);
            Swal.fire('Error', 'No se pudo guardar el cliente', 'error');
        });
    }
});

// =========================================================================
// 3. MODIFICAR CLIENTE (ESTILO SPA CON FORMDATA)
// =========================================================================
document.addEventListener("submit", function (e) {
    if (e.target.id === "formEditarCliente") {
        e.preventDefault();

        let form = e.target;
        const telefono = document.getElementById('edit_telefono').value.trim();
        const correo = document.getElementById('edit_correo').value.trim();
        const direccion = document.getElementById('edit_direccion').value.trim();

        if (!telefono || !correo || !direccion) {
            Swal.fire('Campos vacíos', 'No puede dejar campos obligatorios vacíos.', 'warning');
            return;
        }

        const regexTelefono = /^[0-9]{9}$/;
        if (!regexTelefono.test(telefono)) {
            Swal.fire('Teléfono Inválido', 'El teléfono debe tener 9 dígitos.', 'warning');
            return;
        }

        const regexGmail = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
        if (!regexGmail.test(correo)) {
            Swal.fire('Correo Inválido', 'El correo debe terminar en @gmail.com', 'warning');
            return;
        }

        // Enviamos el FormData completo. Spring lo mapeará directo a tu objeto cliente
        let formData = new FormData(form);

        fetch("/clientes/modificar", {
            method: "POST",
            body: formData
        })
        .then(res => {
            if (!res.ok) throw new Error("Error al actualizar");
            return res.text();
        })
        .then(data => {
            if (data === "ok") {
                Swal.fire({
                    icon: 'success',
                    title: 'Actualizado',
                    text: 'Datos modificados con éxito',
                    timer: 1500,
                    showConfirmButton: false
                });

                let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarCliente'));
                modal?.hide();
                form.reset();
                cargarVista('/clientes');
            } else {
                throw new Error("Error en servidor");
            }
        })
        .catch(err => {
            console.error(err);
            Swal.fire('Error', 'No se pudieron guardar los cambios', 'error');
        });
    }
});

// =========================================================================
// 4. CONSULTAR DNI DESDE LA API DE RENIEC (VINCULADO AL BOTÓN)
// =========================================================================
function consultarDniReniec() {
    const dniInput = document.getElementById('reg_dni');
    const dni = dniInput.value.trim();

    if (dni.length !== 8 || isNaN(dni)) {
        Swal.fire('DNI Inválido', 'Por favor, ingrese un número de DNI válido de 8 dígitos.', 'warning');
        return;
    }

    Swal.fire({
        title: 'Consultando Reniec...',
        text: 'Buscando datos del ciudadano',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });

    fetch('/clientes/buscar/' + dni)
    .then(res => {
        if (!res.ok) throw new Error('No se encontraron respuestas del servidor');
        return res.json();
    })
    .then(data => {
        Swal.close();

        if (data.error) {
            Swal.fire('Sin resultados', data.error, 'info');
            return;
        }

        if (data.origen === "BD") {
            Swal.fire('Cliente Encontrado', 'Este cliente ya se encuentra registrado localmente en el sistema.', 'info');
            document.getElementById('reg_nombres').value = data.nombres;
            document.getElementById('reg_apellido_paterno').value = '';
            document.getElementById('reg_apellido_materno').value = '';
        } else {
            Swal.fire('¡Éxito!', 'Datos obtenidos correctamente de la RENIEC.', 'success');

            // Si la API de Decolecta devuelve la cadena "full_name", la procesamos:
            if (data.nombres) {
                const partes = data.nombres.split(' ');
                if (partes.length >= 3) {
                    // Formato común: Paterno Materno Nombres
                    document.getElementById('reg_apellido_paterno').value = partes[0] || '';
                    document.getElementById('reg_apellido_materno').value = partes[1] || '';
                    document.getElementById('reg_nombres').value = partes.slice(2).join(' ') || '';
                } else {
                    document.getElementById('reg_nombres').value = data.nombres;
                    document.getElementById('reg_apellido_paterno').value = '';
                    document.getElementById('reg_apellido_materno').value = '';
                }
            }
        }
    })
    .catch(err => {
        Swal.close();
        console.error(err);
        Swal.fire('Error', 'No se logró conectar con el servicio de consulta.', 'error');
    });
}

// =========================================================================
// 5. BAJA LÓGICA (ELIMINAR CLIENTE CON SWEETALERT2)
// =========================================================================
function eliminarCliente(btn) {
    const id = btn.getAttribute('data-id');

    Swal.fire({
        title: '¿Estás seguro?',
        text: "El cliente pasará a un estado Inactivo.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Sí, dar de baja',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch('/clientes/eliminar/' + id, {
                method: 'POST'
            })
            .then(res => res.text())
            .then(data => {
                if (data === "ok") {
                    Swal.fire('Inactivado', 'El cliente ha sido deshabilitado del sistema.', 'success').then(() => {
                        if (typeof cargarVista === 'function') cargarVista('/clientes');
                        else location.reload();
                    });
                } else {
                    Swal.fire('Error', 'Hubo un percance al intentar desactivar el registro.', 'error');
                }
            });
        }
    });
}

// =========================================================================
// 6. REACTIVAR CLIENTE (ALTA LÓGICA CON SWEETALERT2)
// =========================================================================
function reactivarCliente(btn) {
    const id = btn.getAttribute('data-id');

    Swal.fire({
        title: '¿Reactivar cliente?',
        text: "El cliente volverá a figurar en el estado Activo.",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#198754',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, reactivar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch('/clientes/reactivar/' + id, {
                method: 'POST'
            })
            .then(res => res.text())
            .then(data => {
                if (data === "ok") {
                    Swal.fire('¡Restaurado!', 'El cliente se encuentra activo nuevamente.', 'success').then(() => {
                        if (typeof cargarVista === 'function') cargarVista('/clientes');
                        else location.reload();
                    });
                } else {
                    Swal.fire('Error', 'Error al intentar revertir el estado del cliente.', 'error');
                }
            });
        }
    });
}


// =========================================================================
// FUNCIÓN PARA BLOQUEAR NÚMEROS Y CARACTERES ESPECIALES (SÓLO LETRAS Y ESPACIOS)
// =========================================================================
function soloLetras(e) {
    // Captura el código de la tecla
    var key = e.keyCode || e.which;
    // Convierte el código a su carácter real
    var teclado = String.fromCharCode(key).toLowerCase();

    // Permitimos letras (a-z), espacio en blanco, y caracteres con tildes/ñ
    var letras = " abcdefghijklmnñopqrstuvwxyzáéíóú";

    // Teclas especiales (Retroceso, Tabulador, Flechas, Suprimir)
    var especiales = [8, 9, 37, 39, 46];
    var tecla_especial = false;

    for (var i in especiales) {
        if (key == especiales[i]) {
            tecla_especial = true;
            break;
        }
    }

    // Si no está en nuestra lista de letras permitidas y no es tecla especial, se bloquea
    if (letras.indexOf(teclado) == -1 && !tecla_especial) {
        e.preventDefault(); // Cancela la escritura
        return false;
    }
}

// =========================================================================
// CONSULTAR DNI DESDE LA API PARA EL FORMULARIO DE USUARIOS
// =========================================================================
function consultarDniUsuario() {
    const dniInput = document.getElementById('reg_dni');
    const dni = dniInput.value.trim();

    // Validación rápida antes de gastar una consulta a la API
    if (dni.length !== 8 || isNaN(dni)) {
        Swal.fire('DNI Inválido', 'Por favor, ingrese un número de DNI válido de 8 dígitos.', 'warning');
        return;
    }

    // Loader de SweetAlert mientras procesa
    Swal.fire({
        title: 'Consultando Reniec...',
        text: 'Buscando datos del ciudadano',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });

    // Apuntamos al endpoint de búsqueda que ya procesa la API en tu servidor
    fetch('/clientes/buscar/' + dni)
    .then(res => {
        if (!res.ok) throw new Error('No se encontraron respuestas del servidor');
        return res.json();
    })
    .then(data => {
        Swal.close(); // Cerramos el loader

        if (data.error) {
            Swal.fire('Sin resultados', data.error, 'info');
            return;
        }

        if (data.origen === "BD") {
            // Si el DNI ya existe en la tabla de clientes o local, te avisa, pero igual te trae el nombre
            Swal.fire('Información', 'Este DNI ya está registrado localmente en el sistema.', 'info');
            document.getElementById('reg_nombres').value = data.nombres;
        } else {
            // Si viene limpio de la API de RENIEC
            Swal.fire('¡Éxito!', 'Datos obtenidos correctamente de la RENIEC.', 'success');

            if (data.nombres) {
                // Seteamos el nombre completo directo en tu input de nombres del usuario
                document.getElementById('reg_nombres').value = data.nombres;
            }
        }
    })
    .catch(err => {
        Swal.close();
        console.error(err);
        Swal.fire('Error', 'No se logró conectar con el servicio de consulta.', 'error');
    });
}



// =========================================================================
// GESTIÓN DE INVENTARIO ASÍNCRONO (KARDEX)
// =========================================================================
// Guardamos en una variable global el botón que abrió el modal para poder actualizar su fila en caliente luego
let botonOrigenInventario = null;

function abrirModalInventario(btn) {
    botonOrigenInventario = btn; // 🌟 Guardamos la referencia del botón físico de la tabla principal
    let idProducto = btn.getAttribute("data-id");
    let nombre = btn.getAttribute("data-nombre");
    let detallesStr = btn.getAttribute("data-detalles");

    document.getElementById("inv_nombreProducto").innerText = nombre;
    document.getElementById("inv_nombreProducto").setAttribute("data-current-id", idProducto);

    let selectVariante = document.getElementById("inv_idDetalle");
    selectVariante.innerHTML = '<option value="">Seleccione Variante</option>';

    if (detallesStr) {
        let variantes = detallesStr.split(";");
        variantes.forEach(v => {
            let datos = v.split("|");
            if (datos.length >= 4) {
                let option = document.createElement("option");
                option.value = datos[0];
                // Guardamos los datos puros en atributos para reconstruirlos fácilmente al actualizar stock
                option.setAttribute("data-talla", datos[1]);
                option.setAttribute("data-color", datos[2]);
                option.setAttribute("data-stock-actual", datos[3]);
                option.innerText = `Talla: ${datos[1]} | Color: ${datos[2]} (Stock: ${datos[3]})`;
                selectVariante.appendChild(option);
            }
        });
    }

    document.getElementById("formInventarioMovimiento").reset();
    document.getElementById("divObservacion").style.display = "none";
    cargarHistorialAuditoria(idProducto);

    let modal = new bootstrap.Modal(document.getElementById("modalInventario"));
    modal.show();

    document.getElementById("inv_tipoMovimiento").disabled = true;
    document.getElementById("inv_tipoMovimientoHidden").value = "";
}

function cargarHistorialAuditoria(idProducto) {
    let tbody = document.getElementById("tbodyHistorialInventario");
    tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">Cargando historial...</td></tr>';

    fetch(`/inventario/producto/${idProducto}`)
        .then(res => res.json())
        .then(movimientos => {
            tbody.innerHTML = "";

            if (movimientos.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted p-3">No hay movimientos registrados para este producto.</td></tr>';
                return;
            }

            movimientos.forEach(m => {
                let fecha = new Date(m.fechaHora).toLocaleString('es-PE');
                let badgeClass = m.tipoMovimiento === 'ENTRADA' ? 'bg-success' : 'bg-danger';
                let cantidadSigno = m.tipoMovimiento === 'ENTRADA' ? `+${m.cantidad}` : `-${m.cantidad}`;
                let cantidadColor = m.tipoMovimiento === 'ENTRADA' ? 'text-success' : 'text-danger';

                // 🌟 BOTÓN DE OJITO SI EXISTE OBSERVACIÓN larguita
                let columnaObs = '<span class="text-muted">-</span>';
                if (m.observacion && m.observacion.trim() !== "") {
                    // Escapamos comillas por seguridad en el JS string
                    let obsEscapada = m.observacion.replace(/'/g, "\\'");
                    columnaObs = `
                        <div class="d-flex align-items-center gap-1">
                            <span class="text-truncate" style="max-width: 120px; display: inline-block;">${m.observacion}</span>
                            <button type="button" class="btn btn-sm btn-outline-dark py-0 px-1" onclick="verObservacionCompleta('${obsEscapada}')" title="Ver observación completa">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                    `;
                }

                let fila = `
                    <tr>
                        <td style="font-size: 13px;">${fecha}</td>
                        <td style="font-size: 13px;" class="fw-bold"><i class="bi bi-person mr-1"></i>${m.usuario}</td>
                        <td style="font-size: 13px;">Talla: ${m.productoDetalle.talla} | Color: ${m.productoDetalle.color}</td>
                        <td style="font-size: 13px;"><span class="badge ${badgeClass}">${m.tipoMovimiento}</span></td>
                        <td style="font-size: 13px;">${m.motivo}</td>
                        <td style="font-size: 13px;" class="fw-bold ${cantidadColor}">${cantidadSigno}</td>
                        <td style="font-size: 13px;">${columnaObs}</td>
                    </tr>
                `;
                tbody.innerHTML += fila;
            });
        })
        .catch(err => {
            console.error(err);
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-danger">Error al cargar el historial de auditoría.</td></tr>';
        });
}

// 🌟 FUNCIÓN AUXILIAR PARA EL OJITO CON SWEETALERT
function verObservacionCompleta(texto) {
    Swal.fire({
        title: 'Sustento del Movimiento',
        text: texto,
        icon: 'info',
        confirmButtonColor: '#212529',
        confirmButtonText: 'Entendido'
    });
}

// Interceptar el envío del formulario para aplicar el movimiento en tiempo real
document.addEventListener("submit", function (e) {
    if (e.target.id === "formInventarioMovimiento") {
        e.preventDefault();
        let form = e.target;

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        // 🌟 SOLUCIÓN AL ERROR ENTRADA/SALIDA: Jalamos los datos usando URLSearchParams
        let formData = new FormData(form);
        let tipoMovimientoReal = document.getElementById("inv_tipoMovimientoHidden").value;

        let params = new URLSearchParams();
        params.append("idDetalle", formData.get("idDetalle"));
        params.append("cantidad", formData.get("cantidad"));
        params.append("motivo", formData.get("motivo"));
        params.append("observacion", formData.get("observacion") || "");
        params.append("tipoMovimiento", tipoMovimientoReal); // 🌟 Aquí se inyecta correctamente "ENTRADA" o "SALIDA" sin nulos

        fetch("/inventario/guardar", {
            method: "POST",
            body: params,
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        })
        .then(res => res.text())
        .then(data => {
            if (data === "ok") {
                Swal.fire({ icon: 'success', title: 'Movimiento Procesado', text: 'El stock ha sido actualizado en tiempo real.', timer: 1500, showConfirmButton: false });

                // 🌟 LOGICA DE ACTUALIZACIÓN DINÁMICA DE RECUROS SIN RECARGAR PÁGINA
                let idDetalleSeleccionado = document.getElementById("inv_idDetalle").value;
                let cantidadDigitada = parseInt(formData.get("cantidad"));
                let selectVariante = document.getElementById("inv_idDetalle");
                let opcionSeleccionada = selectVariante.options[selectVariante.selectedIndex];

                // 1. Calcular nuevo stock en memoria
                let stockActual = parseInt(opcionSeleccionada.getAttribute("data-stock-actual"));
                let nuevoStock = (tipoMovimientoReal === "ENTRADA") ? (stockActual + cantidadDigitada) : (stockActual - cantidadDigitada);

                // 2. Actualizar el select del modal actual de forma inmediata
                opcionSeleccionada.setAttribute("data-stock-actual", nuevoStock);
                let talla = opcionSeleccionada.getAttribute("data-talla");
                let color = opcionSeleccionada.getAttribute("data-color");
                opcionSeleccionada.innerText = `Talla: ${talla} | Color: ${color} (Stock: ${nuevoStock})`;

                // 3. Re-construir el atributo "data-detalles" del botón original de la tabla para que si lo vuelves a abrir no tenga datos viejos
                if (botonOrigenInventario) {
                    let viejoDetallesStr = botonOrigenInventario.getAttribute("data-detalles");
                    let variantesArr = viejoDetallesStr.split(";");
                    let nuevosVariantesArr = variantesArr.map(v => {
                        let subDatos = v.split("|");
                        if (subDatos[0] === idDetalleSeleccionado) {
                            subDatos[3] = nuevoStock; // Actualizamos el stock en la cadena compacta
                        }
                        return subDatos.join("|");
                    });
                    let nuevoDetallesStr = nuevosVariantesArr.join(";");
                    botonOrigenInventario.setAttribute("data-detalles", nuevoDetallesStr);

                    // 4. Actualizar visualmente los badges de tallas en la fila correspondiente del DataTable
                    let filaContenedora = botonOrigenInventario.closest("tr");
                    if (filaContenedora) {
                        // Buscar todos los badges de tallas dentro de esa fila
                        let badges = filaContenedora.querySelectorAll("td div.flex-wrap span.badge");
                        badges.forEach(b => {
                            if (b.innerText.startsWith(talla + " ")) {
                                b.innerText = `${talla} (${nuevoStock})`;
                            }
                        });

                        // 5. recalcular y actualizar el badge negro del Stock Total de la fila
                        let todosLosBadgesDeLaFila = filaContenedora.querySelectorAll("td div.flex-wrap span.badge");
                        let sumaTotalFila = 0;
                        todosLosBadgesDeLaFila.forEach(b => {
                            let matches = b.innerText.match(/\(([^)]+)\)/);
                            if (matches && matches[1]) {
                                sumaTotalFila += parseInt(matches[1]);
                            }
                        });
                        let badgeTotalStock = filaContenedora.querySelector("td span.badge.bg-dark");
                        if (badgeTotalStock) {
                            badgeTotalStock.innerText = sumaTotalFila;
                        }
                    }
                }

                // Limpiar formulario y recargar historial abajo
                form.reset();
                document.getElementById("divObservacion").style.display = "none";

                let idProdActual = document.getElementById("inv_nombreProducto").getAttribute("data-current-id");
                if (idProdActual) {
                    cargarHistorialAuditoria(idProdActual);
                }
            } else {
                Swal.fire('Error de Inventario', data, 'error');
            }
        })
        .catch(err => {
            console.error(err);
            Swal.fire('Error Interno', 'No se pudo conectar con el servidor.', 'error');
        });
    }
});
function ajustarTipoPorMotivo() {
    let motivo = document.getElementById("inv_motivo").value;
    let selectTipo = document.getElementById("inv_tipoMovimiento");
    let hiddenTipo = document.getElementById("inv_tipoMovimientoHidden");
    let divObs = document.getElementById("divObservacion");
    let inputObs = document.getElementById("inv_observacion");

    if (motivo === "COMPRA MERCADERIA") {
        selectTipo.value = "ENTRADA";
        hiddenTipo.value = "ENTRADA";
        divObs.style.display = "none";
        inputObs.removeAttribute("required");
        inputObs.value = "";
    } else if (motivo === "MERMA / DAÑADO" || motivo === "SALIDA EXTRAORDINARIA") {
        selectTipo.value = "SALIDA";
        hiddenTipo.value = "SALIDA";

        if (motivo === "SALIDA EXTRAORDINARIA") {
            divObs.style.display = "block";
            inputObs.setAttribute("required", "required");
        } else {
            divObs.style.display = "none";
            inputObs.removeAttribute("required");
            inputObs.value = "";
        }
    } else {
        selectTipo.value = "";
        hiddenTipo.value = "";
        divObs.style.display = "none";
        inputObs.removeAttribute("required");
    }
}


function abrirModalVerificacionWeb(btn) {
    let idVenta = btn.getAttribute("data-id");
    let codigo = btn.getAttribute("data-codigo");
    let cliente = btn.getAttribute("data-cliente");
    let total = btn.getAttribute("data-total");
    let modo = btn.getAttribute("data-modo"); // PROCESAR o VER_AUDITORIA

    document.getElementById("idVentaVerificarHidden").value = idVenta;
    document.getElementById("txtClienteWebVerificar").innerText = cliente;
    document.getElementById("txtTotalWebVerificar").innerText = "S/ " + parseFloat(total).toFixed(2);
    document.getElementById("txtCodigoWebVerificar").innerText = codigo ? codigo : "SIN CÓDIGO";

    let divAuditoria = document.getElementById("divAuditoriaWeb");
    let btnAprobar = document.getElementById("btnAprobarVentaWebFinal");
    let txtAlerta = document.getElementById("txtAlertaVerificar");
    let headerModal = document.getElementById("headerModalWeb");
    let lblTitulo = document.getElementById("lblTituloModalWeb");

    if (modo === "PROCESAR") {
        // Modo Aprobación Activa
        divAuditoria.classList.add("d-none");
        btnAprobar.classList.remove("d-none");
        txtAlerta.classList.remove("d-none");
        headerModal.style.backgroundColor = "#6f42c1"; // Morado para verificar
        lblTitulo.innerText = "Confirmación de Operación Web";
    } else {
        // Modo Ver Historial / Auditoría
        let admin = btn.getAttribute("data-admin");
        let fecha = btn.getAttribute("data-fechaauditoria");

        document.getElementById("auditUser").innerText = admin;
        document.getElementById("auditFecha").innerText = fecha;

        divAuditoria.classList.remove("d-none");
        btnAprobar.classList.add("d-none"); // Ocultamos el botón porque ya está aprobado
        txtAlerta.classList.add("d-none");
        headerModal.style.backgroundColor = "#198754"; // Verde porque ya fue validado
        lblTitulo.innerText = "Auditoría de Venta Verificada";
    }

    // Instancia única estable
    let miModal = new bootstrap.Modal(document.getElementById("modalVerificarPagoWeb"));
    miModal.show();
}

function ejecutarAprobacionVentaWebFinal() {
    let idVenta = document.getElementById("idVentaVerificarHidden").value;

    // 1. Mostrar SweetAlert de carga mientras el backend procesa en la BD
    Swal.fire({
        title: 'Procesando aprobación...',
        text: 'Validando la transacción en el sistema.',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });

    fetch(`/ventas/aprobar-web/${idVenta}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text) });
        }
        return response.json();
    })
    .then(data => {
        // 1. Cerrar modal
        let modalElement = document.getElementById('modalVerificarPagoWeb');
        let modalInstancia = bootstrap.Modal.getInstance(modalElement);
        if (modalInstancia) modalInstancia.hide();

        // 2. SweetAlert con opción para enviar WhatsApp
        Swal.fire({
            icon: 'success',
            title: '¡Venta Aprobada!',
            text: 'La operation fue validada correctamente.',
            showCancelButton: true,
            confirmButtonColor: '#25d366', // Color verde WhatsApp
            confirmButtonText: '<i class="bi bi-whatsapp"></i> Enviar confirmación',
            cancelButtonColor: '#6f42c1',
            cancelButtonText: 'Solo cerrar'
        }).then((result) => {
            if (result.isConfirmed && data.telefonoCliente) {
                // Formatear mensaje
                let mensaje = encodeURIComponent("¡Hola! Tu compra ha sido aprobada y verificada exitosamente. Gracias por tu preferencia.");
                let telefono = data.telefonoCliente.replace(/\D/g, ''); // Limpiar solo números

                // Abrir en nueva pestaña
                window.open(`https://wa.me/51${telefono}?text=${mensaje}`, '_blank');
            }

            // Recargar vista después de decidir
            cargarVista('/ventas');
        });
    })
    .catch(error => {
        console.error("Error:", error);
        // SweetAlert estético en caso de error
        Swal.fire({
            icon: 'error',
            title: 'Error de Validación',
            text: error.message || 'No se pudo completar la aprobación.',
            confirmButtonColor: '#dc3545',
            confirmButtonText: 'Cerrar'
        });
    });
}

function ejecutarRechazoVentaWebFinal() {
    let idVenta = document.getElementById("idVentaVerificarHidden").value;
    let cliente = document.getElementById("txtClienteWebVerificar").innerText;

    Swal.fire({
        title: '¿Está seguro de rechazar esta venta?',
        text: `Se anulará la orden de ${cliente}. Los productos asignados seguirán disponibles en el inventario general.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, rechazar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            // Mostrar carga mientras procesa
            Swal.fire({
                title: 'Procesando rechazo...',
                text: 'Cancelando orden en el sistema.',
                allowOutsideClick: false,
                didOpen: () => { Swal.showLoading(); }
            });

            fetch(`/ventas/rechazar-web/${idVenta}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text) });
                }
                return response.json();
            })
            .then(data => {
                // Cerrar el modal de verificación
                let modalElement = document.getElementById('modalVerificarPagoWeb');
                let modalInstancia = bootstrap.Modal.getInstance(modalElement);
                if (modalInstancia) modalInstancia.hide();

                // Alerta de éxito con opción de enviar WhatsApp por código erróneo
                Swal.fire({
                    icon: 'error',
                    title: 'Venta Rechazada',
                    text: 'La orden fue cancelada sin alterar el stock del almacén.',
                    showCancelButton: true,
                    confirmButtonColor: '#25d366', // Verde WhatsApp
                    confirmButtonText: '<i class="bi bi-whatsapp"></i> Notificar al Cliente',
                    cancelButtonColor: '#6c757d',
                    cancelButtonText: 'Solo cerrar'
                }).then((resWsp) => {
                    if (resWsp.isConfirmed && data.telefonoCliente) {
                        let mensaje = encodeURIComponent(`Hola ${cliente}, te saludamos de la tienda. Tu orden web no pudo ser aprobada debido a que el código de operación enviado es incorrecto o no figura en nuestras cuentas. Tu pedido ha sido cancelado y los productos vuelven a estar disponibles. Si tuviste un inconveniente, puedes volver a intentarlo.`);
                        let telefono = data.telefonoCliente.replace(/\D/g, '');
                        window.open(`https://wa.me/51${telefono}?text=${mensaje}`, '_blank');
                    }
                    cargarVista('/ventas'); // Recargar tabla
                });
            })
            .catch(error => {
                console.error("Error:", error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error al rechazar',
                    text: error.message || 'No se pudo completar la acción.',
                    confirmButtonColor: '#dc3545'
                });
            });
        }
    });
}

// Al cargar el documento, inicializamos la vista apuntando exclusivamente al día de hoy
document.addEventListener("DOMContentLoaded", function () {
    establecerFiltroDelDiaInicial();
});

// Función matemática centralizadoraizada: Lee montos y cuenta filas VISIBLES
function calcularMontosDesdeTabla() {
    let totalMonto = 0, totalAprobado = 0, totalPendiente = 0;
    let cantMonto = 0, cantAprobado = 0, cantPendiente = 0;

    // Recorremos únicamente las filas que están visibles en la pantalla tras el filtro
    $(".fila-venta-registro:visible").each(function() {
        let fila = $(this);
        let celdaTotal = fila.find("td:nth-child(7) span");
        let celdaEstado = fila.find("td:nth-child(8) .badge");

        if (celdaTotal.length > 0) {
            let monto = parseFloat(celdaTotal.text()) || 0;

            // Suma y conteo General
            totalMonto += monto;
            cantMonto++;

            if (celdaEstado.length > 0) {
                let textoEstado = celdaEstado.text().trim();
                if (textoEstado === "Aprobado") {
                    totalAprobado += monto;
                    cantAprobado++;
                } else if (textoEstado.includes("Por Verificar")) {
                    totalPendiente += monto;
                    cantPendiente++;
                }
            }
        }
    });

    // Pintamos los montos de dinero en los Cards
    $("#cardMontoTotal").text("S/ " + totalMonto.toFixed(2));
    $("#cardMontoAprobado").text("S/ " + totalAprobado.toFixed(2));
    $("#cardMontoPendiente").text("S/ " + totalPendiente.toFixed(2));

    // Pintamos las cantidades de ventas físicas ejecutadas en los subtítulos
    $("#cardCantTotal").text(cantMonto);
    $("#cardCantAprobado").text(cantAprobado);
    $("#cardCantPendiente").text(cantPendiente);
}

// Forza el estado inicial para que cargue solo las ventas pertenecientes a la fecha actual
function establecerFiltroDelDiaInicial() {
    let hoy = new Date();
    let yyyy = hoy.getFullYear();
    let mm = String(hoy.getMonth() + 1).padStart(2, '0');
    let dd = String(hoy.getDate()).padStart(2, '0');
    let fechaHoyStr = `${yyyy}-${mm}-${dd}`;

    $(".fila-venta-registro").each(function() {
        let fechaCelda = $(this).find("td:nth-child(2)").attr("data-fecha");
        if (fechaCelda === fechaHoyStr) {
            $(this).show();
        } else {
            $(this).hide();
        }
    });

    $("#lblCardFecha").text("Monto del Día (" + dd + "/" + mm + ")");
    calcularMontosDesdeTabla();
}

// =========================================================================
// CARGA INICIAL Y CONFIGURACIÓN DE LA VISTA
// =========================================================================
$(document).ready(function () {
    // Al cargar la vista por primera vez, mostramos TODO y recalculamos las cards
    mostrarTodosLosRegistrosIniciales();

    // Si usas DataTables, esto fuerza a que se entere del cambio visual de inmediato
    if ($.fn.DataTable.isDataTable('#tabla_ventas')) {
        $('#tabla_ventas').DataTable().draw();
    }
});

// =========================================================================
// CONTROL DE LÍMITES LÓGICOS EN INPUTS DE FECHA
// =========================================================================
$(document).on("change", "#filtroFechaDesde", function() {
    let fechaDesde = $(this).val();
    // La "Fecha Hasta" no puede ser anterior a la "Fecha Desde"
    $("#filtroFechaHasta").attr("min", fechaDesde);
});

$(document).on("change", "#filtroFechaHasta", function() {
    let fechaHasta = $(this).val();
    // La "Fecha Desde" no puede ser posterior a la "Fecha Hasta"
    $("#filtroFechaDesde").attr("max", fechaHasta);
});

// =========================================================================
// CONTROLADOR DE FILTROS EN TIEMPO REAL (FECHAS + SEARCH) - JQUERY
// =========================================================================
$(document).on("input change", "#filtroFechaDesde, #filtroFechaHasta, #filtroBuscadorGeneral", function() {
    filtrarTablaVentasMaster();
});

function filtrarTablaVentasMaster() {
    let fechaDesdeVal = $("#filtroFechaDesde").val(); // Formato: YYYY-MM-DD
    let fechaHastaVal = $("#filtroFechaHasta").val(); // Formato: YYYY-MM-DD
    let textoBusqueda = $("#filtroBuscadorGeneral").val().toLowerCase().trim();

    // Cambiamos dinámicamente el título del Card principal según el estado del filtro
    if (fechaDesdeVal || fechaHastaVal || textoBusqueda) {
        $("#lblCardFecha").text("Monto Filtrado");
    } else {
        // Si borran los campos, mostramos todo sin filtrar por fecha inicial
        mostrarTodosLosRegistrosIniciales();
        return;
    }

    $(".fila-venta-registro").each(function() {
        let fila = $(this);

        // Captura de datos de las celdas
        let fechaCelda = fila.find("td:nth-child(2)").attr("data-fecha"); // Extrae "yyyy-MM-dd"
        let textoFila = fila.text().toLowerCase();

        let cumpleFecha = true;
        let cumpleTexto = true;

        // 1. Filtrado por rango de fechas
        if (fechaCelda) {
            if (fechaDesdeVal && fechaCelda < fechaDesdeVal) {
                cumpleFecha = false;
            }
            if (fechaHastaVal && fechaCelda > fechaHastaVal) {
                cumpleFecha = false;
            }
        }

        // 2. Filtrado por cadena de texto (Search general)
        if (textoBusqueda && !textoFila.includes(textoBusqueda)) {
            cumpleTexto = false;
        }

        // Mostrar u ocultar la fila en base al cumplimiento de ambas condiciones
        if (cumpleFecha && cumpleTexto) {
            fila.show();
        } else {
            fila.hide();
        }
    });

    // RECALCULO INSTANTÁNEO: Ejecuta la suma y conteo de lo que quedó visible en pantalla
    calcularMontosDesdeTabla();
}

// =========================================================================
// LÓGICA DE CARGA GLOBAL (SIN FILTRAR POR EL DÍA DE HOY)
// =========================================================================
function mostrarTodosLosRegistrosIniciales() {
    // Mostramos absolutamente todas las filas de la tabla de ventas
    $(".fila-venta-registro").show();

    // Colocamos un título descriptivo en la cabecera del panel de filtros
    $("#lblCardFecha").text("Monto General");

    // Ejecutamos el conteo matemático para que cargue con el total acumulado de tu BD
    calcularMontosDesdeTabla();
}

// =========================================================================
// FUNCIÓN MATEMÁTICA CENTRAL: SUMA TOTALES Y CUENTA FILAS VISIBLES
// =========================================================================
function calcularMontosDesdeTabla() {
    let totalMonto = 0, totalAprobado = 0, totalPendiente = 0;
    let cantMonto = 0, cantAprobado = 0, cantPendiente = 0;

    // Nuevos acumuladores para el reporte dinámico
    let totalSistema = 0, totalWeb = 0;
    let cantSistema = 0, cantWeb = 0;

    // Evaluamos exclusivamente los registros que pasaron el filtro (:visible)
    $(".fila-venta-registro:visible").each(function() {
        let fila = $(this);
        let celdaTotal = fila.find("td:nth-child(7) span");
        let celdaEstado = fila.find("td:nth-child(8) .badge");

        if (celdaTotal.length > 0) {
            let monto = parseFloat(celdaTotal.text().replace("S/", "").replace(",", "").trim()) || 0;

            // Sumatoria y Conteo General
            totalMonto += monto;
            cantMonto++;

            // DETECTAR ORIGEN (Web vs. Sistema)
            let tieneBotonAuditoriaWeb = fila.find("button[data-admin]").length > 0;
            let tieneBotonProcesarWeb = fila.find("button[data-modo='PROCESAR']").length > 0;

            let textoEstado = celdaEstado.length > 0 ? celdaEstado.text().trim() : "";
            let esVentaWeb = tieneBotonAuditoriaWeb || tieneBotonProcesarWeb || textoEstado.includes("Por Verificar");

            if (esVentaWeb) {
                totalWeb += monto;
                cantWeb++;
            } else {
                totalSistema += monto;
                cantSistema++;
            }

            // Segmentación por Estados de la Venta para las otras cards
            if (celdaEstado.length > 0) {
                if (textoEstado === "Aprobado") {
                    totalAprobado += monto;
                    cantAprobado++;
                } else if (textoEstado.includes("Por Verificar") || textoEstado.includes("Pendiente")) {
                    totalPendiente += monto;
                    cantPendiente++;
                }
            }
        }
    });

    // Renderizado dinámico de los montos económicos en los Cards
    $("#cardMontoTotal").text("S/ " + totalMonto.toFixed(2));
    $("#cardMontoAprobado").text("S/ " + totalAprobado.toFixed(2));
    $("#cardMontoPendiente").text("S/ " + totalPendiente.toFixed(2));

    // Renderizado de las tarjetas de Origen (Sistema / Web)
    $("#cardMontoSistema").text("S/ " + totalSistema.toFixed(2));
    $("#cardMontoWeb").text("S/ " + totalWeb.toFixed(2));

    // Renderizado dinámico de los subtítulos de cantidad
    $("#cardCantTotal").text(cantMonto);
    $("#cardCantAprobado").text(cantAprobado);
    $("#cardCantPendiente").text(cantPendiente);

    // Renderizado de cantidades de las tarjetas de Origen
    $("#cardCantSistema").text(cantSistema);
    $("#cardCantWeb").text(cantWeb);
}

// =========================================================================
// ACCIÓN DEL BOTÓN LIMPIAR FILTROS (CORREGIDO PARA MOSTRAR TODO)
// =========================================================================
function limpiarFiltrosVentas() {
    // 1. Blanqueamos los inputs del panel de control
    $("#filtroFechaDesde").val("");
    $("#filtroFechaHasta").val("");
    $("#filtroBuscadorGeneral").val("");

    // 2. Removemos las restricciones mínimas y máximas de los inputs de fecha
    $("#filtroFechaDesde").removeAttr("max");
    $("#filtroFechaHasta").removeAttr("min");

    // 3. Mostramos todos los registros y recalculamos cards con el acumulado completo
    mostrarTodosLosRegistrosIniciales();
}