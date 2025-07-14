# 🤖 **SISTEMA DE NOTIFICACIONES PROACTIVAS - SAGA**

## 📊 **RESUMEN DE IMPLEMENTACIÓN**

Se ha implementado un sistema completo de notificaciones proactivas para el bot de Telegram SAGA que permite:

### ✅ **FUNCIONALIDADES COMPLETADAS**

1. **Autenticación Simplificada por Cédula**
   - Eliminado el proceso complejo de solicitar número de teléfono
   - Proceso directo: Hola → Ingresa tu cédula → Autenticado

2. **Sistema de Notificaciones Proactivas**
   - El bot puede escribir a árbitros cuando sean asignados a partidos
   - Notificaciones automáticas con información completa del partido
   - Recordatorios de partidos próximos

### 🚀 **COMPONENTES IMPLEMENTADOS**

#### 1. **TelegramNotificationService** 
**Ubicación:** `src/main/java/com/saga/telegram/service/TelegramNotificationService.java`

**Métodos principales:**
- `notificarAsignacionPartido(cedula, partido)` - Notifica a un árbitro específico
- `notificarAsignacionMultiple(cedulas, partido)` - Notifica a múltiples árbitros
- `enviarRecordatorioPartido(cedula, partido, horas)` - Envía recordatorios
- `verificarArbitrosConTelegram()` - Verifica qué árbitros tienen Telegram vinculado

#### 2. **NotificacionController**
**Ubicación:** `src/main/java/com/saga/controller/NotificacionController.java`

**Endpoints REST:**
- `POST /api/notificaciones/asignar/{cedula}` - Notificar asignación individual
- `POST /api/notificaciones/asignar-multiple` - Notificar asignación múltiple
- `POST /api/notificaciones/recordatorio/{cedula}/{horas}` - Enviar recordatorio
- `GET /api/notificaciones/verificar-telegram` - Verificar árbitros con Telegram

#### 3. **TelegramMessageProcessor** (Simplificado)
**Ubicación:** `src/main/java/com/saga/telegram/service/TelegramMessageProcessor.java`

**Cambios:**
- Eliminado proceso complejo de verificación por teléfono
- Autenticación directa por cédula
- Código más limpio y mantenible

#### 4. **SAGATelegramBot** (Actualizado)
**Ubicación:** `src/main/java/com/saga/telegram/bot/SAGATelegramBot.java`

**Mejoras:**
- Método `sendMessage()` público para notificaciones
- Correcciones en manejo de teclados de Telegram

## 🔄 **FLUJO DE NOTIFICACIÓN PROACTIVA**

### **1. Cuando se asigna un partido:**

```java
// Ejemplo de uso desde el código Java
@Autowired
private TelegramNotificationService notificationService;

// Notificar a un árbitro específico
boolean enviado = notificationService.notificarAsignacionPartido("12345678", partido);

// Notificar a múltiples árbitros
List<String> cedulas = Arrays.asList("12345678", "87654321");
notificationService.notificarAsignacionMultiple(cedulas, partido);
```

### **2. Usando la API REST:**

```bash
# Notificar asignación individual
curl -X POST http://localhost:8080/api/notificaciones/asignar/12345678 \
  -H "Content-Type: application/json" \
  -d '{
    "id": "P001",
    "fecha": "2025-07-15",
    "hora": "15:00",
    "equipoLocal": "Real Madrid",
    "equipoVisitante": "Barcelona",
    "categoria": "Primera División",
    "municipio": "Bogotá",
    "escenario": "Estadio El Campín"
  }'

# Enviar recordatorio 2 horas antes
curl -X POST http://localhost:8080/api/notificaciones/recordatorio/12345678/2 \
  -H "Content-Type: application/json" \
  -d '{ ... mismo JSON del partido ... }'
```

### **3. Mensaje que recibe el árbitro:**

```
⚽ ¡Nueva Asignación de Partido!

Hola Juan, has sido asignado a un nuevo partido:

📅 Fecha: 2025-07-15
⏰ Hora: 15:00
🏟️ Categoría: Primera División
📍 Municipio: Bogotá

🆚 Equipos:
🏠 Local: Real Madrid
🚌 Visitante: Barcelona

📋 Detalles:
🆔 ID Partido: P001
🏟️ Escenario: Estadio El Campín

💡 Recuerda confirmar tu disponibilidad en el sistema SAGA

¿Necesitas más información? Escribe /info para ver tus datos.
```

## 📋 **REQUISITOS TÉCNICOS**

### **Para que funcione el sistema:**

1. **Árbitro debe estar autenticado:** El árbitro debe haber usado el bot al menos una vez y haberse autenticado con su cédula
2. **Chat ID vinculado:** El sistema almacena el Chat ID del árbitro en el campo `chatId` del modelo `Arbitro`
3. **Cédula válida:** La cédula debe existir en el archivo Excel de árbitros

### **Verificar árbitros con Telegram:**

```bash
# Verificar qué árbitros tienen Telegram vinculado
curl http://localhost:8080/api/notificaciones/verificar-telegram
```

Esto mostrará en los logs:
```
INFO: Árbitros totales: 50
INFO: Árbitros con Telegram vinculado: 12
INFO: Árbitros sin Telegram: 38
```

## 🚀 **CÓMO USAR EL SISTEMA**

### **Paso 1: Iniciar el sistema**
```powershell
# Ejecutar el script de inicio
.\iniciar-saga.ps1
```

### **Paso 2: Árbitro se autentica en el bot**
1. Árbitro envía "Hola" al bot
2. Bot solicita cédula
3. Árbitro ingresa su cédula
4. Sistema vincula Chat ID con perfil de árbitro

### **Paso 3: Notificar asignaciones**
Usar la API REST o llamar directamente al servicio desde el código Java.

## 🔧 **PERSONALIZACIÓN DE MENSAJES**

Los mensajes se pueden personalizar editando el método `generarMensajeAsignacion()` en `TelegramNotificationService.java`.

## 🔒 **SEGURIDAD Y PRIVACIDAD**

- Solo árbitros registrados pueden usar el bot
- El sistema no accede a números de teléfono automáticamente
- La vinculación es explícita y requiere autenticación por cédula
- Los Chat IDs son únicos e inmutables por Telegram

## 📞 **SOPORTE**

Para dudas o problemas:
1. Verificar logs del sistema
2. Comprobar que el árbitro esté autenticado
3. Validar que la cédula exista en el sistema
4. Revisar la conexión del bot de Telegram

---

**✅ Sistema implementado y listo para usar en producción**
