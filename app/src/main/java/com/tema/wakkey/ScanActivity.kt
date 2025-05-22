package com.tema.wakkey

import android.Manifest
import android.app.KeyguardManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.*
import com.journeyapps.barcodescanner.camera.CameraSettings

// Esta clase se encarga de manejar la lógica y la interacción de la actividad de Scan.
class ScanActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView // Vista de códigos de barras
    private var isFlashOn = false // Estado del flash
    private val CAMERA_PERMISSION_CODE = 1001 // Código de permiso de cámara

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout (cambia por el nombre real de tu layout)
        setContentView(R.layout.scankkey_main)

        // Compatibilidad adicional para versiones antiguas
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // Requiere API 27+
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        // Solicitar desbloqueo del Keyguard (bloqueo de pantalla)
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        keyguardManager?.requestDismissKeyguard(this, null)

        // Inicializar barcodeView
        barcodeView = findViewById(R.id.barcodeScannerView)

        // Configurar botón de flash
        val flashButton = findViewById<Button>(R.id.flashButton)
        flashButton.setOnClickListener {
            isFlashOn = !isFlashOn
            barcodeView.barcodeView.setTorch(isFlashOn)
            val estado = if (isFlashOn) "Encendido" else "Apagado"
            Toast.makeText(this, "Flash $estado", Toast.LENGTH_SHORT).show()
        }

        // Obtener sonido del intent
        val sonidoNombre = intent.getStringExtra("sonido")
        val sonidoResId = when (sonidoNombre) {
            "Crystal Waters" -> R.raw.crystalwaters
            "Hawaii" -> R.raw.hawai
            "Lofi" -> R.raw.lofi
            "Morning" -> R.raw.morning
            "Piano" -> R.raw.piano
            else -> R.raw.morning
        }

        // Iniciar sonido
        AlarmSoundPlayer.start(this, sonidoResId)

        // Comprobar permiso y arrancar cámara o pedir permiso
        checkCameraPermission()
    }

    private fun checkCameraPermission() { // Comprobar permiso de cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            startBarcodeScanner()
        }
    }

    private fun startBarcodeScanner() {
        barcodeView.cameraSettings = CameraSettings().apply {
            requestedCameraId = 0 // Cámara trasera
        }

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.text?.let { code ->
                    Toast.makeText(this@ScanActivity, "Código escaneado: $code", Toast.LENGTH_LONG).show()
                    finalizarJuego() // Finalizar el juego
                    barcodeView.pause() // Pausar la vista de códigos de barras
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {} // No se usa
        })
    }

    private fun finalizarJuego() {
        AlarmSoundPlayer.stop()
        finish()
    }

    override fun onRequestPermissionsResult( // Manejar permisos
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBarcodeScanner()
            } else {
                Toast.makeText(this, "Se requiere permiso de cámara para escanear", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::barcodeView.isInitialized) {
            barcodeView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::barcodeView.isInitialized) {
            barcodeView.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmSoundPlayer.stop()
    }
}
