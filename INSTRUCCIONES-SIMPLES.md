# 🚀 SAGA - Instrucciones Súper Simples

## 📋 Para Iniciar el Sistema

### 1️⃣ Primera vez (instalar dependencias)
```powershell
.\instalar-dependencias.ps1
```

### 2️⃣ Siempre (ejecutar sistema)
```powershell
.\iniciar-saga.ps1
```

## 🎯 ¿Qué hace cada script?

### `instalar-dependencias.ps1`
- Verifica que Node.js esté instalado
- Instala dependencias de WhatsApp
- **Solo se ejecuta una vez**

### `iniciar-saga.ps1`
- Compila proyecto Java
- Inicia bot de WhatsApp (nueva ventana)
- Inicia servidor web (nueva ventana)
- Inicia menú Java (consola actual)
- **Se ejecuta cada vez que quieras usar el sistema**

## 🎮 Cómo usar después de iniciar

### 🖥️ **Menú Java (Consola actual)**
- Usuario: `ARBIANTIOQUIA`
- Contraseña: `ADMIN`
- Funciones: Gestión completa de árbitros y partidos

### 🤖 **Bot WhatsApp (Nueva ventana)**
- Escanea el QR con WhatsApp
- Envía mensaje "SAGA" para probar
- Los árbitros se registran automáticamente

### 🌐 **Servidor Web**
- Corre en puerto 8080
- Para integraciones avanzadas

## 🔧 Si algo sale mal

### ❌ Error Node.js
- Instala desde: https://nodejs.org
- Ejecuta: `.\instalar-dependencias.ps1`

### ❌ Error Java
- Verifica Java 17+ instalado
- Ejecuta: `mvn --version`

### ❌ Error Puerto
- Cierra otras apps en puerto 8080
- Reinicia el sistema

---

## 🎉 **¡Solo 2 comandos y listo!**

1. `.\instalar-dependencias.ps1` (primera vez)
2. `.\iniciar-saga.ps1` (siempre)

**¡Súper simple para todos!** 🚀
