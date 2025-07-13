# 🔍 INFORMACIÓN DISPONIBLE EN TELEGRAM BOT API

## 📱 **QUE INFORMACIÓN PUEDE VER UN BOT DE TELEGRAM**

### ✅ **INFORMACIÓN SIEMPRE DISPONIBLE:**
```java
// Información básica del usuario
Long chatId = update.getMessage().getChatId();              // ✅ Chat ID único
String firstName = update.getMessage().getFrom().getFirstName(); // ✅ Nombre
String lastName = update.getMessage().getFrom().getLastName();   // ✅ Apellido (si está configurado)
String username = update.getMessage().getFrom().getUserName();   // ✅ @username (si es público)
Integer userId = update.getMessage().getFrom().getId();          // ✅ User ID de Telegram

// Información del mensaje
String messageText = update.getMessage().getText();              // ✅ Texto del mensaje
Date messageDate = update.getMessage().getDate();               // ✅ Fecha del mensaje
```

### ❌ **INFORMACIÓN NO DISPONIBLE AUTOMÁTICAMENTE:**
```java
// ❌ Número de teléfono - Solo si el usuario lo comparte explícitamente
// ❌ Contactos del usuario
// ❌ Ubicación - Solo si el usuario la comparte
// ❌ Conversaciones con otros bots/usuarios
// ❌ Información del perfil privado
```

### 📱 **CÓMO OBTENER NÚMERO DE TELÉFONO:**

#### **Método 1: Usuario comparte contacto**
```java
if (update.getMessage().hasContact()) {
    Contact contact = update.getMessage().getContact();
    String phoneNumber = contact.getPhoneNumber();    // ✅ Número disponible
    String firstName = contact.getFirstName();        // ✅ Nombre del contacto
    String lastName = contact.getLastName();          // ✅ Apellido del contacto
    Integer userId = contact.getUserId();             // ✅ User ID (si es el mismo usuario)
}
```

#### **Método 2: Solicitar con botón personalizado**
```java
// Crear botón para solicitar contacto
KeyboardButton contactButton = new KeyboardButton("📱 Compartir mi contacto");
contactButton.setRequestContact(true);  // ✅ Esto hace que Telegram solicite permiso

ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
keyboard.setKeyboard(Arrays.asList(Arrays.asList(contactButton)));
keyboard.setResizeKeyboard(true);
keyboard.setOneTimeKeyboard(true);

SendMessage message = new SendMessage();
message.setReplyMarkup(keyboard);
```

### 🔒 **LIMITACIONES DE PRIVACIDAD (POR DISEÑO):**

1. **Telegram protege la privacidad** del usuario
2. **El bot NO puede acceder** a información no autorizada
3. **El usuario debe dar permiso explícito** para compartir datos sensibles
4. **Esto es una característica de seguridad**, no un bug

### 🧪 **EJEMPLO COMPLETO DE CAPTURA:**

```java
@Override
public void onUpdateReceived(Update update) {
    if (update.hasMessage()) {
        Message message = update.getMessage();
        
        // Información siempre disponible
        Long chatId = message.getChatId();
        String firstName = message.getFrom().getFirstName();
        String username = message.getFrom().getUserName();
        
        System.out.println("👤 Usuario: " + firstName);
        System.out.println("🆔 Chat ID: " + chatId);
        System.out.println("📝 Username: " + (username != null ? "@" + username : "No público"));
        
        // Solo si hay texto
        if (message.hasText()) {
            String text = message.getText();
            System.out.println("💬 Mensaje: " + text);
        }
        
        // Solo si comparte contacto
        if (message.hasContact()) {
            Contact contact = message.getContact();
            String phone = contact.getPhoneNumber();
            System.out.println("📱 Teléfono compartido: " + phone);
            
            // ✅ AQUÍ es cuando SÍ tenemos el número
            processPhoneNumber(chatId, phone);
        }
    }
}
```

### 🎯 **ESTRATEGIA PARA NUESTRO BOT SAGA:**

1. **Capturar Chat ID** - Siempre disponible ✅
2. **Solicitar contacto** - Con botón amigable ✅  
3. **Alternativamente** - Pedir cédula manualmente ✅
4. **Vincular Chat ID ↔ Árbitro** - Una vez verificado ✅
5. **Sesiones persistentes** - Para no pedir datos nuevamente ✅

### 🚨 **IMPORTANTE:**

**El bot NO puede "espiar" números de teléfono**. Esta es una limitación intencional de Telegram para proteger la privacidad de los usuarios. Es la razón por la que implementamos:

- ✅ Solicitud explícita de contacto
- ✅ Verificación alternativa por cédula  
- ✅ Explicación clara al usuario
- ✅ Botones que solicitan permisos

**Esto hace que nuestro sistema sea seguro y compatible con las políticas de privacidad de Telegram.**
