package com.example.servertelnetlegacyalignet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.servertelnetlegacyalignet.MainActivity.Companion.CONNECTED
import com.example.servertelnetlegacyalignet.MainActivity.Companion.HOST
import com.example.servertelnetlegacyalignet.MainActivity.Companion.PORT
import com.example.servertelnetlegacyalignet.databinding.ActivityMainBinding
import com.example.servertelnetlegacyalignet.databinding.ActivityResultOneTestBinding

class ResultOneTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultOneTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultOneTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fillFields()
        listeners()



//        println(host)
//        println(port)
//        println(isConnected)



    }

    private fun listeners() {
        binding.btnRecalculate.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }
    }

    private fun fillFields()
    {
        val host:String? = intent.extras?.getString(MainActivity.HOST) ?: ""
        val port:String? = intent.extras?.getString(PORT)
        val isConnected:Boolean? = intent.extras?.getBoolean(CONNECTED)

        if (isConnected == true ){
            binding.tvResultOne.text = "Conexion Success"
            binding.tvResultOne.setTextColor(ContextCompat.getColor(this, R.color.peso_normal))
            binding.tvDescriptionOne.text = "El Host $host se conecto exitosamente por el puerto $port"
            binding.imgLed.setImageResource(R.drawable.boton_verde)
        }

        else{
            binding.tvResultOne.text = "Conexion Failed"
            binding.tvResultOne.setTextColor(ContextCompat.getColor(this, R.color.peso_sobrepeso))
            binding.tvDescriptionOne.text = "El Host $host no pudo conectarse por el puerto $port"
            binding.imgLed.setImageResource(R.drawable.boton_rojo)

        }
    }
}