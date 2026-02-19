package pe.edu.idat.app_galeria_fotos

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pe.edu.idat.app_galeria_fotos.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private var archivoFoto : File? = null
    private var rutaArchivoActual = ""

    companion object {
        private val FILE_PROVIDER_AUTHORITY = "pe.edu.idat.app_galeria_fotos.fileProvider"
        private val MIME_JPG = "image/jpeg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btntomarfoto.setOnClickListener(this)
        binding.btncompartirfoto.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.btntomarfoto -> tomarFoto()
            R.id.btncompartirfoto -> compartirFoto()
        }
    }

    private fun tomarFoto (){
        val intentCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            itCamara ->
            itCamara.resolveActivity(packageManager).also {
                val file = crearArchivoParaFoto()
                archivoFoto = file
                rutaArchivoActual = file.absolutePath
                val urlFoto = FileProvider.getUriForFile(
                    this, FILE_PROVIDER_AUTHORITY, file)
                itCamara.putExtra(MediaStore.EXTRA_OUTPUT, urlFoto)
                itCamara.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }
        abrirCamara.launch(intentCamara)
    }
    private fun compartirFoto (){

    }

    private fun crearArchivoParaFoto() : File {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${System.currentTimeMillis()}_",
            ".jpg", dir)
    }

    private val abrirCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK){
            var file = archivoFoto ?: return@registerForActivityResult
            val url = FileProvider.getUriForFile(this,
                FILE_PROVIDER_AUTHORITY, file)
            binding.ivfoto.setImageURI(url)
            binding.btncompartirfoto.isEnabled = true
        }
    }

}