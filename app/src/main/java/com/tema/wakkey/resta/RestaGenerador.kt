package com.tema.wakkey.resta


data class RestaGenerador(val enunciado: String, val resultado: Int) // Clase para representar una pregunta de resta

class GeneradorResta {
    fun generarPreguntas(dificultad: String): List<RestaGenerador> {
        return when (dificultad.uppercase()) {
            "F" -> List(3) {
                val a = (5..9).random() // Genera un número aleatorio entre 5 y 9
                val b = (1..4).random() // Genera un número aleatorio entre 1 y 4
                RestaGenerador("$a - $b", a - b)
            }
            "M" -> List(3) {
                val a = (50..99).random() // Genera un número aleatorio entre 50 y 99
                val b = (10..49).random() // Genera un número aleatorio entre 10 y 49
                RestaGenerador("$a - $b", a - b)
            }
            "D" -> List(3) {
                val a = (100..199).random() // Genera un número aleatorio entre 100 y 199
                val b = (50..99).random() // Genera un número aleatorio entre 50 y 99
                val c = (10..49).random() // Genera un número aleatorio entre 10 y 49
                RestaGenerador("$a - $b - $c", a - b - c)
            }
            else -> emptyList() // Devuelve una lista vacía si la dificultad no es válida
        }
    }
}
