package com.tema.wakkey.despiertakkey


data class DespiertaConfig(val progresoObjetivo: Int, val sensibilidad: Float) // Clase para la configuración del juego

class DespiertaKkeyGenerador {
    fun obtenerConfiguracion(dificultad: String): DespiertaConfig {
        return when (dificultad.uppercase()) {
            "F" -> DespiertaConfig(progresoObjetivo = 100, sensibilidad = 15.0f) // Configuracion para dificultad facil
            "M" -> DespiertaConfig(progresoObjetivo = 200, sensibilidad = 17.0f) // Configuracion para dificultad media
            "D" -> DespiertaConfig(progresoObjetivo = 300, sensibilidad = 19.0f) // Configuracion para dificultad dificil
            else -> DespiertaConfig(progresoObjetivo = 100, sensibilidad = 15.0f) // Configuracion por defecto
        }
    }

}
