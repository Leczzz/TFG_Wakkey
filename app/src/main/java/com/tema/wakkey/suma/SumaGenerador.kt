package com.tema.wakkey.suma


data class SumaGenerador(val enunciado: String, val resultado: Int)

    class GeneradorSuma {
        fun generarPreguntas(dificultad: String): List<SumaGenerador> {
            return when (dificultad.lowercase()) {
                "fácil" -> List(3) {
                    val a = (1..9).random()
                    val b = (1..9).random()
                    SumaGenerador("$a + $b", a + b)
                }
                "medio" -> List(3) {
                    val a = (10..99).random()
                    val b = (10..99).random()
                    SumaGenerador("$a + $b", a + b)
                }
                "difícil" -> List(3) {
                    val a = (10..99).random()
                    val b = (10..99).random()
                    val c = (10..99).random()
                    SumaGenerador("$a + $b + $c", a + b + c)
                }
                else -> emptyList()
            }
        }
    }

