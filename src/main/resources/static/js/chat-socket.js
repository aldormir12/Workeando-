let chatStompClient = null;
let chatUsuarioIdSuscripcion = null;
let chatOnMessageCb = null;

// Inicializa la conexión y suscripción
function initChatSocket(opts) {
  // opts: { usuarioId: number, onMessage: function(dto){} }
  if (!opts || typeof opts.usuarioId !== "number") {
    console.error("usuarioId requerido para initChatSocket");
    return;
  }
  chatUsuarioIdSuscripcion = opts.usuarioId;
  chatOnMessageCb = typeof opts.onMessage === "function" ? opts.onMessage : null;

  const socket = new SockJS("/ws"); // Debe existir el endpoint /ws en WebSocketConfig
  chatStompClient = Stomp.over(socket);

  // Opcional: silenciar logs STOMP
  chatStompClient.debug = null;

  chatStompClient.connect({}, function () {
    const destino = "/queue/chat/" + chatUsuarioIdSuscripcion;
    chatStompClient.subscribe(destino, function (frame) {
      try {
        const dto = JSON.parse(frame.body);
        if (chatOnMessageCb) {
          chatOnMessageCb(dto);
        } else {
          console.log("Mensaje entrante:", dto);
        }
      } catch (e) {
        console.error("Error parseando mensaje:", e);
      }
    });
  }, function (error) {
    console.error("Error de conexión STOMP:", error);
  });
}

// Envía un mensaje al servidor
function sendChatMessage(payload) {
  // payload: { postulacionId, remitenteId, remitenteNombre, destinatarioId, contenido }
  if (!chatStompClient || !chatStompClient.connected) {
    console.error("STOMP no conectado");
    return;
  }
  if (!payload || typeof payload !== "object") {
    console.error("Payload requerido");
    return;
  }
  if (typeof payload.contenido !== "string" || payload.contenido.trim() === "") {
    console.error("Contenido requerido");
    return;
  }
  const dto = {
    postulacionId: typeof payload.postulacionId === "number" ? payload.postulacionId : null,
    remitenteId: payload.remitenteId,
    remitenteNombre: payload.remitenteNombre || "",
    destinatarioId: payload.destinatarioId,
    contenido: payload.contenido.trim(),
    timestamp: Date.now()
  };
  chatStompClient.send("/app/chat.send", {}, JSON.stringify(dto));
}

// Cierra la conexión
function disconnectChatSocket() {
  if (chatStompClient) {
    try { chatStompClient.disconnect(() => {}); } catch (e) {}
    chatStompClient = null;
  }
}

// Exponer funciones en window
window.initChatSocket = initChatSocket;
window.sendChatMessage = sendChatMessage;
window.disconnectChatSocket = disconnectChatSocket;