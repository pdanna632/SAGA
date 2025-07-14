# 🤖 GUÍA DE PRUEBAS - BOT TELEGRAM SAGA MEJORADO

## 🎯 **NUEVAS FUNCIONALIDADES IMPLEMENTADAS**

### ✅ **1. AUTENTICACIÓN AUTOMÁTICA POR NÚMERO DE TELÉFONO**
- El bot verifica automáticamente si tu número está registrado
- Si está registrado, te autentica automáticamente
- Si no, solicita verificación por cédula

### ✅ **2. VERIFICACIÓN POR CÉDULA COMO RESPALDO**
- Si el número no está registrado o no es accesible
- El bot solicita tu cédula para verificar identidad
- Una vez verificado, te da acceso completo

### ✅ **3. SISTEMA DE SESIONES**
- Mantiene tu sesión activa entre conversaciones
- Saludo personalizado cuando regreses
- Opción de cerrar sesión con `/logout`

### ✅ **4. MENÚS PERSONALIZADOS**
- Menús específicos según tu estado de autenticación
- Información personalizada con tu nombre
- Comandos contextuales

---

## 🧪 **CÓMO PROBAR LAS MEJORAS**

### **ESCENARIO 1: Usuario con número registrado**
1. Abre Telegram y busca `@saga_arbitros_bot`
2. Escribe: `hola`
3. El bot te pedirá compartir contacto o escribir cédula
4. **Comparte tu contacto** usando el botón de Telegram
5. ✅ Si tu número está registrado: **Autenticación automática**

### **ESCENARIO 2: Usuario con número NO registrado**
1. Escribe: `hola`
2. Comparte un contacto que NO esté en el sistema
3. ❌ El bot dirá que el número no está registrado
4. 📝 Pide tu cédula para verificación
5. Escribe tu cédula registrada en el sistema
6. ✅ **Autenticación por cédula exitosa**

### **ESCENARIO 3: Usuario sin compartir contacto**
1. Escribe: `hola`
2. NO compartas contacto
3. El bot te dará opciones: compartir contacto O escribir cédula
4. Escribe tu cédula directamente
5. ✅ **Autenticación por cédula**

### **ESCENARIO 4: Usuario ya autenticado**
1. Una vez autenticado, escribe: `hola` otra vez
2. ✅ El bot te saluda por tu nombre
3. Te muestra menú personalizado

---

## 📱 **COMANDOS DISPONIBLES DESPUÉS DE AUTENTICARSE**

```
👋 hola          - Saludo inicial/autenticación
📋 /info         - Ver tu información completa
⚽ /partidos      - Tus próximos partidos
📅 /disponibilidad - Gestionar disponibilidad  
📞 /contactos     - Directorio de árbitros
❓ /ayuda         - Ver comandos disponibles
🚪 /logout        - Cerrar sesión
```

---

## 🔧 **DATOS DE PRUEBA**

Para probar, necesitas usar datos que estén en el archivo:
`src/main/resources/data/Arbitros.xlsx`

**Ejemplo de datos típicos:**
- Cédulas: Las que están registradas en el Excel
- Teléfonos: Los números asociados a esas cédulas

---

## ⚠️ **IMPORTANTE PARA EL DESARROLLADOR**

### **Archivo de Configuración:**
- Token del bot está en: `src/main/resources/application.properties`
- ⚠️ **CRÍTICO**: Este token debe moverse a variables de entorno

### **Logs para Debug:**
El bot genera logs detallados en la consola:
- 📨 Mensajes recibidos
- 🔐 Intentos de autenticación
- ✅ Autenticaciones exitosas
- ❌ Errores de verificación

### **Próximas Mejoras Recomendadas:**
1. 🔒 Mover token a variables de entorno
2. 💾 Persistir vinculaciones chat ID ↔ cédula
3. ⚽ Implementar funcionalidades de partidos reales
4. 📅 Sistema de disponibilidad funcional
5. 📞 Directorio de contactos dinámico

---

## 🚀 **INSTRUCCIONES DE EJECUCIÓN**

1. **Iniciar el bot:**
   ```powershell
   .\ejecutar-solo-bot.ps1
   ```

2. **Verificar que está funcionando:**
   - Debes ver logs de Spring Boot iniciándose
   - Mensaje: "Bot Telegram SAGA iniciado exitosamente!"
   - Puerto 8080 activo

3. **Probar en Telegram:**
   - Buscar: `@saga_arbitros_bot`
   - Escribir: `hola`
   - Seguir el flujo de autenticación

---

## ✅ **CHECKLIST DE FUNCIONALIDADES**

- [x] Captura de número de teléfono
- [x] Búsqueda automática en base de datos Excel
- [x] Autenticación por número registrado
- [x] Verificación por cédula como respaldo
- [x] Sistema de sesiones persistentes
- [x] Menús personalizados por usuario
- [x] Comandos contextuales
- [x] Logging detallado
- [x] Manejo de errores robusto
- [x] Interfaz usuario-friendly

**🎉 ¡El bot ahora tiene un sistema de autenticación completo y personalizado!**
