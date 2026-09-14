
function interpretarModalidadPago(valor) {
  switch (valor) {
    case "POR_PROYECTO":
      return "por el proyecto";
    case "POR_SEMANA":
      return "por semana";
    case "POR_MES":
      return "por mes";
    case "POR_DIA":
      return "por día";
    default:
      return valor;
  }
}

document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll(".categoria-opcion").forEach((item) => {
    item.addEventListener("click", function () {
      const categoriaId = this.getAttribute("data-categoria-id");

      // Verifica si categoriaId es válido
      if (!categoriaId) {
        console.error("categoriaId no es válido");
        return;
      }

      const categoriaTexto =
        this.getAttribute("data-categoria") || "Filtrar por categorías";
      document.getElementById("categoriaTexto").textContent = categoriaTexto;

      // Hacer fetch a la ruta del filtro
      fetch(
        `/proyectos/empleador/filtrar?categoriaId=${encodeURIComponent(
          categoriaId
        )}`
      )
        .then((response) => response.json())
        .then((data) => {
          const lista = document.getElementById("listaProyectos");
          const panelDetalle = document.getElementById("panelDetalle");
          const contenido = document.getElementById("contenidoProyecto");
          const panelIzquierdo = document.querySelector(".col-md-4"); // Panel izquierdo
          lista.innerHTML = ""; // Limpiar la lista de proyectos

          // Eliminar cualquier contenedor extra si existe
          document.querySelectorAll('.extra-contenedor').forEach((el) => el.remove());

          // Si no hay proyectos, mostrar mensaje y ocultar ambos paneles
          if (!data || data.length === 0) {
            lista.innerHTML = `
              <div class="d-flex flex-column justify-content-center align-items-center text-muted text-center" style="min-height: 300px;">
                <p class="mb-0">
                  No hay proyectos para la categoría <strong>${categoriaTexto}</strong>.
                </p>
              </div>
            `;
            // Ocultar panel izquierdo y derecho si no hay proyectos
            if (panelIzquierdo) panelIzquierdo.classList.add("d-none");
            if (panelDetalle) panelDetalle.classList.add("d-none");
            if (contenido) contenido.innerHTML = ""; // Limpiar contenido del panel derecho
            return;
          }

          // Si hay proyectos, mostrar ambos paneles
          if (panelIzquierdo) panelIzquierdo.classList.remove("d-none");
          if (panelDetalle) panelDetalle.classList.remove("d-none");

          // Agregar proyectos a la lista
          data.forEach((proy, index) => {
            const item = document.createElement("a");
            item.href = "#";
            item.className =
              "list-group-item list-group-item-action rounded-0 py-3" +
              (index === 0 ? " active" : "");
            item.addEventListener("click", (e) => {
              e.preventDefault();
              seleccionarProyecto(item, proy.id);
            });
            item.innerHTML = `
              <div class="d-flex w-100 justify-content-between">
                <h6 class="mb-1 fw-semibold">${proy.titulo}</h6>
                <small><i class="fas fa-clock me-1"></i><span class="fecha-relativa" data-fecha="${proy.fechaPublicacion}"></span></small>
              </div>
              <small><i class="fas fa-map-marker-alt me-1 text-danger"></i> ${proy.ubicacion}</small>
            `;
            lista.appendChild(item);
          });

          // Actualizar fechas relativas
          document.querySelectorAll(".fecha-relativa").forEach((el) => {
            const fechaTexto = el.dataset.fecha;
            const fecha = new Date(fechaTexto);
            const ahora = new Date();
            const diffMs = ahora - fecha;
            const diffMin = Math.floor(diffMs / 60000);
            const diffHoras = Math.floor(diffMin / 60);
            const diffDias = Math.floor(diffHoras / 24);

            let resultado = "";
            if (diffMin < 1) resultado = "hace un momento";
            else if (diffMin < 60) resultado = `hace ${diffMin} min`;
            else if (diffHoras < 24) resultado = `hace ${diffHoras} h`;
            else resultado = `hace ${diffDias} día${diffDias > 1 ? "s" : ""}`;

            el.textContent = resultado;
          });

          // Cargar automáticamente el primer proyecto
          const primerProyecto = data[0];
          const primerItem = document.querySelector(
            "#listaProyectos .list-group-item"
          );
          if (primerItem && primerProyecto) {
            if (panelDetalle) panelDetalle.classList.remove("d-none");
            if (contenido) contenido.innerHTML = ""; // Limpiar contenido anterior
            seleccionarProyecto(primerItem, primerProyecto.id);
          }
        })
        .catch((error) => {
          console.error("Error al obtener proyectos filtrados:", error);
        });
    });
  });
});

let proyectoSeleccionadoId = null;

function seleccionarProyecto(item, id) {
  // Marcar el proyecto activo
  document
    .querySelectorAll(".list-group-item")
    .forEach((el) => el.classList.remove("active"));
  item.classList.add("active");

  proyectoSeleccionadoId = id;

  const contenedor = document.getElementById("contenidoProyecto");
  const template = document.getElementById("templatePostulado");

  if (!contenedor || !template) {
    console.warn("Faltan elementos requeridos.");
    return;
  }

  contenedor.setAttribute("data-proyecto-id", id);

  // Primero verificar si ya está postulado
  fetch(`/postulaciones/existe?proyectoId=${id}`)
    .then((res) => res.json())
    .then((data) => {
      if (data.yaPostulado) {
        // Mostrar mensaje si ya está postulado
        const nuevoContenido = template.content.cloneNode(true);
        contenedor.replaceChildren(nuevoContenido);
        return;
      }
fetch(`/api/proyectos/${id}`)
  .then((response) => response.json())
  .then((data) => {
    contenedor.innerHTML = `
  <div id="detalleProyecto" class="bg-white rounded-3 p-4" style="max-height: 80vh; overflow-y: auto">
    <div class="d-flex align-items-start justify-content-between mb-3">
      <h4 id="tituloProyecto" class="section-title mb-0 text-body">
        <i class="fas fa-clipboard-list me-2" style="color: #0d6efd"></i>
        <span id="tituloTexto">${data.titulo}</span>
      </h4>
    </div>

    <ul class="list-unstyled small mb-0 text-secondary meta-list">
      <li class="mb-2">
        <span class="text-body fw-semibold">
          <i class="fas fa-align-left me-2" style="color: #198754"></i>Descripción:
        </span>
        <span id="descProyecto" class="multiline"></span>
      </li>

      <li class="mb-2">
        <span class="text-body fw-semibold">
          <i class="fas fa-tag me-2" style="color: #6f42c1"></i>Categoría:
        </span>
        <span id="catProyecto">${data.categoria?.nombre ?? "Sin categoría"}</span>
      </li>

      <li class="mb-2">
        <span class="text-body fw-semibold">
          <i class="fas fa-map-marker-alt me-2" style="color: #dc3545"></i>Ubicación:
        </span>
        <span id="ubiProyecto">${data.ubicacion}</span>
      </li>

      <li class="mb-2">
        <span class="text-body fw-semibold">
          <i class="fas fa-coins me-2" style="color: #ffc107"></i>Pago:
        </span>
        <span id="pagoProyecto">S/ ${data.presupuesto} ${interpretarModalidadPago(data.modalidadPago)}</span>
      </li>

      <li class="mb-2">
        <span class="text-body fw-semibold">
          <i class="fas fa-calendar-plus me-2" style="color: #20c997"></i>Inicio:
        </span>
        <span id="inicioProyecto">${data.fechaInicio}</span>
      </li>

      <li class="mb-2">
        <span class="text-body fw-semibold">
          <i class="fas fa-calendar-check me-2" style="color: #0dcaf0"></i>Final:
        </span>
        <span id="finProyecto">${data.fechaFinal}</span>
      </li>

      <li>
        <span class="text-body fw-semibold">
          <i class="fas fa-laptop me-2" style="color: #6c757d"></i>Modalidad:
        </span>
        <span id="modProyecto">${data.modalidad}</span>
      </li>
    </ul>

    <form id="formPostulacion" method="post" class="d-flex gap-2 flex-wrap mt-3">
      <input type="hidden" id="montoContraofertaInput" name="montoContraoferta" />
      <button type="submit" class="btn btn-dark btn-sm">
        <i class="fas fa-paper-plane me-1"></i>Postular
      </button>
      <button type="button" class="btn btn-outline-secondary btn-sm" id="btnContraoferta">
        <i class="fas fa-hand-holding-usd me-1"></i>Enviar contraoferta
      </button>
    </form>

    <div id="contenedorInfoContraoferta" class="mt-3"></div>
  </div>
`;



    const descEl = document.getElementById("descProyecto");
    if (descEl) {
      descEl.textContent = data.descripcion ?? "";
    }

          // Reasignar evento para botón de contraoferta
          const btnLanzador = document.getElementById("btnContraoferta");
          if (btnLanzador) {
            btnLanzador.addEventListener("click", () => {
              document.getElementById("montoNuevo").value = "";
              const modal = new bootstrap.Modal(
                document.getElementById("modalContraoferta")
              );
              modal.show();
            });
          }

          // Reasignar evento de envío del formulario
          asignarEnvioFormulario();
        })
        .catch((error) =>
          console.error("Error al cargar detalles del proyecto:", error)
        );
    })
    .catch((err) =>
      console.error("Error al verificar si ya está postulado:", err)
    );
}

// SECCIÓN: Modal de contraoferta
function mostrarModalContraoferta() {
  document.getElementById("montoNuevo").value = "";
  const modal = new bootstrap.Modal(
    document.getElementById("modalContraoferta")
  );
  modal.show();
}

function confirmarContraoferta() {
  const monto = document.getElementById("montoNuevo").value;
  if (monto && parseFloat(monto) > 0 && proyectoSeleccionadoId) {
    const form = document.createElement("form");
    form.method = "post";
    form.action = `/postulaciones/${proyectoSeleccionadoId}`;

    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "montoContraoferta";
    input.value = monto;

    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
  }
}

// SECCIÓN: Fecha relativa
document.querySelectorAll(".fecha-relativa").forEach((el) => {
  const fechaTexto = el.dataset.fecha;
  const fecha = new Date(fechaTexto);
  const ahora = new Date();
  const diffMs = ahora - fecha;
  const diffMin = Math.floor(diffMs / 60000);
  const diffHoras = Math.floor(diffMin / 60);
  const diffDias = Math.floor(diffHoras / 24);

  let resultado = "";
  if (diffMin < 1) resultado = "justo ahora";
  else if (diffMin < 60) resultado = `hace ${diffMin} min`;
  else if (diffHoras < 24) resultado = `hace ${diffHoras} h`;
  else resultado = `hace ${diffDias} día${diffDias > 1 ? "s" : ""}`;

  el.textContent = resultado;
});

// SECCIÓN: Eventos al cargar la página

document.addEventListener("DOMContentLoaded", () => {
  // Activar automáticamente primer proyecto
  const primeraOpcion = document.querySelector(
    "#listaProyectos .list-group-item"
  );
  if (primeraOpcion) primeraOpcion.click();

  // Eventos del botón de contraoferta
  const btnLanzador = document.getElementById("btnContraoferta");
  if (btnLanzador) {
    btnLanzador.addEventListener("click", () => {
      document.getElementById("montoNuevo").value = "";
      const modal = new bootstrap.Modal(
        document.getElementById("modalContraoferta")
      );
      modal.show();
    });
  }

  const btnConfirmar = document.getElementById("btnConfirmarContraoferta");
  if (btnConfirmar) {
    btnConfirmar.addEventListener("click", () => {
      const monto = parseFloat(document.getElementById("montoNuevo").value);

      // Obtener el monto del proyecto desde el texto
      const textoPago = document.getElementById("pagoProyecto").textContent;
      const montoOriginal = parseFloat(textoPago.replace("S/", "").trim());

      if (!isNaN(monto) && monto > 0) {
        if (monto < montoOriginal) {
          mostrarToast(
            `⚠️ La contraoferta no puede ser menor al monto del proyecto (S/ ${montoOriginal.toFixed(
              2
            )}).`
          );

          return;
        }

        // Insertar en campo oculto del formulario
        const inputOculto = document.getElementById("montoContraofertaInput");
        inputOculto.value = monto;

        // Eliminar mensaje previo si existe
        const avisoPrevio = document.getElementById("infoContraoferta");
        if (avisoPrevio) avisoPrevio.remove();

        // Crear nuevo aviso
        const aviso = document.createElement("div");
        aviso.id = "infoContraoferta";
        aviso.className = "alert alert-info mt-3 py-2 px-3 small";
        aviso.innerHTML = `<strong>💸 Contraoferta cargada:</strong> S/ ${monto.toFixed(
          2
        )}`;

        // Insertar directamente en el panel de detalle
        const panelDetalle = document.getElementById("detalleProyecto");
        if (panelDetalle) panelDetalle.appendChild(aviso);

        // Cerrar el modal
        bootstrap.Modal.getInstance(
          document.getElementById("modalContraoferta")
        ).hide();
      } else {
        mostrarToast(
          "⚠️ Ingresa un monto válido mayor a cero para tu contraoferta."
        );
      }
    });
  }

  // Interceptar envío del formulario para evitar recarga
  const formPostulacion = document.getElementById("formPostulacion");
  if (formPostulacion) {
    formPostulacion.addEventListener("submit", function (e) {
      e.preventDefault(); // Previene recarga

      if (!proyectoSeleccionadoId) return;

      fetch(`/postulaciones/existe?proyectoId=${proyectoSeleccionadoId}`)
        .then((res) => res.json())
        .then((data) => {
          if (data.yaPostulado) {
            const modal = new bootstrap.Modal(
              document.getElementById("modalYaPostulado")
            );
            modal.show();
          } else {
            // Envío con fetch sin recargar la página
            const monto =
              document.getElementById("montoContraofertaInput").value || "";

            fetch(`/postulaciones/${proyectoSeleccionadoId}`, {
              method: "POST",
              headers: {
                "Content-Type": "application/x-www-form-urlencoded",
              },
              body: new URLSearchParams({
                montoContraoferta: monto,
              }),
            })
              .then((res) => {
                if (res.ok) {
                  const avisoPrevio =
                    document.getElementById("infoContraoferta");
                  if (avisoPrevio) avisoPrevio.remove();

                  const modal = new bootstrap.Modal(
                    document.getElementById("modalPostulacionExitosa")
                  );
                  modal.show();
                  window.history.replaceState(
                    {},
                    document.title,
                    window.location.pathname
                  );
                } else {
                  throw new Error("Error en la postulación");
                }
              })
              .catch((error) => {
                console.error("Error al enviar postulación:", error);
              });
          }
        })
        .catch((error) => {
          console.error("Error al verificar postulación:", error);
        });
    });
  }

  // Mostrar modal de éxito
  const params = new URLSearchParams(window.location.search);
  if (params.get("postulacionExitosa") === "true") {
    const modal = new bootstrap.Modal(
      document.getElementById("modalPostulacionExitosa")
    );
    modal.show();
  }

  // Mostrar modal si ya está postulado
  if (params.get("yaPostulado") === "true") {
    const modal = new bootstrap.Modal(
      document.getElementById("modalYaPostulado")
    );
    modal.show();
  }

  // Eventos de cierre para los botones de los modales
  const btnExito = document.getElementById("btnIrAFree");
  if (btnExito) {
    btnExito.addEventListener("click", () => {
      const modal = bootstrap.Modal.getInstance(
        document.getElementById("modalPostulacionExitosa")
      );
      if (modal) modal.hide();
      window.history.replaceState({}, document.title, window.location.pathname);
    });
  }

  const btnYaPostulado = document.getElementById("btnExplorarOfertas");
  if (btnYaPostulado) {
    btnYaPostulado.addEventListener("click", () => {
      const modal = bootstrap.Modal.getInstance(
        document.getElementById("modalYaPostulado")
      );
      if (modal) modal.hide();
      window.history.replaceState({}, document.title, window.location.pathname);
    });
  }
});
function asignarEnvioFormulario() {
  const formPostulacion = document.getElementById("formPostulacion");
  if (formPostulacion) {
    formPostulacion.addEventListener("submit", function (e) {
      e.preventDefault();

      if (!proyectoSeleccionadoId) return;

      fetch(`/postulaciones/existe?proyectoId=${proyectoSeleccionadoId}`)
        .then((res) => res.json())
        .then((data) => {
          if (data.yaPostulado) {
            const modal = new bootstrap.Modal(
              document.getElementById("modalYaPostulado")
            );
            modal.show();
          } else {
            const monto =
              document.getElementById("montoContraofertaInput").value || "";

            fetch(`/postulaciones/${proyectoSeleccionadoId}`, {
              method: "POST",
              headers: {
                "Content-Type": "application/x-www-form-urlencoded",
              },
              body: new URLSearchParams({
                montoContraoferta: monto,
              }),
            })
              .then((res) => {
                if (res.ok) {
                  //  Limpia la contraoferta cargada
                  const avisoPrevio =
                    document.getElementById("infoContraoferta");
                  if (avisoPrevio) avisoPrevio.remove();

                  const modal = new bootstrap.Modal(
                    document.getElementById("modalPostulacionExitosa")
                  );
                  modal.show();
                  window.history.replaceState(
                    {},
                    document.title,
                    window.location.pathname
                  );
                } else {
                  throw new Error("Error en la postulación");
                }
              })
              .catch((error) => {
                console.error("Error al enviar postulación:", error);
              });
          }
        });
    });
  }
}

//MOSTRAR TOAST
function mostrarToast(mensaje, tipo = "warning") {
  const toastContainer = document.getElementById("toastContainer");

  // Asegurar que el contenedor exista
  if (!toastContainer) {
    console.error("No se encontró #toastContainer en el DOM");
    return;
  }

  // ID DE CADA TOAST
  const toastId = `toast-${Date.now()}-${Math.floor(Math.random() * 1000)}`;

  const toast = document.createElement("div");
  toast.id = toastId;
  toast.className = `toast align-items-center text-bg-${tipo} border-0 show mb-2`;
  toast.setAttribute("role", "alert");
  toast.setAttribute("aria-live", "assertive");
  toast.setAttribute("aria-atomic", "true");

  toast.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">${mensaje}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>
    </div>
  `;

  toastContainer.appendChild(toast);

  // Desaparecer automáticamente el toast después de 5 segundos
  setTimeout(() => {
    const toastEl = document.getElementById(toastId);
    if (toastEl) toastEl.remove();
  }, 5000);
}
//mensaje ya estas postulado
function verificarPostulacion() {
  const contenedor = document.getElementById("contenidoProyecto");
  const template = document.getElementById("templatePostulado");

  if (!contenedor || !template) {
    console.warn("Faltan elementos clave para verificar postulación.");
    return;
  }

  const proyectoId = contenedor.getAttribute("data-proyecto-id");
  if (!proyectoId) {
    console.warn("No se encontró data-proyecto-id en #contenidoProyecto");
    return;
  }

  fetch(`/postulaciones/existe?proyectoId=${proyectoId}`)
    .then((res) => res.json())
    .then((data) => {
      if (data.yaPostulado) {
        // 1. Cierra cualquier modal activo
        const modalAbierto = document.querySelector(".modal.show");
        if (modalAbierto) {
          const instanciaModal = bootstrap.Modal.getInstance(modalAbierto);
          if (instanciaModal) instanciaModal.hide();
        }

        if (document.activeElement) {
          document.activeElement.blur();
        }

        // Limpia y reemplaza el contenido con el template
        const contenidoNuevo = template.content.cloneNode(true);
        contenedor.replaceChildren(contenidoNuevo);
      }
    })
    .catch((err) => console.error("Error al verificar postulación:", err));
}

// Ejecutar al cargar la página
document.addEventListener("DOMContentLoaded", () => {
  verificarPostulacion();
});
function asignarEnvioFormulario() {
  const form = document.getElementById("formPostulacion");
  if (!form) return;

  form.addEventListener("submit", function (e) {
    e.preventDefault();

    const monto = document.getElementById("montoContraofertaInput").value;
    const proyectoId = document
      .getElementById("contenidoProyecto")
      .getAttribute("data-proyecto-id");

    const formData = new FormData();
    if (monto) formData.append("montoContraoferta", monto);

    fetch(`/postulaciones/${proyectoId}`, {
      method: "POST",
      body: formData,
    })
      .then((res) => {
        if (res.redirected && res.url.includes("yaPostulado")) {
          const modal = new bootstrap.Modal(
            document.getElementById("modalYaPostulado")
          );
          modal.show();
        } else {
          const modal = new bootstrap.Modal(
            document.getElementById("modalPostulacionExitosa")
          );
          modal.show();
        }
      })
      .catch((err) => console.error("Error al enviar la postulación:", err));
  });

  // boton seguir explorando para que aparezca el estado de ya postulado
  const btnExplorar = document.getElementById("btnIrAFree");
  if (btnExplorar) {
    btnExplorar.addEventListener("click", () => {
      const modal = bootstrap.Modal.getInstance(
        document.getElementById("modalPostulacionExitosa")
      );
      if (modal) modal.hide();

      const contenedor = document.getElementById("contenidoProyecto");
      const template = document.getElementById("templatePostulado");

      if (contenedor && template) {
        const nuevoContenido = template.content.cloneNode(true);
        contenedor.replaceChildren(nuevoContenido);
      }
    });
  }
}
window.addEventListener("DOMContentLoaded", function () {
  const toastMsg = document.getElementById("toastExito");
  if (toastMsg && toastMsg.textContent.trim() !== "") {
    const toast = new bootstrap.Toast(toastMsg);
    toast.show();
  }
});


