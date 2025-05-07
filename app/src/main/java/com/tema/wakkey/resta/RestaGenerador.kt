package com.tema.wakkey.resta


data class RestaGenerador(val enunciado: String, val resultado: Int)

class GeneradorResta {
    fun generarPreguntas(dificultad: String): List<RestaGenerador> {
        return when (dificultad.uppercase()) {
            "F" -> List(3) {
                val a = (5..9).random()
                val b = (1..4).random()
                RestaGenerador("$a - $b", a - b)
            }
            "M" -> List(3) {
                val a = (50..99).random()
                val b = (10..49).random()
                RestaGenerador("$a - $b", a - b)
            }
            "D" -> List(3) {
                val a = (100..199).random()
                val b = (50..99).random()
                val c = (10..49).random()
                RestaGenerador("$a - $b - $c", a - b - c)
            }
            else -> emptyList()
        }
    }
}
