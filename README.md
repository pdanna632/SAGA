# SAGA - Sistema Automatizado de Gestión Arbitral

**Desarrollado por:**
- Danna Valeria Pérez Niño  
- Juan Pablo Jiménez Vergara  
- Samuel García Rojas  
- Samuel Gutiérrez Betancur  
- Santiago Barrientos Medina

## 📋 Descripción
Sistema de gestión arbitral simplificado:
- 🖥️ **Menú Java tradicional** - Sistema completo en una aplicación
- 📱 **Arquitectura limpia** - Preparado para futuras integraciones
- 📊 **Gestión de datos** - Excel como base de datos

## ⚡ INSTALACIÓN Y EJECUCIÓN SIMPLE

### 1️⃣ Instalar Dependencias (Solo una vez)
```powershell
.\instalar-dependencias.ps1
```

### 2️⃣ Ejecutar Sistema Completo
```powershell
.\iniciar-saga.ps1
```

**¡Eso es todo!** El sistema iniciará automáticamente:
- 🖥️ Menú Java tradicional (sistema completo)

### 🔧 Archivos de Sistema:
- `iniciar-saga.ps1` - Script principal de inicio
- `instalar-dependencias.ps1` - Instalación de dependencias

### 📊 Tecnologías:
- Backend: Java 17 + Maven
- Datos: Archivos `.xlsx` como base de datos
- Arquitectura: Limpia y extensible

## Funcionalidades Principales

- Verificación de identidad del usuario  
- Visualización de árbitros disponibles  
- Asignación de árbitros a partidos o eventos  
- Generación de informes semanales  
- Modificación extemporánea de asignaciones

## Estado actual del proyecto

✅ **SISTEMA LIMPIO Y FUNCIONAL** - Versión 1.0

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

