package com.saga.whatsapp.test;

import java.util.Scanner;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.saga.whatsapp.config.WhatsAppConfig;
import com.saga.whatsapp.service.WhatsAppService;

/**
 * Clase para probar el bot de WhatsApp de forma interactiva
 * Solo se ejecuta si se activa el perfil 'test'
 */
@Component
public class WhatsAppBotTester implements CommandLineRunner {
    
    private static final Logger logger = Logger.getLogger(WhatsAppBotTester.class.getName());
    
    @Autowired
    private WhatsAppService whatsAppService;
    
    @Autowired
    private WhatsAppConfig config;
    
    @Override
    public void run(String... args) {
        // Solo ejecutar en modo test
        if (args.length > 0 && "test".equals(args[0])) {
            runInteractiveTest();
        }
    }
    
    private void runInteractiveTest() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n🧪 ===============================");
        System.out.println("🤖 PROBADOR BOT WHATSAPP SAGA");
        System.out.println("🧪 ===============================\n");
        
        System.out.println("📱 Phone Number ID: " + config.getPhoneNumberId());
        System.out.println("🆔 App ID: " + config.getAppId());
        System.out.println("📡 API Version: " + config.getApiVersion());
        System.out.println();
        
        while (true) {
            System.out.println("Opciones disponibles:");
            System.out.println("1. Enviar mensaje de prueba");
            System.out.println("2. Enviar mensaje de bienvenida");
            System.out.println("3. Mostrar configuración");
            System.out.println("4. Salir");
            System.out.print("\nSelecciona una opción (1-4): ");
            
            String opcion = scanner.nextLine().trim();
            
            switch (opcion) {
                case "1":
                    enviarMensajePrueba(scanner);
                    break;
                case "2":
                    enviarMensajeBienvenida(scanner);
                    break;
                case "3":
                    mostrarConfiguracion();
                    break;
                case "4":
                    System.out.println("👋 ¡Hasta luego!");
                    return;
                default:
                    System.out.println("❌ Opción no válida");
            }
            
            System.out.println("\n" + "=".repeat(50) + "\n");
        }
    }
    
    private void enviarMensajePrueba(Scanner scanner) {
        System.out.print("📱 Ingresa el número de WhatsApp (ej: +573001234567): ");
        String numero = scanner.nextLine().trim();
        
        System.out.print("💬 Ingresa el mensaje a enviar: ");
        String mensaje = scanner.nextLine().trim();
        
        if (numero.isEmpty() || mensaje.isEmpty()) {
            System.out.println("❌ Número y mensaje son requeridos");
            return;
        }
        
        System.out.println("📤 Enviando mensaje...");
        
        boolean enviado = whatsAppService.sendTextMessage(numero, mensaje);
        
        if (enviado) {
            System.out.println("✅ Mensaje enviado exitosamente!");
        } else {
            System.out.println("❌ Error al enviar mensaje");
        }
    }
    
    private void enviarMensajeBienvenida(Scanner scanner) {
        System.out.print("📱 Ingresa el número de WhatsApp: ");
        String numero = scanner.nextLine().trim();
        
        System.out.print("👤 Ingresa el nombre (opcional): ");
        String nombre = scanner.nextLine().trim();
        
        if (numero.isEmpty()) {
            System.out.println("❌ Número es requerido");
            return;
        }
        
        System.out.println("📤 Enviando mensaje de bienvenida...");
        
        boolean enviado = whatsAppService.sendWelcomeMessage(numero, nombre.isEmpty() ? null : nombre);
        
        if (enviado) {
            System.out.println("✅ Mensaje de bienvenida enviado!");
        } else {
            System.out.println("❌ Error al enviar mensaje de bienvenida");
        }
    }
    
    private void mostrarConfiguracion() {
        System.out.println("⚙️ CONFIGURACIÓN ACTUAL:");
        System.out.println("• Phone Number ID: " + config.getPhoneNumberId());
        System.out.println("• App ID: " + config.getAppId());
        System.out.println("• Business Account ID: " + config.getBusinessAccountId());
        System.out.println("• API Version: " + config.getApiVersion());
        System.out.println("• Verify Token: " + config.getVerifyToken());
        System.out.println("• Messages Endpoint: " + config.getMessagesEndpoint());
        System.out.println("• Access Token: " + maskToken(config.getAccessToken()));
    }
    
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "..." + token.substring(token.length() - 4);
    }
}
