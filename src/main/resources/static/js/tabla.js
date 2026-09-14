// Cambiar estado
function cambiarEstado(button, proyectoId) {
  fetch(`/empleador/cambiarEstado/${proyectoId}`, {
    //usamos fecth para enviar y recibir datos
    method: "POST",
    headers: {
      "X-Requested-With": "XMLHttpRequest",
      "Content-Type": "application/json",
    },
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.nuevoEstado) {
        button.textContent =
          data.nuevoEstado === "Abierto" ? "Cerrar" : "Abrir";
        const fila = button.closest("tr");
        fila.querySelector(".estado-proyecto").textContent = data.nuevoEstado;
      } else {
        alert("Error al cambiar el estado.");
      }
    })
    .catch(() => alert("Error al conectar con el servidor."));
}
// Función para mostrar toast
function mostrarToast(mensaje, tipo) {
  const toastContainer = document.getElementById("toastContainer");
  if (!toastContainer) return;

  const toastEl = document.createElement("div");
  toastEl.className = `toast align-items-center text-bg-${tipo} border-0`;
  toastEl.setAttribute("role", "alert");
  toastEl.setAttribute("aria-live", "assertive");
  toastEl.setAttribute("aria-atomic", "true");

  toastEl.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">${mensaje}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>
    </div>
  `;

  toastContainer.appendChild(toastEl);

  const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
  toast.show();

  toastEl.addEventListener("hidden.bs.toast", () => {
    toastEl.remove();
  });
}

// Eliminar proyecto
function eliminarProyecto(button, proyectoId) {
  const modalConfirmar = new bootstrap.Modal(
    document.getElementById("modalConfirmarEliminar")
  );
  modalConfirmar.show();

  const btnConfirmar = document.getElementById("btnConfirmarEliminar");
  const btnCancelar = document.getElementById("btnCancelarEliminar");

  btnConfirmar.replaceWith(btnConfirmar.cloneNode(true));
  btnCancelar.replaceWith(btnCancelar.cloneNode(true));

  const nuevoBtnConfirmar = document.getElementById("btnConfirmarEliminar");
  const nuevoBtnCancelar = document.getElementById("btnCancelarEliminar");

  nuevoBtnConfirmar.addEventListener("click", () => {
    modalConfirmar.hide();

    fetch(`/empleador/eliminarProyecto/${proyectoId}`, {
      method: "POST",
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ _method: "delete" }),
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.eliminado) {
          button.closest("tr").remove();
          mostrarToast("Proyecto eliminado con éxito", "success");
        } else {
          mostrarToast("Error al eliminar el proyecto.", "danger");
        }
      })
      .catch(() =>
        mostrarToast("Error al conectar con el servidor.", "danger")
      );
  });

  nuevoBtnCancelar.addEventListener("click", () => {
    modalConfirmar.hide();
  });
}

// Abrir modal para editar
function abrirModalEdicion(proyectoId) {
  fetch(`/empleador/proyecto/${proyectoId}`, {
    method: "GET",
    headers: { "X-Requested-With": "XMLHttpRequest" },
  })
    .then((res) => {
      if (!res.ok) throw new Error("No se pudo cargar el proyecto");
      return res.json();
    })
    .then((proyecto) => {
      // Rellenar campos del formulario del modal
      document.querySelector("#modalPublicarProyecto #titulo").value =
        proyecto.titulo || "";
      document.querySelector("#modalPublicarProyecto #descripcion").value =
        proyecto.descripcion || "";
      document.querySelector("#modalPublicarProyecto #categoria").value =
        proyecto.categoria || "";
      document.querySelector("#modalPublicarProyecto #presupuesto").value =
        proyecto.presupuesto || "";
      document.querySelector("#modalPublicarProyecto #modalidadPago").value =
        proyecto.modalidadPago || "";
      document.querySelector("#modalPublicarProyecto #ubicacion").value =
        proyecto.ubicacion || "";
      document.querySelector("#modalPublicarProyecto #fechaInicio").value =
        proyecto.fechaInicio || "";
      document.querySelector("#modalPublicarProyecto #fechaFinal").value =
        proyecto.fechaFinal || "";
      document.querySelector("#modalPublicarProyecto #modalidad").value =
        proyecto.modalidad || "";

      const form = document.querySelector("#formPublicarProyecto");
      form.action = `/empleador/editarProyecto/${proyectoId}`;

      // Abrir modal
      const modal = new bootstrap.Modal(
        document.getElementById("modalPublicarProyecto")
      );
      modal.show();
    })
    .catch((err) => alert("Error al cargar el proyecto: " + err.message));
}

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("formPublicarProyecto");
  const errorGeneral = document.getElementById("errorGeneral");

  //  Rango de fechas permitido: desde hoy hasta 1 año adelante
  const fechaMax = new Date();
  fechaMax.setFullYear(fechaMax.getFullYear() + 1);
  const maxFecha = fechaMax.toISOString().split("T")[0];

  document
    .getElementById("fechaInicio")
    .setAttribute("min", new Date().toISOString().split("T")[0]);
  document.getElementById("fechaInicio").setAttribute("max", maxFecha);
  document
    .getElementById("fechaFinal")
    .setAttribute("min", new Date().toISOString().split("T")[0]);
  document.getElementById("fechaFinal").setAttribute("max", maxFecha);

  // "publicar proyecto", formulario limpio
  const botonAbrirModal = document.getElementById("btnAbrirModalPublicar");
  const modalProyecto = new bootstrap.Modal(
    document.getElementById("modalPublicarProyecto")
  );

  if (botonAbrirModal) {
    botonAbrirModal.addEventListener("click", () => {
      const form = document.getElementById("formPublicarProyecto");

      // Limpiar formulario
      form.reset();

      // Restaurar acción original para publicar
      form.action = "/empleador/publicar";

      // Limpiar errores visuales
      const errorGeneral = document.getElementById("errorGeneral");
      errorGeneral.classList.add("d-none");
      errorGeneral.textContent = "";
    });
  }
  document
    .getElementById("modalPublicarProyecto")
    .addEventListener("hidden.bs.modal", () => {
      const form = document.getElementById("formPublicarProyecto");
      form.reset();
      form.action = "/empleador/publicar";

      const errorGeneral = document.getElementById("errorGeneral");
      errorGeneral.classList.add("d-none");
      errorGeneral.textContent = "";
    });

  if (form && errorGeneral) {
    form.addEventListener("submit", function (event) {
      event.preventDefault();

      // Limpiar errores previos
      errorGeneral.classList.add("d-none");
      errorGeneral.textContent = "";

      const errores = [];

      // Obtener valores
      const fechaInicioVal = document.getElementById("fechaInicio").value;
      const fechaFinalVal = document.getElementById("fechaFinal").value;
      const titulo = document.getElementById("titulo").value.trim();
      const categoria = document.getElementById("categoria").value;
      const presupuesto = document.getElementById("presupuesto").value;
      const modalidadPago = document.getElementById("modalidadPago").value;
      const ubicacion = document.getElementById("ubicacion").value.trim();
      const modalidad = document.getElementById("modalidad").value;

      // Validar campos
      if (titulo.length < 10)
        errores.push("El título debe tener al menos 10 caracteres.");
      if (!categoria) errores.push("Debe seleccionar una categoría.");

      //presupuesto
      const presupuestoNum = parseFloat(presupuesto);

      if (!presupuesto || isNaN(presupuestoNum)) {
        errores.push("El presupuesto es obligatorio.");
      } else if (presupuestoNum < 10) {
        errores.push("El presupuesto no puede ser menor a 10.");
      } else if (presupuestoNum > 50000) {
        errores.push("El presupuesto no puede exceder los 50,000.");
      }

      if (!modalidadPago)
        errores.push("Debe seleccionar una modalidad de pago.");
      if (ubicacion.length === 0) {
        errores.push("La ubicación es obligatoria.");
      } else if (ubicacion.length < 5) {
        errores.push("La ubicación debe tener al menos 5 caracteres.");
      } else if (!/^[a-zA-ZÁÉÍÓÚáéíóúñÑ0-9\s,.\-#]+$/.test(ubicacion)) {
        errores.push("La ubicación contiene caracteres no permitidos.");
      }

      if (!modalidad) errores.push("Debe seleccionar una modalidad.");
      if (!fechaInicioVal) errores.push("Debe seleccionar la fecha de inicio.");
      if (!fechaFinalVal) errores.push("Debe seleccionar la fecha final.");

      if (fechaInicioVal && fechaFinalVal) {
        const hoyStr = new Date().toISOString().split("T")[0]; // "YYYY-MM-DD"

        const unAnioDespues = new Date();
        unAnioDespues.setFullYear(unAnioDespues.getFullYear() + 1);
        const maxFechaStr = unAnioDespues.toISOString().split("T")[0]; // "YYYY-MM-DD"

        // Validación: la fecha de inicio no puede ser anterior a hoy
        if (fechaInicioVal < hoyStr) {
          errores.push("La fecha de inicio no puede ser anterior a hoy.");
        }

        // Validación: la fecha final debe ser posterior a la fecha de inicio
        if (fechaFinalVal <= fechaInicioVal) {
          errores.push(
            "La fecha final debe ser posterior a la fecha de inicio."
          );
        }

        // Validación: fecha de inicio no más de un año en el futuro
        if (fechaInicioVal > maxFechaStr) {
          errores.push(
            "La fecha de inicio no puede ser más de un año en el futuro."
          );
        }

        // Validación: fecha final no más de un año en el futuro
        if (fechaFinalVal > maxFechaStr) {
          errores.push(
            "La fecha de finalización no puede ser más de un año en el futuro."
          );
        }
      }

      if (errores.length > 0) {
        errorGeneral.innerHTML = errores.map((e) => `<div>${e}</div>`).join("");
        errorGeneral.classList.remove("d-none");
        return; // No enviar formulario si hay errores
      }

      // Si pasa validación, enviar formulario vía AJAX
      const formData = new FormData(form);

      fetch(form.action, {
        method: form.method,
        body: formData,
      })
        .then((response) => {
          if (!response.ok) throw new Error("Error al publicar el proyecto");
          return response.text();
        })
        .then(() => {
          // Mostrar modal de éxito
          const modalExitoElement = document.getElementById("modalExito");
          const modalExito = new bootstrap.Modal(modalExitoElement);
          modalExito.show();

          // Cerrar modal de publicar proyecto si está abierto
          const modalPublicar = bootstrap.Modal.getInstance(
            document.getElementById("modalPublicarProyecto")
          );
          if (modalPublicar) modalPublicar.hide();

          // resetear formulario
          form.reset();

          // Recargar página cuando se cierre el modal de éxito
          modalExitoElement.addEventListener(
            "hidden.bs.modal",
            () => {
              location.reload();
            },
            { once: true }
          );
        })
        .catch((error) => {
          errorGeneral.textContent = error.message;
          errorGeneral.classList.remove("d-none");
        });
    });

    // Limpiar errores cuando cambian fechas o campos importantes
    [
      "fechaInicio",
      "fechaFinal",
      "titulo",
      "categoria",
      "presupuesto",
      "modalidadPago",
      "ubicacion",
      "modalidad",
    ].forEach((id) => {
      const elem = document.getElementById(id);
      if (elem) {
        elem.addEventListener("change", () => {
          errorGeneral.classList.add("d-none");
          errorGeneral.textContent = "";
        });
      }
    });
  }
});

document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll("[data-modalidad]").forEach((el) => {
    const tipo = el.getAttribute("data-modalidad");
    const traduccion =
      {
        POR_PROYECTO: "por proyecto",
        POR_SEMANA: "por semana",
        POR_MES: "por mes",
        POR_DIA: "por día",
      }[tipo] || "sin especificar";

    const span = el.querySelector(".modalidad-text");
    if (span) span.textContent = `(${traduccion})`;
  });
});
