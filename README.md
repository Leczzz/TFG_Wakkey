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
    commit id: "Inicio del proyecto Wakkey"
    branch main
    commit id: "Base funcional estable"

    branch juegos
    commit id: "Interfaz para juegos y creación de alarmas"

    branch Suma_Alarma
    commit id: "Lógica e interfaz de juego ¡Suma!"
    checkout juegos
    merge Suma_Alarma

    branch logica_interfaz_juegos
    commit id: "Código recuperado y ajustes generales"
    merge juegos

    branch Logica_interfaz_juegos_y_juego_resta
    commit id: "Juego Resta implementado"

    branch logica_completa_alarmas
    commit id: "Lógica completa de alarmas"

    branch mejoras_cronometro
    commit id: "Mejoras de cronómetro"

    branch logica_interfaz_cronometro
    commit id: "Interfaz y lógica del cronómetro"

    branch logica_interfaz_cuentaatras
    commit id: "Cuenta atrás funcional"

    branch logica_interfaz_juego_despeinakkey
    commit id: "Juego Despeina a Kkey"

    branch logica_interfaz_juego_despiertakkey
    commit id: "Juego Despierta a Kkey"

    branch logica_interfaz_juego_scankkey
    commit id: "Juego ScanKkey"

    checkout main
    merge logica_completa_alarmas
    merge logica_interfaz_juegos
    merge Logica_interfaz_juegos_y_juego_resta
    merge mejoras_cronometro
    merge logica_interfaz_cronometro
    merge logica_interfaz_cuentaatras
    merge logica_interfaz_juego_despeinakkey
    merge logica_interfaz_juego_despiertakkey
    merge logica_interfaz_juego_scankkey
```
