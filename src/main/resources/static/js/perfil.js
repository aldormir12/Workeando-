document.addEventListener("DOMContentLoaded", function () {
  const checkboxes = document.querySelectorAll(".categoria-check");
  checkboxes.forEach((cb) => {
    cb.addEventListener("change", () => {
      const checked = Array.from(checkboxes).filter((c) => c.checked);
      if (checked.length > 3) {
        cb.checked = false;
        mostrarToast("⚠️ Solo puedes seleccionar hasta 3 categorías.");
      }
    });
  });
});

// SECCIÓN 2: Mostrar toast de feedback
function mostrarToast(mensaje, tipo = "warning") {
  const toastContainer = document.getElementById("toastContainer");

  if (!toastContainer) return;

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

  setTimeout(() => {
    const toastEl = document.getElementById(toastId);
    if (toastEl) toastEl.remove();
  }, 5000);
}

// SECCIÓN 3: Pantalla intermedia para seleccionar método de perfil
let opcionSeleccionada = null;

function seleccionarOpcion(modo) {
  opcionSeleccionada = modo;

  document.querySelectorAll(".opcion-perfil").forEach((card) => {
    card.classList.remove("border-primary", "bg-light");
    card.querySelector(".check-icon")?.classList.add("d-none");
  });

  const seleccionada = document.getElementById(
    modo === "cv" ? "opcionCv" : "opcionManual"
  );
  seleccionada.classList.add("border-primary", "bg-light");
  seleccionada.querySelector(".check-icon")?.classList.remove("d-none");

  document.getElementById("btnContinuar").disabled = false;
}

// SECCIÓN 4: Continuar a siguiente sección (formulario o CV)
function continuar() {
  if (!opcionSeleccionada) return;

  const pantalla = document.getElementById("pantallaSeleccion");
  const formulario = document.getElementById("formularioPerfil");
  const seccionCv = document.getElementById("seccionSubirCv");

  pantalla.classList.add("d-none");
  formulario?.classList.add("d-none");
  seccionCv?.classList.add("d-none");

  if (opcionSeleccionada === "manual") {
    formulario.classList.remove("d-none");
    setTimeout(() => formulario.classList.add("show"), 10);
  } else if (opcionSeleccionada === "cv") {
    seccionCv.classList.remove("d-none");
    setTimeout(() => seccionCv.classList.add("show"), 10);
  }
}

function volverPantallaInicio() {
  const pantalla = document.getElementById("pantallaSeleccion");
  const formulario = document.getElementById("formularioPerfil");
  const seccionCv = document.getElementById("seccionSubirCv");

  formulario?.classList.remove("show");
  seccionCv?.classList.remove("show");

  setTimeout(() => {
    formulario?.classList.add("d-none");
    seccionCv?.classList.add("d-none");
    pantalla.classList.remove("d-none");
  }, 800);
}

// SECCIÓN 5: Guardar perfil con animación (si usas AJAX)
const guardarBtn = document.getElementById("guardarBtn");
if (guardarBtn) {
  guardarBtn.addEventListener("click", function (event) {
    event.preventDefault();

    const form = document.querySelector("form");
    const formData = new FormData(form);

    fetch("/free/perfil", {
      method: "POST",
      body: formData,
    })
      .then((response) => {
        if (response.ok) {
          const overlay = document.getElementById("fadeOverlay");
          overlay.style.pointerEvents = "auto";
          overlay.style.opacity = "1";
          setTimeout(() => {
            window.location.href = "/free";
          }, 700);
        } else {
          alert("Error al guardar el perfil.");
        }
      })
      .catch((error) => {
        console.error("Error en la solicitud:", error);
        alert("Error de red.");
      });
  });
}

// SECCIÓN 6: Agregar experiencia laboral
function agregarExperiencia() {
  const container = document.getElementById("experienciasContainer");
  const index = container.querySelectorAll(".experiencia-card").length;

  const nueva = document.createElement("div");
  nueva.className =
    "experiencia-card border rounded p-3 mb-3 position-relative";
  nueva.innerHTML = `
    <div class="mb-2">
      <label class="form-label">Empresa *</label>
      <input type="text" name="experienciaLaboral[${index}].empresa" class="form-control" required />
    </div>
    <div class="mb-2">
      <label class="form-label">Puesto *</label>
      <input type="text" name="experienciaLaboral[${index}].puesto" class="form-control" required />
    </div>
    <div class="mb-2 row">
      <div class="col">
        <label class="form-label">Desde (MM/YYYY)</label>
        <input type="text" name="experienciaLaboral[${index}].fechaDesde" class="form-control" placeholder="Ej: 03/2022" required />
      </div>
      <div class="col">
        <label class="form-label">Hasta (MM/YYYY)</label>
        <input type="text" name="experienciaLaboral[${index}].fechaHasta" class="form-control fecha-hasta" placeholder="Ej: 05/2024" required />
      </div>
      <div class="col-12 mt-1">
        <input type="checkbox" class="form-check-input check-actualidad me-2" onchange="toggleActualidad(this)">
        <label class="form-check-label">Actualmente trabajo aquí</label>
      </div>
    </div>
    <div class="mb-2">
      <label class="form-label">Descripción *</label>
      <textarea name="experienciaLaboral[${index}].descripcion" class="form-control" rows="2" required></textarea>
    </div>
    <div class="text-end">
      <button type="button" class="btn btn-sm btn-outline-secondary me-1" onclick="editarExperiencia(this)">✏️ Editar</button>
      <button type="button" class="btn btn-sm btn-outline-danger" onclick="eliminarExperiencia(this)">🗑️ Eliminar</button>
    </div>
  `;
  container.appendChild(nueva);
}

// SECCIÓN 7: Eliminar experiencia
function eliminarExperiencia(btn) {
  const card = btn.closest(".experiencia-card");
  if (card) card.remove();
}

// SECCIÓN 8: Habilitar edición en campos
function editarExperiencia(btn) {
  const card = btn.closest(".experiencia-card");
  if (card) {
    const inputs = card.querySelectorAll("input, textarea");
    inputs.forEach((input) => input.removeAttribute("readonly"));
  }
}

// SECCIÓN 9: Control de checkbox “Actualmente trabajo aquí”
function toggleActualidad(checkbox) {
  const fechaHastaInput = checkbox
    .closest(".row")
    .querySelector(".fecha-hasta");
  if (checkbox.checked) {
    fechaHastaInput.value = "";
    fechaHastaInput.setAttribute("readonly", true);
  } else {
    fechaHastaInput.removeAttribute("readonly");
  }
}

// Sección: Idiomas dinámicos
document.addEventListener("DOMContentLoaded", () => {
  const agregarIdiomaBtn = document.getElementById("agregarIdioma");
  const idiomasContainer = document.getElementById("idiomasContainer");

  if (agregarIdiomaBtn && idiomasContainer) {
    agregarIdiomaBtn.addEventListener("click", () => {
      const index = idiomasContainer.querySelectorAll(".idioma-item").length;

      const nuevoIdiomaHTML = `
        <div class="idioma-item border rounded p-3 mb-2">
          <div class="row g-2 align-items-center">
            <div class="col-md-6">
              <label class="form-label">Idioma</label>
              <select name="idiomas[${index}].nombre" class="form-select" required>
                <option value="">Seleccione un idioma</option>
                <option value="Español">Español</option>
                <option value="Inglés">Inglés</option>
                <option value="Francés">Francés</option>
                <option value="Portugués">Portugués</option>
                <option value="Alemán">Alemán</option>
                <option value="Italiano">Italiano</option>
                <option value="Chino">Chino</option>
                <option value="Japonés">Japonés</option>
                <option value="Ruso">Ruso</option>
              </select>
            </div>
            <div class="col-md-5">
              <label class="form-label">Nivel</label>
              <select name="idiomas[${index}].nivel" class="form-select" required>
                <option value="">Selecciona</option>
             <option value="">Selecciona</option>
             <option value="A1_PRINCIPIANTE">A1 - Principiante</option>
             <option value="A2_BASICO">A2 - Básico</option>
             <option value="B1_INTERMEDIO_BAJO">B1 - Intermedio bajo</option>
             <option value="B2_INTERMEDIO_ALTO">B2 - Intermedio alto</option>
             <option value="C1_AVANZADO">C1 - Avanzado</option>
             <option value="C2_EXPERTO">C2 - Experto</option>
             <option value="BASICO">Básico</option>
             <option value="INTERMEDIO">Intermedio</option>
             <option value="AVANZADO">Avanzado</option>
             <option value="NATIVO">Nativo</option>

              </select>
            </div>
            <div class="col-md-1 d-flex align-items-end">
              <button type="button" class="btn btn-danger btn-sm eliminarIdioma">
                <i class="bi bi-x"></i>
              </button>
            </div>
          </div>
        </div>
      `;

      idiomasContainer.insertAdjacentHTML("beforeend", nuevoIdiomaHTML);
    });

    // Delegación para eliminar idioma
    idiomasContainer.addEventListener("click", (e) => {
      if (e.target.closest(".eliminarIdioma")) {
        e.target.closest(".idioma-item").remove();
      }
    });
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const habilidadesContainer = document.getElementById("habilidadesContainer");
  const agregarBtn = document.getElementById("agregarHabilidad");

  // Verificamos que ambos elementos existan antes de continuar
  if (habilidadesContainer && agregarBtn) {
    const opcionesHabilidad =
      typeof habilidadesDisponibles !== "undefined"
        ? habilidadesDisponibles
        : [];

    agregarBtn.addEventListener("click", () => {
      const index =
        habilidadesContainer.querySelectorAll(".habilidad-item").length;

      const options = opcionesHabilidad
        .map((h) => `<option value="${h}">${h}</option>`)
        .join("");

      const nuevaHabilidadHTML = `
        <div class="habilidad-item border rounded p-3 mb-2">
          <div class="row g-2 align-items-center">
            <div class="col-md-6">
              <label class="form-label">Habilidad</label>
              <select name="habilidadesTecnicas[${index}].nombre" class="form-select" required>
                <option value="">Seleccione</option>
                ${options}
              </select>
            </div>
            <div class="col-md-5">
              <label class="form-label">Nivel</label>
              <select name="habilidadesTecnicas[${index}].nivel" class="form-select" required>
                <option value="">Seleccione</option>
                <option value="Básico">Básico</option>
                <option value="Intermedio">Intermedio</option>
                <option value="Avanzado">Avanzado</option>
                <option value="Experto">Experto</option>
              </select>
            </div>
            <div class="col-md-1 d-flex align-items-end">
              <button type="button" class="btn btn-danger btn-sm eliminarHabilidad">
                <i class="bi bi-x"></i>
              </button>
            </div>
          </div>
        </div>
      `;

      habilidadesContainer.insertAdjacentHTML("beforeend", nuevaHabilidadHTML);
    });

    // Delegación para eliminar habilidad
    habilidadesContainer.addEventListener("click", (e) => {
      if (e.target.closest(".eliminarHabilidad")) {
        const habilidadItem = e.target.closest(".habilidad-item");
        if (habilidadItem) {
          habilidadItem.remove();
        }
      }
    });
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const fechaInputs = document.querySelectorAll('input[placeholder="MM/YYYY"]');

  fechaInputs.forEach((input) => {
    input.addEventListener("input", (e) => {
      let valor = input.value.replace(/[^\d]/g, ""); // Solo números

      if (valor.length >= 2) {
        valor = valor.substring(0, 2) + "/" + valor.substring(2);
      }

      input.value = valor.substring(0, 7); // Limita a MM/YYYY
    });

    input.addEventListener("blur", () => {
      const valor = input.value;
      const regex = /^(0[1-9]|1[0-2])\/\d{4}$/;

      const errorContainer = input.nextElementSibling;
      if (!regex.test(valor)) {
        input.classList.add("is-invalid");
        if (
          !errorContainer ||
          !errorContainer.classList.contains("invalid-feedback")
        ) {
          const feedback = document.createElement("div");
          feedback.className = "invalid-feedback";
          feedback.innerText =
            "Formato inválido. Usa MM/YYYY con mes entre 01 y 12.";
          input.parentNode.appendChild(feedback);
        }
      } else {
        input.classList.remove("is-invalid");
        const feedback = input.parentNode.querySelector(".invalid-feedback");
        if (feedback) feedback.remove();
      }
    });

    input.addEventListener("keypress", (e) => {
      const char = String.fromCharCode(e.which);
      if (!/[0-9]/.test(char)) {
        e.preventDefault(); // Solo permitir números
      }
    });
  });
});

document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("formSubirCv");
  const inputArchivo = document.querySelector('input[type="file"]');
  const overlay = document.getElementById("cvOverlayCarga");

  if (form && inputArchivo && overlay) {
    form.addEventListener("submit", function (e) {
      e.preventDefault();

      if (!inputArchivo.files.length) {
        mostrarToast("⚠️ Por favor selecciona un archivo antes de continuar.");
        return;
      }

      overlay.classList.remove("d-none"); // Muestra el overlay

      const texto = document.getElementById("textoCarga"); 
      if (texto) {
        texto.textContent = "Estamos leyendo tu CV...";
        texto.classList.add("blink");
      }

      setTimeout(() => {
        if (texto) {
          texto.classList.remove("blink");
          texto.textContent = "CV cargado con éxito";
        }

        form.submit();
      }, 4000);
    });
  }
});


document.addEventListener("DOMContentLoaded", () => {
  const fileInput = document.getElementById("cvFile");
  const label = fileInput.closest("label");
  const textP = label.querySelector("p.mt-3");
  const icono = document.getElementById("iconoCv");

  fileInput.addEventListener("change", () => {
    const fileName = fileInput.files.length > 0 ? fileInput.files[0].name : "";

    // Cambiar texto
    textP.textContent = fileName
      ? `Archivo seleccionado: ${fileName}`
      : "Haz clic para seleccionar tu archivo";

    // Cambiar ícono a documento Word si hay archivo
    if (fileName && icono) {
      icono.classList.remove("bi-upload");
      icono.classList.add("bi-file-earmark-word");
    } else if (icono) {
      icono.classList.remove("bi-file-earmark-word");
      icono.classList.add("bi-upload");
    }
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const pantalla = document.getElementById("pantallaSeleccion");
  const formulario = document.getElementById("formularioPerfil");
  const seccionCv = document.getElementById("seccionSubirCv");

  const mostrarFormulario =
    (typeof modoEdicion !== "undefined" && modoEdicion === true) ||
    (typeof cvProcesado !== "undefined" && cvProcesado === true) ||
    (typeof soloLectura !== "undefined" && soloLectura === true);

  if (mostrarFormulario) {
    pantalla?.classList.add("d-none");
    seccionCv?.classList.add("d-none");
    formulario?.classList.remove("d-none");
    formulario?.classList.add("show");

    // Hacer scroll si la URL contiene #formularioPerfil
    if (window.location.hash === "#formularioPerfil") {
      setTimeout(() => {
        formulario.scrollIntoView({ behavior: "smooth" });
      }, 100);
    }
  } else {
    pantalla?.classList.remove("d-none");
    formulario?.classList.add("d-none");
    seccionCv?.classList.add("d-none");
  }
});
document.addEventListener("DOMContentLoaded", () => {
  const opcionCv = document.getElementById("opcionCv");
  const opcionManual = document.getElementById("opcionManual");
  const btnContinuar = document.getElementById("btnContinuar");

  if (opcionCv)
    opcionCv.addEventListener("click", () => seleccionarOpcion("cv"));
  if (opcionManual)
    opcionManual.addEventListener("click", () => seleccionarOpcion("manual"));
  if (btnContinuar) btnContinuar.addEventListener("click", continuar);
});
