# SAGA - Sistema Automatizado de Gestión Arbitral
https://github.com/pdanna632/SAGA.git
**Desarrollado por:**
- Danna Valeria Pérez Niño  
- Juan Pablo Jiménez Vergara  
- Samuel García Rojas  
- Samuel Gutiérrez Betancur  
- Santiago Barrientos Medina

## ¿Qué es SAGA?

SAGA (Sistema Automatizado de Gestión Arbitral) es una herramienta pensada para asistir y optimizar las labores administrativas relacionadas con la organización de árbitros. En su primera etapa, está enfocada en automatizar tareas para Arbiantioquia, una escuela de árbitros regional.

## Objetivo

Automatizar la gestión arbitral haciendo uso de archivos de Excel como base de datos principal, ofreciendo funcionalidades clave que agilicen procesos manuales y reduzcan errores humanos.

## Tecnologías

- Backend (lógica y funcionalidades): Java  
- Frontend (interfaz web - futura implementación): React.js  
- Datos: Archivos `.xlsx` como base de datos simulada

## Funcionalidades Principales

- Verificación de identidad del usuario  
- Visualización de árbitros disponibles  
- Asignación de árbitros a partidos o eventos  
- Generación de informes semanales  
- Modificación extemporánea de asignaciones

## Estado actual del proyecto

- En desarrollo de la lógica principal en consola (Java)  
- Conexión inicial a documentos de Excel simulando una base de datos  
- Diseño de arquitectura para el paso futuro a interfaz web con React


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

