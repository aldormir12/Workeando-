// SECCIÓN 1: Lógica de selección de categorías (máximo 3)
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

// SECCIÓN 2: Función para mostrar toasts
function mostrarToast(mensaje, tipo = "warning") {
  const toastContainer = document.getElementById("toastContainer");

  if (!toastContainer) {
    console.error("No se encontró el contenedor de toasts (#toastContainer)");
    return;
  }

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

// SECCIÓN 3: Selección de opción en pantalla intermedia (wizard)
let opcionSeleccionada = null;

function seleccionarOpcion(modo) {
  opcionSeleccionada = modo;

  // Limpiar estilos de todas las tarjetas
  document.querySelectorAll(".opcion-perfil").forEach((card) => {
    card.classList.remove("border-primary", "bg-light");
    card.querySelector(".check-icon")?.classList.add("d-none");
  });

  // Marcar visualmente la seleccionada
  const seleccionada = document.getElementById(
    modo === "cv" ? "opcionCv" : "opcionManual"
  );
  seleccionada.classList.add("border-primary", "bg-light");
  seleccionada.querySelector(".check-icon")?.classList.remove("d-none");

  // Habilitar el botón de continuar
  document.getElementById("btnContinuar").disabled = false;
}

// SECCIÓN 4: Acción al hacer clic en "Continuar"

function continuar() {
  if (!opcionSeleccionada) return;

  const pantalla = document.getElementById("pantallaSeleccion");
  const formulario = document.getElementById("formularioPerfil");
  const seccionCv = document.getElementById("seccionSubirCv");

  // Ocultar todo primero
  pantalla.classList.add("d-none");
  formulario?.classList.remove("show");
  seccionCv?.classList.remove("show");
  formulario?.classList.add("d-none");
  seccionCv?.classList.add("d-none");

  // Mostrar con transición la opción elegida
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

  // Quitar clase 'show' de ambas secciones
  formulario?.classList.remove("show");
  seccionCv?.classList.remove("show");

  // Esperar la transición y luego ocultar y mostrar pantalla principal
  setTimeout(() => {
    if (formulario) formulario.classList.add("d-none");
    if (seccionCv) seccionCv.classList.add("d-none");

    pantalla.classList.remove("d-none");
  }, 800); // coincide con la duración de tu animación
}
document.getElementById('guardarBtn').addEventListener('click', function(event) {
    event.preventDefault();

    const form = document.querySelector('form'); // Ajusta si tienes un ID específico
    const formData = new FormData(form);

    fetch('/free/perfil', {
        method: 'POST',
        body: formData
    }).then(response => {
        if (response.ok) {
            // Transición suave
            const overlay = document.getElementById('fadeOverlay');
            overlay.style.pointerEvents = 'auto';
            overlay.style.opacity = '1';
            setTimeout(() => {
                window.location.href = '/free';
            }, 700); // Igual al tiempo de transición CSS
        } else {
            alert('Error al guardar el perfil.');
        }
    }).catch(error => {
        console.error('Error en la solicitud:', error);
        alert('Error de red.');
    });
});
