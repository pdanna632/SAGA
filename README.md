# 🚀 SAGA - Sistema Automatizado de Gestión Arbitral

**Desarrollado por:**
- Danna Valeria Pérez Niño  
- Juan Pablo Jiménez Vergara  
- Samuel García Rojas  
- Samuel Gutiérrez Betancur  
- Santiago Barrientos Medina

## 📋 Descripción
Sistema integral de gestión arbitral que combina:
- 🖥️ **Menú Java tradicional** - Sistema completo de administración
- 📱 **Bot de WhatsApp** - Registro automático de árbitros
- 🌐 **Servidor web** - Para futuras integraciones
- 📊 **Gestión de datos** - Excel como base de datos

---

## ⚡ INSTALACIÓN Y USO - SÚPER SIMPLE

### 🎯 **Solo 2 comandos necesarios:**

#### 1️⃣ **Primera vez** (instalar dependencias)
```powershell
.\instalar-dependencias.ps1
```

#### 2️⃣ **Siempre** (ejecutar sistema)
```powershell
.\iniciar-saga.ps1
```

**¡Eso es todo!** El sistema iniciará automáticamente:
- 🖥️ Menú Java tradicional (sistema principal)
- 🤖 Bot de WhatsApp (nueva ventana)
- 🌐 Servidor web (puerto 8080)

---

## 🎮 Cómo Usar Después de Iniciar

### �️ **Menú Java (Consola Principal)**
- **Usuario:** `ARBIANTIOQUIA`
- **Contraseña:** `ADMIN`
- **Funciones:** 
  - Gestión completa de árbitros y partidos
  - Asignación de árbitros
  - Generación de informes semanales
  - Modificación de disponibilidades

### 🤖 **Bot WhatsApp (Nueva Ventana)**
- Escanea el código QR con WhatsApp
- Envía mensaje **"SAGA"** para probar
- Los árbitros se registran automáticamente
- Actualización de números telefónicos
- Conversación natural y amigable

### 🌐 **Servidor Web**
- Corre en puerto 8080
- Para integraciones avanzadas
- Interfaz web moderna (en desarrollo)

---

## 🔧 Solución de Problemas

### ❌ **Error Node.js**
```powershell
# Instala Node.js desde: https://nodejs.org
.\instalar-dependencias.ps1
```

### ❌ **Error Java**
```powershell
# Verifica Java 17+ instalado
mvn --version
```

### ❌ **Error Puerto**
- Cierra otras aplicaciones en puerto 8080
- Reinicia el sistema

### ❌ **Error WhatsApp**
- Verifica conexión a internet
- Escanea nuevamente el código QR
- Asegúrate de que WhatsApp esté actualizado

---

## 📊 Tecnologías y Arquitectura

### **Backend**
- Java 17 + Maven
- Arquitectura limpia y extensible
- Manejo de Excel con Apache POI

### **WhatsApp Integration**
- Baileys (JavaScript/Node.js)
- QR Code authentication
- Normalización automática de números

### **Web**
- React + Vite
- Interfaz moderna y responsiva
- Puerto 8080

### **Base de Datos**
- Archivos `.xlsx` como base de datos
- Fácil mantenimiento y backup
- Compatible con Excel

---

## 📁 Estructura del Proyecto

```
SAGA/
├── 📋 README.md                    # Este archivo (guía completa)
├── 🚀 iniciar-saga.ps1            # EJECUTABLE PRINCIPAL
├── 📦 instalar-dependencias.ps1   # Instalador de dependencias
├── ⚙️ pom.xml                      # Configuración Maven
├── 📂 src/                         # Código fuente Java
├── 📂 Pagina web SAGA/            # Aplicación web React
└── 📂 target/                      # Archivos compilados
```

---

## 🎯 Funcionalidades Principales

### **Gestión de Árbitros**
- ✅ Verificación de identidad del usuario  
- ✅ Visualización de árbitros disponibles  
- ✅ Registro automático via WhatsApp
- ✅ Actualización de información de contacto

### **Gestión de Partidos**
- ✅ Asignación de árbitros a partidos
- ✅ Verificación de disponibilidad
- ✅ Modificación extemporánea de asignaciones
- ✅ Gestión de conflictos de horario

### **Reportes y Comunicación**
- ✅ Generación de informes semanales  
- ✅ Notificaciones automáticas via WhatsApp
- ✅ Exportación de datos
- ✅ Historial de asignaciones

---

## 🌟 Estado del Proyecto

### ✅ **COMPLETADO**
- Sistema de menú Java funcional
- Integración con WhatsApp Bot
- Gestión completa de árbitros y partidos
- Interfaz web básica
- Documentación unificada

### 🚧 **EN DESARROLLO**
- Expansión de funcionalidades web
- Integración con API oficial de Meta WhatsApp
- Dashboard web completo
- Notificaciones automáticas mejoradas

---

## 📞 Soporte

Para problemas o dudas:
1. Revisa la sección "Solución de Problemas"
2. Verifica que todos los requisitos estén instalados
3. Contacta al equipo de desarrollo

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
1. Clona este repositorio:
   ```bash
   git clone https://github.com/pdanna632/SAGA.git
   ```
2. Ingresa a la carpeta del proyecto:
   ```bash
   cd SAGA
   ```
3. Compila el proyecto con Maven:
   ```bash
   mvn clean install
   ```
4. Ejecuta la aplicación principal:
   ```bash
   mvn exec:java
   ```

Esto ejecutará la aplicación de consola. Si tienes algún problema con la ejecución, asegúrate de que las variables de entorno de Java y Maven estén correctamente configuradas.

