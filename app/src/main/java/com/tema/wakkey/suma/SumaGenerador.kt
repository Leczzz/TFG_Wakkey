package com.tema.wakkey.suma


data class SumaGenerador(val enunciado: String, val resultado: Int) // Clase para representar una pregunta de suma

    class GeneradorSuma {
        fun generarPreguntas(dificultad: String): List<SumaGenerador> {
            return when (dificultad.uppercase()) {
                "F" -> List(3) {
                    val a = (1..9).random() // Genera un número aleatorio entre 1 y 9
                    val b = (1..9).random() // Genera un número aleatorio entre 1 y 9
                    SumaGenerador("$a + $b", a + b) // Crea una pregunta de suma
                }
                "M" -> List(3) {
                    val a = (10..99).random() // Genera un número aleatorio entre 10 y 99
                    val b = (10..99).random() // Genera un número aleatorio entre 10 y 99
                    SumaGenerador("$a + $b", a + b) // Crea una pregunta de suma
                }
                "D" -> List(3) {
                    val a = (10..99).random() // Genera un número aleatorio entre 10 y 99
                    val b = (10..99).random() // Genera un número aleatorio entre 10 y 99
                    val c = (10..99).random() // Genera un número aleatorio entre 10 y 99
                    SumaGenerador("$a + $b + $c", a + b + c) // Crea una pregunta de suma
                }
                else -> emptyList()
            }
        }

    }

