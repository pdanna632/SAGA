# 🚀 SAGA - Sistema Automatizado de Gestión Arbitral

**Desarrollado por:**
- Danna Valeria Pérez Niño  
- Juan Pablo Jiménez Vergara  
- Samuel García Rojas  
- Samuel Gutiérrez Betancur  
- Santiago Barrientos Medina

---

## 🎯 **CÓMO EJECUTAR EL PROYECTO - 3 OPCIONES**

### 📋 **Descripción del Sistema**
Sistema integral de gestión arbitral que incluye:
- 🖥️ **Sistema Java tradicional** - Menú de consola completo
- 🤖 **Bot de Telegram** - Consultas de árbitros via Telegram
- 🌐 **Servidor web** - Interfaz moderna (en desarrollo)

### ⚡ **OPCIÓN 1: SISTEMA COMPLETO AUTOMÁTICO**

#### 1️⃣ **Primera vez** (instalar dependencias)
```powershell
.\instalar-dependencias.ps1
```

#### 2️⃣ **Siempre** (ejecutar todo el sistema)
```powershell
.\iniciar-saga.ps1
```

**Resultado:** Se ejecuta automáticamente:
- 🖥️ Menú Java tradicional (consola)
- 🤖 Bot de Telegram (servidor Spring Boot en puerto 8080)
- 🌐 Servidor web React (puerto 5173)
- 🔗 API REST (puerto 8080/api/)

---

### 🖥️ **OPCIÓN 2: SOLO SISTEMA JAVA (Menú Original)**

```bash
# Compilar y ejecutar sistema Java
mvn clean compile
mvn exec:java
```

**Resultado:** Solo el menú de consola Java
- **Usuario:** `ARBIANTIOQUIA`
- **Contraseña:** `ADMIN`

---

### 📱 **OPCIÓN 3: SOLO BOT TELEGRAM**

#### Versión Simple (sin servidor):
```powershell
# Ejecutar solo el bot de Telegram (versión simple)
.\ejecutar-telegram-bot.ps1
```

#### Versión con Servidor (recomendada):
```powershell
# Ejecutar bot + servidor Spring Boot + API REST
.\ejecutar-servidor-telegram.ps1
```

**O manualmente:**
```powershell
mvn clean compile
mvn spring-boot:run "-Dspring-boot.run.main-class=com.saga.TelegramBotApplication"
```

**Resultado:** Bot de Telegram + API REST
- Bot activo en Telegram: @saga_arbitros_bot
- Servidor Spring Boot: http://localhost:8080
- API REST: http://localhost:8080/api/
- Comandos disponibles: /start, /ayuda, /mi_info, /partidos

---

## 🎮 **¿QUÉ OPCIÓN ELEGIR?**

| Opción | Cuándo usarla |
|--------|---------------|
| **Opción 1** | Uso completo del sistema (recomendado) |
| **Opción 2** | Solo necesitas gestión de árbitros/partidos |
| **Opción 3** | Solo necesitas bot de Telegram (simple o con servidor) |

---

## 🎮 Funcionalidades por Opción

### 🖥️ **Sistema Java (Menú de Consola)**
- **Usuario:** `ARBIANTIOQUIA`
- **Contraseña:** `ADMIN`
- **Funciones:** 
  - Gestión completa de árbitros y partidos
  - Asignación de árbitros
  - Generación de informes semanales
  - Modificación de disponibilidades

### 🤖 **Bot Telegram**
- Consultas de información personal de árbitros
- Comandos: `/start`, `/ayuda`, `/mi_info`, `/partidos`
- Búsqueda automática por chat ID de Telegram
- Respuestas inteligentes y personalizadas

### 🌐 **Servidor Web**
- Puerto 5173 (React frontend)
- Puerto 8080 (Spring Boot backend + Bot Telegram)
- Endpoints REST para integraciones
- Interfaz web moderna (en desarrollo)

---

## 🔧 Solución de Problemas

### ❌ **Error al ejecutar - Java**
```bash
# Verifica Java 17+ instalado
java -version
mvn --version

# Si falla, instala Java 17+
# Descarga desde: https://adoptium.net/
```

### ❌ **Error al ejecutar - Bot Telegram**
```powershell
# Verifica que las credenciales de Telegram estén configuradas
# Revisa src/main/resources/application.properties

# Si falla la compilación
mvn clean compile
```

### ❌ **Error Puerto 8080**
- Cierra otras aplicaciones en puerto 8080
- Cambia puerto en `application.properties`
- Reinicia el sistema

### ❌ **Error Bot de Telegram**
- Verifica las credenciales en `application.properties`
- Asegúrate de que el bot esté creado en @BotFather
- Confirma que el token de Telegram sea válido

---

## 📊 Tecnologías y Arquitectura

### **Backend**
- Java 17 + Maven
- Spring Boot (servidor API + Bot Telegram)
- Arquitectura limpia y extensible
- Manejo de Excel con Apache POI

### **Telegram Integration**
- TelegramBots API (Java)
- Integración con Spring Boot
- Comandos interactivos
- Autenticación por chat ID

### **Web**
- React + Vite (puerto 5173)
- Spring Boot API (puerto 8080)
- Interfaz moderna y responsiva
- Comunicación frontend-backend

### **Base de Datos**
- Archivos `.xlsx` como base de datos
- Fácil mantenimiento y backup
- Compatible con Excel

---

## 📁 Estructura del Proyecto

```
SAGA/
├── 📋 README.md                           # Esta guía completa
├── 🚀 iniciar-saga.ps1                   # EJECUTABLE SISTEMA COMPLETO
├── 🤖 ejecutar-telegram-bot.ps1          # EJECUTABLE BOT TELEGRAM SIMPLE
├── 🌐 ejecutar-servidor-telegram.ps1     # EJECUTABLE BOT + SERVIDOR
├── 📦 instalar-dependencias.ps1          # Instalador de dependencias
├── ⚙️ pom.xml                             # Configuración Maven
├── 📂 src/main/java/com/saga/             # Código fuente Java original
├── 📂 src/main/java/com/saga/telegram/    # Código fuente Bot Telegram
├── 📂 src/main/java/com/saga/controller/  # Controladores REST API
├── 📂 src/main/java/com/saga/service/     # Servicios Spring Boot
├── 📂 Pagina web SAGA/                   # Aplicación web React
└── 📂 target/                            # Archivos compilados
```

---

## 🎯 Funcionalidades Principales

### **Gestión de Árbitros**
- ✅ Verificación de identidad del usuario  
- ✅ Visualización de árbitros disponibles  
- ✅ Registro automático via Telegram
- ✅ Actualización de información de contacto

### **Gestión de Partidos**
- ✅ Asignación de árbitros a partidos
- ✅ Verificación de disponibilidad
- ✅ Modificación extemporánea de asignaciones
- ✅ Gestión de conflictos de horario

### **Reportes y Comunicación**
- ✅ Generación de informes semanales  
- ✅ Notificaciones automáticas via Telegram
- ✅ Exportación de datos
- ✅ Historial de asignaciones

---

## 🌟 Estado del Proyecto

### ✅ **COMPLETADO**
- ✅ Sistema de menú Java funcional
- ✅ Bot de Telegram integrado
- ✅ Gestión completa de árbitros y partidos
- ✅ Procesamiento de comandos inteligente
- ✅ Integración con API de Telegram
- ✅ Scripts de automatización
- ✅ Documentación completa

### 🚧 **EN DESARROLLO**
- Dashboard web completo
- Gestión de partidos via Telegram
- Notificaciones automáticas
- Gestión de disponibilidades via bot

---

## 📞 Soporte y Documentación

### 📋 **Documentación Adicional**
- `src/main/resources/application.properties` - Configuración del sistema

### 🆘 **Para problemas o dudas:**
1. Revisa la sección "Solución de Problemas" arriba
2. Verifica que todos los requisitos estén instalados
3. Consulta la documentación específica del componente
4. Contacta al equipo de desarrollo

### 🔗 **Enlaces útiles:**
- [Java 17 Download](https://adoptium.net/)
- [Node.js Download](https://nodejs.org/)
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [Maven Installation](https://maven.apache.org/install.html)

---

**¡Sistema SAGA - Gestión Arbitral Simplificada!** 🏆

- ✅ Lógica principal en consola (Java) - **TERMINADA**
- ✅ Conexión a documentos Excel como base de datos - **TERMINADA**
- ✅ Scripts de automatización para Windows - **TERMINADAS**
- ✅ Arquitectura limpia y simplificada - **TERMINADA**
- ✅ Proyecto sin dependencias innecesarias - **TERMINADA**

### 🎯 Funcionalidades Implementadas:
- Sistema de autenticación (`ARBIANTIOQUIA`/`ADMIN`)
- Gestión completa de árbitros mediante Excel
- Asignación automática de árbitros a partidos
- Generación de informes y estadísticas
- Modificación de disponibilidades
- Interfaz de menú intuitiva

### 🔮 Próximos Pasos:
- Futuras integraciones según necesidades del proyecto
- Posibles expansiones web o móviles
- Mejoras en la interfaz de usuario


## Próximos pasos

- Consolidar todas las funcionalidades en Java mediante consola  
- Crear componentes iniciales en React  
- Implementar conexión entre frontend y lógica de backend  
- Exportar informes en formato PDF/Excel

## Agradecimientos

Este proyecto está pensado como herramienta para Arbiantioquia, con el objetivo de apoyar su labor formativa y organizacional.

## Licencia

MIT License

## Notas para desarrolladores 

Este proyecto sigue una estructura modular en Java. Cada carpeta contiene lógica separada para entidades como usuarios, árbitros y reportes. Las funcionalidades deben manejar archivos Excel como fuente de datos. El objetivo es una futura migración fluida a una interfaz gráfica hecha en React.

## Instrucciones para ejecutar el proyecto

### Requisitos previos
- Java 17 o superior instalado
- Maven instalado

### Pasos para ejecutar

#### ⚠️ **NOTA IMPORTANTE**: Usa las opciones de arriba para ejecutar el proyecto.

**Las siguientes instrucciones son solo para casos específicos:**

1. Clona este repositorio:
   ```bash
   git clone https://github.com/pdanna632/SAGA.git
   ```
2. Ingresa a la carpeta del proyecto:
   ```bash
   cd SAGA
   ```
3. Para **SOLO sistema Java** (sin bot WhatsApp):
   ```bash
   mvn clean compile
   mvn exec:java
   ```
4. Para **SOLO bot Telegram**:
   ```bash
   mvn clean compile
   mvn exec:java "-Dexec.mainClass=com.saga.SimpleTelegramBotApplication"
   ```

**🎯 RECOMENDADO**: Usa `.\iniciar-saga.ps1` para ejecutar el sistema completo automáticamente.

