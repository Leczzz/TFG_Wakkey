package com.tema.wakkey.despeinakkey


data class DespeinaConfig(val progresoObjetivo: Int, val sensibilidad: Float) // Clase para la configuración del juego

class DespeinaGenerador {
    fun obtenerConfiguracion(dificultad: String): DespeinaConfig { // Metodo para obtener la configuración del juego
        return when (dificultad.uppercase()) {
            "F" -> DespeinaConfig(progresoObjetivo = 100, sensibilidad = 2.5f) // Configuracion para dificultad facil
            "M" -> DespeinaConfig(progresoObjetivo = 200, sensibilidad = 2.0f) // Configuracion para dificultad media
            "D" -> DespeinaConfig(progresoObjetivo = 300, sensibilidad = 1.5f) // Configuracion para dificultad dificil
            else -> DespeinaConfig(progresoObjetivo = 100, sensibilidad = 2.5f) // Configuracion por defecto
        }
    }
}
