package com.tema.wakkey.despiertakkey


data class DespiertaConfig(val progresoObjetivo: Int, val sensibilidad: Float)

class DespiertaKkeyGenerador {
    fun obtenerConfiguracion(dificultad: String): DespiertaConfig {
        return when (dificultad.uppercase()) {
            "F" -> DespiertaConfig(progresoObjetivo = 100, sensibilidad = 15.0f)
            "M" -> DespiertaConfig(progresoObjetivo = 200, sensibilidad = 17.0f)
            "D" -> DespiertaConfig(progresoObjetivo = 300, sensibilidad = 19.0f)
            else -> DespiertaConfig(progresoObjetivo = 100, sensibilidad = 15.0f)
        }
    }

}
