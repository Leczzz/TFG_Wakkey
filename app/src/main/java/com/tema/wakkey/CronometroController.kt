import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

//Clase para el cronometro. Se encarga de manejar el tiempo y la lógica del cronómetro.
class CronometroController(private val onTick: (String) -> Unit) {

    private var startTime = 0L // Tiempo de inicio
    private var accumulatedTime = 0L // Tiempo acumulado
    var isRunning = false // Variable para saber si está corriendo
    var isPaused = false // Variable para saber si está pausado
    private var tiempoUltimaVuelta: Long = 0L // Tiempo de la última vuelta

    private val handler = Handler(Looper.getMainLooper()) // Manejador de mensajes
    private val updateRunnable = object : Runnable { // Runnable para actualizar el tiempo
        override fun run() { // Metodo que se ejecuta en el hilo principal
            if (isRunning) { // Si está corriendo
                val time = System.currentTimeMillis() - startTime + accumulatedTime //Calcula el tiempo actual
                onTick(formatTime(time)) // Llama a la función onTick con el tiempo formateado
                handler.postDelayed(this, 1000) // Espera 1 segundo antes de actualizar nuevamente
            }
        }
    }

    val vueltas = mutableListOf<Vuelta>() // Lista para almacenar las vueltas

    data class Vuelta(val numero: Int, val tiempoVuelta: String, val tiempoTotal: String) // Clase para representar una vuelta

    fun start() { // Inicia el cronómetro
        if (!isRunning) { // Si no está corriendo
            startTime = System.currentTimeMillis() // Obtiene el tiempo actual
            isRunning = true // Se marca como corriendo
            isPaused = false // Al iniciar, no está pausado
            handler.post(updateRunnable) // Inicia la actualización del tiempo
        }
    }

    fun pause() { // Pausa el cronómetro
        if (isRunning) { //Si esta corriendo
            accumulatedTime += System.currentTimeMillis() - startTime // Actualiza el tiempo acumulado
            isRunning = false // Se marca como no corriendo
            isPaused = true // Al pausar, se marca como pausado
            handler.removeCallbacks(updateRunnable) // Elimina la actualización del tiempo
        }
    }

    //Reanuda el cronómetro
    fun resume() {
        if (isPaused) { // Si está pausado
            startTime = System.currentTimeMillis() // Obtiene el tiempo actual
            isRunning = true // Se marca como corriendo
            isPaused = false // Al reanudar, ya no está pausado
            handler.post(updateRunnable) // Inicia la actualización del tiempo
        }
    }

    // Resetea el cronómetro
    fun reset() {
        isRunning = false // Al resetear, no está corriendo
        isPaused = false // Al resetear, ya no está ni corriendo ni pausado
        startTime = 0 // Restablece el tiempo de inicio
        accumulatedTime = 0 // Restablece el tiempo acumulado
        handler.removeCallbacks(updateRunnable) // Elimina la actualización del tiempo
        onTick("00:00:00") // Llama a la función onTick con el tiempo reseteado
        vueltas.clear() // Limpia la lista de vueltas
    }

// Obtiene el tiempo transcurrido desde el inicio del cronómetro
    fun getElapsedTime(): Long { // Obtiene el tiempo transcurrido
        return if (isRunning) // Si está corriendo, devuelve el tiempo actual
            System.currentTimeMillis() - startTime + accumulatedTime // Calcula el tiempo actual
        else // Si no está corriendo, devuelve el tiempo acumulado
            accumulatedTime // Devuelve el tiempo acumulado
    }

    // Formatea el tiempo a una cadena legible
    private fun formatTime(ms: Long): String {0 // Formatea el tiempo a una cadena legible
        val totalSeconds = ms / 1000 // Calcula los segundos totales
        val hours = totalSeconds / 3600 // Calcula las horas
        val minutes = (totalSeconds % 3600) / 60 // Calcula los minutos
        val seconds = totalSeconds % 60 // Calcula los segundos

        return String.format("%02d:%02d:%02d", hours, minutes, seconds) // Formatea la cadena
    }

    // Obtiene el tiempo transcurrido formateado
    fun getFormattedElapsedTime(): String {
        return formatTime(getElapsedTime())
    }

    // Maneja las vueltas
    fun registrarVuelta(tablaVueltas: TableLayout) {
        val totalMs = getElapsedTime() // Obtiene el tiempo total
        val tiempoVueltaMs = totalMs - tiempoUltimaVuelta // Calcula el tiempo de la vuelta
        tiempoUltimaVuelta = totalMs // Actualiza el tiempo de la última vuelta

        val vuelta = Vuelta( // Crea una nueva vuelta
            numero = vueltas.size + 1, // Número de la vuelta
            tiempoVuelta = formatTime(tiempoVueltaMs), // Formatea el tiempo de la vuelta
            tiempoTotal = getFormattedElapsedTime() // Formatea el tiempo total
        )

        vueltas.add(vuelta) // Agrega la vuelta a la lista de vueltas
        agregarFilaTabla(vuelta, tablaVueltas) // Agrega la fila a la tabla de vueltas
    }

    // Crea una celda en la tabla
    private fun crearCelda(texto: String, context: android.content.Context): TextView { // Crea una celda en la tabla
        return TextView(context).apply { // Crea una instancia de TextView
            this.text = texto // Establece el texto
            textSize = 16f // Establece el tamaño de fuente
            setTextColor(Color.BLACK) // Establece el color del texto
            gravity = Gravity.CENTER // Centra el texto
            setPadding(12, 12, 12, 12) // Establece el padding
        }
    }

    // Agrega una fila a la tabla de vueltas
    private fun agregarFilaTabla(vuelta: Vuelta, tablaVueltas: TableLayout) { // Agrega una fila a la tabla de vueltas
        val fila = TableRow(tablaVueltas.context) // Crea una nueva fila
        fila.addView(crearCelda("Vuelta ${vuelta.numero}", tablaVueltas.context)) // Agrega la celda con el número de vuelta
        fila.addView(crearCelda(vuelta.tiempoVuelta, tablaVueltas.context)) // Agrega la celda con el tiempo de vuelta
        fila.addView(crearCelda(vuelta.tiempoTotal, tablaVueltas.context)) // Agrega la celda con el tiempo total
        tablaVueltas.addView(fila) // Agrega la fila a la tabla
    }


}
