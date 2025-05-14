package com.tema.wakkey.despeinakkey


data class DespeinaConfig(val progresoObjetivo: Int, val sensibilidad: Float)

class DespeinaGenerador {
    fun obtenerConfiguracion(dificultad: String): DespeinaConfig {
        return when (dificultad.uppercase()) {
            "F" -> DespeinaConfig(progresoObjetivo = 100, sensibilidad = 1.5f)
            "M" -> DespeinaConfig(progresoObjetivo = 200, sensibilidad = 1.0f)
            "D" -> DespeinaConfig(progresoObjetivo = 300, sensibilidad = 0.5f)
            else -> DespeinaConfig(progresoObjetivo = 100, sensibilidad = 1.5f)
        }
    }
}
