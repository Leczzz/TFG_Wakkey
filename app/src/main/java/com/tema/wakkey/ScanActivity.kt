package com.tema.wakkey
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

class ScanActivity : AppCompatActivity() {

    private var isFlashOn = false  // Controla el estado del flash


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.scankkey_main)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )



        // Inicia el escaneo al crear la actividad
        startScanning()

        // Configura el botón para encender y apagar el flash
        val flashButton: Button = findViewById(R.id.flashButton)
        flashButton.setOnClickListener {
            toggleFlash()
        }
    }

    private fun startScanning() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)  // Puedes especificar tipos específicos de código de barras
        integrator.setCameraId(0)  // Usa la cámara trasera (0 es la cámara trasera, 1 es la delantera)
        integrator.setBeepEnabled(true)  // Habilita el sonido al escanear
        integrator.setBarcodeImageEnabled(true)  // Habilita la toma de una imagen del código escaneado
        integrator.initiateScan()  // Inicia el escaneo
    }

    // Modo para encender o apagar el flash
    private fun toggleFlash() {
        val integrator = IntentIntegrator(this)
        isFlashOn = !isFlashOn  // Cambia el estado del flash

        integrator.setTorchEnabled(isFlashOn)  // Activa o desactiva el flash
        val flashStatus = if (isFlashOn) "Encendido" else "Apagado"
        Toast.makeText(this, "Flash $flashStatus", Toast.LENGTH_SHORT).show()
    }

    // Modo que recibe el resultado del escaneo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Maneja el resultado del escaneo
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents != null) {
                // Si se detecta un código de barras, maneja el resultado
                val barcode = result.contents  // El código de barras escaneado
                Toast.makeText(this, "Código escaneado: $barcode", Toast.LENGTH_LONG).show()

                // Detener el sonido cuando se termina el escaneo
                AlarmSoundPlayer.stop()

                // Aquí puedes implementar la lógica para detener la alarma y volver a la actividad de la alarma
                closeAlarmAndReturn()
            } else {
                // Si el escaneo no fue exitoso
                Toast.makeText(this, "Escaneo fallido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun closeAlarmAndReturn() {
        // Detener la alarma y regresar a la actividad de la alarma
        val intent = Intent(this, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener el sonido cuando la actividad sea destruida
        AlarmSoundPlayer.stop()
    }
}
