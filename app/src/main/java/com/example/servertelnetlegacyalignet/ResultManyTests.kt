package com.example.servertelnetlegacyalignet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.servertelnetlegacyalignet.MainActivity.Companion.CONNECTED
import com.example.servertelnetlegacyalignet.MainActivity.Companion.PORT
import com.example.servertelnetlegacyalignet.MainActivity.Companion.PORTSCONNECTED
import com.example.servertelnetlegacyalignet.MainActivity.Companion.PORTSNOTCONNECTED
import com.example.servertelnetlegacyalignet.databinding.ActivityResultManyTestsBinding
import com.example.servertelnetlegacyalignet.databinding.ActivityResultOneTestBinding

class ResultManyTests : AppCompatActivity() {
    private lateinit var binding: ActivityResultManyTestsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultManyTestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        println("entro a many Result")
        val host:String? = intent.extras?.getString(MainActivity.HOST) ?: ""
        val portsConected:Array<String>? = intent.getSerializableExtra(PORTSCONNECTED) as? Array<String>
        val portsNotConected:Array<String>? = intent.getSerializableExtra(PORTSNOTCONNECTED) as? Array<String>
        val portNotConnected:String = portsNotConected!!.joinToString(separator = ", ")
        val portConnected:String = portsConected!!.joinToString(separator = ", ")
        cargarTexto(portConnected, portNotConnected, host!!)
        listeners()

//        portsConected?.forEach {
//                elemento ->
//            println(elemento)
//        }
//        portsNotConected?.forEach {
//                elemento ->
//            println(elemento)
//        }
//        println(host)


    }

    private fun cargarTexto(portsConnected: String, portsNotConnected: String, host: String){
        if (binding.tvDescriptionManySuccess.text.toString().isEmpty()){

            binding.tvDescriptionManySuccess.text = "ninguna"
        }
        else{
        binding.tvDescriptionManySuccess.text = "El host $host se conecto correctamente a los puertos: $portsConnected."}
        if (binding.tvDescriptionManyFailed.text.toString().isEmpty()){
        binding.tvDescriptionManyFailed.text = "El host $host no se puedo conectar a ningun puerto"}
        else{
            binding.tvDescriptionManyFailed.text = "El host $host no se puedo conectar a los puertos: $portsNotConnected."
        }
    }

    private fun listeners() {
        binding.btnRecalculate.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }
    }
}
