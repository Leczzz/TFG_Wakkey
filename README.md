# 🛌⏰ Wakkey - La llave del despertar

![Wakkey Logo](img.png)

---

## 🚨 IMPORTANTE

### ✅ **El código limpio, bien estructurado y totalmente comentado está en la rama [`main`](https://github.com/Leczzz/TFG_Wakkey)**

> Para revisar la lógica completa de la app, ejemplos de implementación de juegos, y estructura general de carpetas y clases, por favor consulta la rama `main`.

---

## 📱 ¿Qué es Wakkey?

**Wakkey** es una aplicación de alarmas para Android que solo se puede desactivar completando un juego. Está diseñada para asegurarse de que el usuario esté verdaderamente despierto antes de apagar la alarma.

---

## 🎮 Juegos disponibles

- **¡Suma!**: Resuelve sumas según la dificultad elegida.
- **¡Resta!**: Resuelve restas según la dificultad elegida.
- **Despeina a Kkey**: Sopla en el micrófono hasta llenar una barra.
- **Despierta a Kkey**: Agita el dispositivo hasta despertar a Kkey.
- **ScanKkey**: Escanea un código de barras para completar el reto.

---

## 🛠 Tecnologías

- Kotlin  
- Android SDK  
- RoomDB  
- Sensores (micrófono, acelerómetro, cámara)

---

## 👩‍💻 Desarrolladora

- Maria Alexandra Tirca  
- [https://github.com/Leczzz](https://github.com/Leczzz)

---


## 🌿 Estructura de ramas de Wakkey (Git)

gitG## 🌿 Estructura de ramas de Wakkey (Git)

```mermaid
gitGraph
   commit id: "Inicio del proyecto"
   branch main
   checkout main
   commit id: "Código base en main"

   branch juegos
   checkout juegos
   commit id: "Interfaz de crear alarma y item_juegos"

   branch Suma_Alarma
   checkout Suma_Alarma
   commit id: "Juego ¡Suma! implementado"
   checkout juegos
   merge Suma_Alarma

   branch logica_juegos
   checkout logica_juegos
   commit id: "Recuperación de juegos y mejoras"
   merge juegos

   branch juego_resta
   checkout juego_resta
   commit id: "Implementación del juego Resta"

   branch alarmas
   checkout alarmas
   commit id: "Lógica completa de alarmas"

   branch cronometro
   checkout cronometro
   commit id: "Mejoras y lógica del cronómetro"

   branch cuenta_atras
   checkout cuenta_atras
   commit id: "Cuenta atrás completada"

   branch despeinakkey
   checkout despeinakkey
   commit id: "Juego Despeina a Kkey"

   branch despiertakkey
   checkout despiertakkey
   commit id: "Juego Despierta a Kkey"

   branch scankkey
   checkout scankkey
   commit id: "Juego ScanKkey terminado"

   checkout main
   merge logica_juegos
   merge juego_resta
   merge alarmas
   merge cronometro
   merge cuenta_atras
   merge despeinakkey
   merge despiertakkey
   merge scankkey
```
