# 🤖 Bot WhatsApp SAGA

Bot de WhatsApp Business integrado con el sistema SAGA para gestión de árbitros.

## 📋 Características

- ✅ Recibe y procesa mensajes de WhatsApp Business
- ✅ Busca árbitros por número de teléfono
- ✅ Responde consultas sobre información personal
- ✅ Comandos intuitivos y respuestas automáticas
- ✅ Integración completa con base de datos Excel
- 🔄 Próximamente: Gestión de partidos y disponibilidades

## 🚀 Instalación y Configuración

### 1. Prerrequisitos

- Java 17 o superior
- Apache Maven
- Acceso a WhatsApp Business API

### 2. Configuración de WhatsApp Business

El bot ya está configurado con las credenciales proporcionadas:

```properties
# Configuración actual en application.properties
whatsapp.access-token=EAFYd8RQeBNI... (configurado)
whatsapp.app-id=24239769258951890
whatsapp.phone-number-id=679166358619626
whatsapp.business-account-id=1760637238152242
```

### 3. Ejecutar el Bot

#### Opción 1: Script PowerShell (Recomendado)
```powershell
.\ejecutar-whatsapp-bot.ps1
```

#### Opción 2: Maven directo
```bash
# Compilar
mvn clean compile

# Ejecutar servidor
mvn spring-boot:run

# O ejecutar modo prueba
mvn exec:java -Dexec.mainClass="com.saga.WhatsAppBotApplication" -Dexec.args="test"
```

## 🌐 Endpoints del Bot

Una vez ejecutado, el bot estará disponible en:

- **Webhook**: `http://localhost:8080/webhook/whatsapp`
- **Health Check**: `http://localhost:8080/webhook/health`
- **Información**: `http://localhost:8080/webhook/info`

## 📱 Configurar Webhook en WhatsApp Business

1. Ve a tu [consola de desarrolladores de Meta](https://developers.facebook.com/)
2. Selecciona tu aplicación
3. Ve a WhatsApp > Configuración
4. En "Webhook", configura:
   - **URL**: `https://tu-dominio.com/webhook/whatsapp`
   - **Token de verificación**: `SAGA_VERIFY_TOKEN_2025`
   - **Campos**: Marca "messages"

## 💬 Comandos Disponibles

### Comandos Básicos
- `hola` - Mensaje de bienvenida
- `ayuda` - Lista de comandos disponibles
- `mi info` - Información personal del árbitro

### Consultas Específicas
- `partidos` - Próximos partidos asignados (en desarrollo)
- `disponibilidad` - Gestión de disponibilidad (en desarrollo)
- `contacto [nombre]` - Buscar contacto de árbitro (en desarrollo)

## 🔧 Estructura del Proyecto

```
src/main/java/com/saga/whatsapp/
├── config/
│   └── WhatsAppConfig.java          # Configuración de WhatsApp API
├── controller/
│   └── WhatsAppWebhookController.java # Controlador del webhook
├── model/
│   └── WhatsAppWebhookPayload.java   # Modelos de datos
├── service/
│   ├── WhatsAppService.java          # Servicio para enviar mensajes
│   └── MessageProcessor.java        # Procesador de mensajes entrantes
└── test/
    └── WhatsAppBotTester.java        # Herramienta de pruebas
```

## 🧪 Modo de Prueba

El bot incluye un modo de prueba interactivo para enviar mensajes:

```powershell
.\ejecutar-whatsapp-bot.ps1
# Seleccionar opción 2
```

Permite:
- Enviar mensajes de prueba
- Enviar mensajes de bienvenida
- Verificar configuración

## 🔍 Logs y Debugging

El bot registra toda la actividad:

```
📥 Mensaje recibido de: +573001234567
📝 Contenido: mi info
✅ Mensaje enviado exitosamente a: +573001234567
```

## 🚨 Troubleshooting

### Error: "No se puede conectar a WhatsApp API"
- Verifica tu token de acceso
- Asegúrate de que el número de teléfono esté verificado
- Revisa que tu app de Facebook esté activa

### Error: "Webhook no recibe mensajes"
- Verifica que la URL del webhook sea accesible públicamente
- Usa ngrok para exponer localhost: `ngrok http 8080`
- Configura la URL en Meta: `https://tu-url-ngrok.com/webhook/whatsapp`

### Error: "Árbitro no encontrado"
- Verifica que el archivo `Arbitros.xlsx` exista
- Asegúrate de que el formato de teléfono sea correcto
- Revisa que el número esté registrado en la base de datos

## 🔒 Seguridad

- El token de acceso está configurado en `application.properties`
- Solo los números registrados en la base de datos pueden acceder a información completa
- El webhook usa token de verificación para validar requests de Meta

## 📈 Próximas Funcionalidades

- 📅 Gestión completa de disponibilidades
- ⚽ Consulta de partidos asignados
- 📞 Directorio de contactos de árbitros
- 📊 Estadísticas de arbitraje
- 🔔 Notificaciones automáticas
- 📱 Interfaz web de administración

## 🤝 Soporte

Para reportar problemas o sugerir mejoras, contacta al equipo de desarrollo de SAGA.

---

*Bot desarrollado para el Sistema de Gestión de Árbitros (SAGA) 🏆*
