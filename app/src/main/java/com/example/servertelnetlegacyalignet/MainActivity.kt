package com.example.servertelnetlegacyalignet

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.servertelnetlegacyalignet.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val puertosToConnect:Array<String> =  arrayOf<String>("50000", "51000", "50500", "52000","53000")
        val adaptador = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, puertosToConnect)
        binding.spinner.adapter = adaptador

//        binding.radioGroup.setOnCheckedChangeListener{buttonView, isChecked ->
//            val msg = "Hello"
//            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
//
//        }
        val radioGroup = binding.radioGroup

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (binding.radioButtonTestAll.isChecked){

                binding.tvPort.visibility = View.INVISIBLE
                binding.spinner.visibility = View.INVISIBLE
                Toast.makeText(this,"Se Probara el host con todos los puertos: (50000, 51000, 50500, 52000,53000)",Toast.LENGTH_SHORT).show()
            }
            if (binding.radioButtonTestOne.isChecked){
                binding.tvPort.visibility = View.VISIBLE
                binding.spinner.visibility = View.VISIBLE


            }
        }




        binding.buttonTest.setOnClickListener{

            if (binding.radioButtonTestOne.isChecked == true){

                val server = binding.etHost.text.toString()
                val puerto:String= binding.spinner.selectedItem.toString()

                testServer(server, puerto)
            }
            if (binding.radioButtonTestAll.isChecked == true){
                val server = binding.etHost.text.toString()
                testWithAllThePorts(server)
//                Toast.makeText(this, "Todavia no esta implementado", Toast.LENGTH_SHORT).show()
            }

        }



    }



    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://xl0p1u8rf1.execute-api.us-east-1.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    fun hideKeyboard(){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow( binding.viewRoot.windowToken, 0)


    }


    private fun testWithAllThePorts(server:String){
        val puertos = listOf<String>("50000", "51000", "50500", "52000","53000")
        val response = mutableListOf<String>()



            CoroutineScope(Dispatchers.IO).launch {
                for (puerto in puertos){
                    var test = TestBody(server,puerto)
                    var objetbodyTest = Body(listOf(test))
                val call: Response<ServerResponse> = getRetrofit().create(ApiService::class.java).validateServer(objetbodyTest)
                val bodyResponse = call.body()
                println(bodyResponse)
                runOnUiThread{
                    if (call.isSuccessful){
                        val testObject = bodyResponse?.tests
                        val successParameter:Boolean? = testObject?.get(0)?.success
                        if (successParameter == true){
                            response.add(puerto)
                            println("Entro a success")
                        }

                    }

                }

            }
                println("Response after for ${response}")

                binding.textView.text = response.toString()
        }




    }


    @SuppressLint("SetTextI18n")
    private fun testServer(server: String, port:String){
        hideKeyboard()
        var test = TestBody(server,port)
        var objetbodyTest = Body(listOf(test))
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<ServerResponse> = getRetrofit().create(ApiService::class.java).validateServer(objetbodyTest)
            val pokemonBody = call.body()
            println(pokemonBody)
            runOnUiThread{
                if (call.isSuccessful){
//                    binding.textView.text = pokemonBody?.tests.toString()
                    val testObject = pokemonBody?.tests
                    val successParameter:Boolean? = testObject?.get(0)?.success
                    if (successParameter == false){
                        binding.imageView.setImageResource(R.drawable.boton_rojo)
                        binding.textView.text= "El host: $server No se conecto al puerto $port"
                    }
                    else{
                        binding.imageView.setImageResource(R.drawable.boton_verde)
                        binding.textView.text= "El host: $server Se conecto al puerto $port"
                    }
//                    binding.tvNombre.text = pokemonBody?.name.toString()
//                    binding.tvPeso.text = pokemonBody?.weight.toString()
//                    val sprite: Sprites? = pokemonBody?.sprites
//                    val urlString = sprite?.front_default
//
//                    Picasso.get().load(urlString).into(binding.ivPokemon)

//                    binding.textViewResultado.text= pokemonBody.toString()
//                    dogImages.clear()
//                    dogImages.addAll(images)
//                    adapter.notifyDataSetChanged()
                }else{
                    Toast.makeText(this@MainActivity, "Ha Ocurrido un error", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }
}