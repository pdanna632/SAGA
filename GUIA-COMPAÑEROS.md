# 📋 GUÍA PARA COMPAÑEROS DE EQUIPO

## ✅ **SISTEMA LIMPIO Y FUNCIONAL**

✅ Menú Java tradicional: Funciona correctamente  
✅ Scripts PowerShell: Funcionales  
✅ Comandos Maven: Sintaxis correcta  
✅ Sin dependencias innecesarias: Proyecto limpio  

---

## 🎯 **Para usar el sistema SAGA:**

### 🚀 **Solo necesitas 2 comandos:**

1. **Primera vez únicamente:**
   ```powershell
   .\instalar-dependencias.ps1
   ```

2. **Cada vez que quieras usar el sistema:**
   ```powershell
   .\iniciar-saga.ps1
   ```

## 🎮 **Qué esperar después de ejecutar:**

### 📺 **Una sola ventana:**
- **Consola actual** → Menú Java tradicional (sistema completo)

### 🔑 **Credenciales del sistema:**
- Usuario: `ARBIANTIOQUIA`
- Contraseña: `ADMIN`

##  **Archivos importantes:**

- **SÍ puedes modificar:** `src/main/java/com/saga/model/` (lógica principal)
- **SÍ puedes modificar:** `src/main/resources/data/` (archivos Excel)
- **NO tocar:** `start-server.bat` (archivo auxiliar)

## 🛠️ **Si algo no funciona:**

### ❌ **Error: "Maven no encontrado"**
- Verifica que Java 17+ esté instalado
- Ejecuta: `mvn --version`

### ❌ **Error: "Puerto ocupado"**
- Cierra otras aplicaciones
- Reinicia el sistema

## 🎯 **Sistema simplificado:**

### 🖥️ **Menú Java (Todo en uno)**
- Gestión administrativa completa
- Asignación de árbitros
- Generación de informes
- Modificación de disponibilidades
- Toda la funcionalidad del sistema

## 🔄 **Flujo de trabajo:**

1. Ejecuta: `.\iniciar-saga.ps1`
2. Usa el menú Java para todas las funciones
3. Todo se guarda automáticamente en Excel

## 📊 **Ventajas del sistema simplificado:**

- ✅ **Una sola aplicación** → Todo funciona
- ✅ **Datos en Excel** → Excel actualizado siempre
- ✅ **Cero configuración** → Scripts automáticos
- ✅ **Fácil de usar** → Menú tradicional intuitivo
- ✅ **Sin dependencias externas** → Solo Java y Maven
- ✅ **Proyecto limpio** → Listo para futuras expansiones

---

## 🎉 **¡Súper simple!**

**Solo recuerda:** Un script para instalar (primera vez), otro para ejecutar (siempre). ¡Eso es todo! 🚀

**Nota:** El sistema está preparado para futuras integraciones con la API oficial de Meta WhatsApp. �
