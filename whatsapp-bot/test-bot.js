#!/usr/bin/env node

// test-bot.js - Script de prueba para verificar el bot

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

console.log('🧪 Probando configuración del bot...');

try {
    // Verificar Node.js
    console.log('✅ Node.js versión:', process.version);
    
    // Verificar dependencias
    console.log('📦 Verificando dependencias...');
    
    // Verificar package.json
    const packageJsonPath = path.join(__dirname, 'package.json');
    if (fs.existsSync(packageJsonPath)) {
        const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
        console.log('✅ Package.json encontrado:', packageJson.name);
        console.log('✅ Versión:', packageJson.version);
    } else {
        console.log('❌ Package.json no encontrado');
    }
    
    // Verificar node_modules
    const nodeModulesPath = path.join(__dirname, 'node_modules');
    if (fs.existsSync(nodeModulesPath)) {
        console.log('✅ node_modules encontrado');
    } else {
        console.log('❌ node_modules no encontrado - ejecutar: npm install');
    }
    
    // Verificar dependencias principales
    const deps = ['@whiskeysockets/baileys', 'pino', 'qrcode-terminal', '@hapi/boom'];
    for (const dep of deps) {
        try {
            const depPath = path.join(__dirname, 'node_modules', dep);
            if (fs.existsSync(depPath)) {
                console.log(`✅ ${dep} instalado`);
            } else {
                console.log(`❌ ${dep} no instalado`);
            }
        } catch (e) {
            console.log(`❌ ${dep} no instalado`);
        }
    }
    
    // Verificar archivos del bot
    const botFiles = ['bot.js', 'README.md', 'iniciar-bot.bat'];
    botFiles.forEach(file => {
        if (fs.existsSync(path.join(__dirname, file))) {
            console.log(`✅ ${file} encontrado`);
        } else {
            console.log(`❌ ${file} no encontrado`);
        }
    });
    
    console.log('\n🎉 Verificación completada!');
    console.log('📱 Para iniciar el bot, ejecuta: npm start');
    console.log('🚀 Para iniciar desde Java, usa la opción "Iniciar bot" en Extras');
    
} catch (error) {
    console.error('❌ Error durante la verificación:', error.message);
}
