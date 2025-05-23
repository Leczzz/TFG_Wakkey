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

```mermaid
gitGraph
   commit id: "Inicio del proyecto"
   branch main
   commit id: "Estructura base y funcionalidades iniciales"

   branch juegos
   checkout juegos
   commit id: "Interfaz de crear alarma y item_juegos"

   branch Suma_Alarma
   checkout Suma_Alarma
   commit id: "Juego ¡Suma! implementado"
   checkout juegos
   merge Suma_Alarma

   branch logica_interfaz_juegos
   checkout logica_interfaz_juegos
   commit id: "Cambios generales y recuperación de código"
   merge juegos

   branch Logica_interfaz_juegos_y_juego_resta
   checkout Logica_interfaz_juegos_y_juego_resta
   commit id: "Juego Resta e integración"

   branch logica_completa_alarmas
   checkout logica_completa_alarmas
   commit id: "Lógica completa para alarmas"
   checkout main
   merge logica_completa_alarmas

   branch mejoras-cronometro
   checkout mejoras-cronometro
   commit id: "Mejoras en el cronómetro"

   branch logica_interfaz_cronometro
   checkout logica_interfaz_cronometro
   commit id: "Lógica + Interfaz de cronómetro"

   branch logica_interfaz_cuentaatras
   checkout logica_interfaz_cuentaatras
   commit id: "Cuenta atrás lista"

   branch logica_interfaz_juego_despeinakkey
   checkout logica_interfaz_juego_despeinakkey
   commit id: "Juego Despeina a Kkey"

   branch logica_interfaz_juego_despiertakkey
   checkout logica_interfaz_juego_despiertakkey
   commit id: "Juego Despierta a Kkey"

   branch logica_interfaz_juego_scankkey
   checkout logica_interfaz_juego_scankkey
   commit id: "Juego ScanKkey finalizado"

   checkout main
   merge juegos
   merge logica_interfaz_juegos
   merge Logica_interfaz_juegos_y_juego_resta
   merge mejoras-cronometro
   merge logica_interfaz_cronometro
   merge logica_interfaz_cuentaatras
   merge logica_interfaz_juego_despeinakkey
   merge logica_interfaz_juego_despiertakkey
   merge logica_interfaz_juego_scankkey
