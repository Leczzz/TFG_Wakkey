package com.tema.wakkey

import android.Manifest
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

class ScanActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private var isFlashOn = false
    private val CAMERA_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mantener pantalla encendida y sobre la pantalla de bloqueo
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        setContentView(R.layout.scankkey_main)
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

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
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
                    finalizarJuego()
                    barcodeView.pause()
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })
    }

    private fun finalizarJuego() {
        AlarmSoundPlayer.stop()
        finish()
    }

    override fun onRequestPermissionsResult(
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
