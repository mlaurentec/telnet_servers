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
        val puertosToConnect = resources.getStringArray(R.array.puertos)
//        val puertosToConnect:Array<String> =  arrayOf<String>("50000", "51000", "50500", "52000","53000")
        val adapter = ArrayAdapter(this,R.layout.lista_items, puertosToConnect)

        with(binding.comboBoxMaterialDesign){
            setAdapter(adapter)
        }


//        binding.radioGroup.setOnCheckedChangeListener{buttonView, isChecked ->
//            val msg = "Hello"
//            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
//
//        }
        val radioGroup = binding.radioGroup

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (binding.radioButtonTestAll.isChecked){


                binding.textInputLayoutPuerto.visibility = View.INVISIBLE
                binding.imageView.visibility = View.INVISIBLE
                Toast.makeText(this,"Puertos seleccionados:50000,51000,50500,52000,53000)",Toast.LENGTH_SHORT).show()
            }
            if (binding.radioButtonTestOne.isChecked){
                binding.textInputLayoutPuerto.visibility = View.VISIBLE
                binding.imageView.visibility = View.VISIBLE



            }
        }




        binding.buttonTest.setOnClickListener{

            if (binding.radioButtonTestOne.isChecked == true){

                val server = binding.etHost.text.toString()
                val puerto:String = binding.comboBoxMaterialDesign.text.toString()
//                val puerto:String= binding.spinner.selectedItem.toString()

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
        hideKeyboard()
        val puertos = listOf<String>("50000", "51000", "50500", "52000","53000")
        val response = mutableListOf<String>()



        val loading = LoadingDialog(this@MainActivity)
        loading.startLoading()
        CoroutineScope(Dispatchers.IO).launch {


                for (puerto in puertos){

                    val test = TestBody(server,puerto)
                    val objetbodyTest = Body(listOf(test))
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
//                loading.isDismiss()
                println("Response after for ${response}")

                binding.textView.text = response.toString()
                loading.isDismiss()
        }





    }


    @SuppressLint("SetTextI18n")
    private fun testServer(server: String, port:String){
        hideKeyboard()
        val loading = LoadingDialog(this)
        loading.startLoading()
        val test = TestBody(server,port)
        val objetbodyTest = Body(listOf(test))
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

                }else{
                    Toast.makeText(this@MainActivity, "Ha Ocurrido un error", Toast.LENGTH_SHORT).show()
                }

            }
            loading.isDismiss()

        }

    }
}