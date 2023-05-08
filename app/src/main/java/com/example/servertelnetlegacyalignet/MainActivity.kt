package com.example.servertelnetlegacyalignet

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
    companion object{
        const val CONNECTED = "success"
        const val HOST = "host"
        const val PORT = "port"
        const val PORTSCONNECTED = "portConnected"
        const val PORTSNOTCONNECTED = "portNotConnected"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenSplash.setKeepOnScreenCondition{false}
        val puertosToConnect = resources.getStringArray(R.array.puertos)
//        val puertosToConnect:Array<String> =  arrayOf<String>("50000", "51000", "50500", "52000","53000")
        val adapter = ArrayAdapter(this,R.layout.lista_items, puertosToConnect)

        with(binding.comboBoxMaterialDesign){
            setAdapter(adapter)
        }


        val radioGroup = binding.radioGroup

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (binding.radioButtonTestAll.isChecked){


                binding.textInputLayoutPuerto.visibility = View.INVISIBLE

                Toast.makeText(this,"Puertos seleccionados:50000,51000,50500,52000,53000)",Toast.LENGTH_SHORT).show()
            }
            if (binding.radioButtonTestOne.isChecked){
                binding.textInputLayoutPuerto.visibility = View.VISIBLE



            }
        }

        binding.buttonTest.setOnClickListener{


            if (binding.radioButtonTestOne.isChecked == true){
                if (binding.textNumberOne.text.toString().isEmpty()) {
                    // El campo1 está vacío, mostrar un mensaje de error
                    binding.textNumberOne.error = "Debe ingresar un valor"
                    return@setOnClickListener
                }
                if (binding.textNumberTwo.text.toString().isEmpty()) {
                    // El campo1 está vacío, mostrar un mensaje de error
                    binding.textNumberTwo.error = "Debe ingresar un valor"
                    return@setOnClickListener
                }
                if (binding.textNumberThree.text.toString().isEmpty()) {
                    // El campo1 está vacío, mostrar un mensaje de error
                    binding.textNumberThree.error = "Debe ingresar un valor"
                    return@setOnClickListener
                }
                if (binding.textNumberFour.text.toString().isEmpty()) {
                    // El campo1 está vacío, mostrar un mensaje de error
                    binding.textNumberFour.error = "Debe ingresar un valor"
                    return@setOnClickListener
                }
                if (binding.comboBoxMaterialDesign.text.toString().isEmpty()) {
                    // El campo1 está vacío, mostrar un mensaje de error
                    binding.comboBoxMaterialDesign.error = "Ingresa un Puerto"
                    return@setOnClickListener
                }

                val server = binding.textNumberOne.text.toString() + "." + binding.textNumberTwo.text.toString() + "." + binding.textNumberThree.text.toString() + "." + binding.textNumberFour.text.toString()
                val puerto:String = binding.comboBoxMaterialDesign.text.toString()
//                val puerto:String= binding.spinner.selectedItem.toString()

                testServer(server, puerto)
            }
            if (binding.radioButtonTestAll.isChecked == true){
                if (binding.textNumberOne.text.toString().isEmpty()) {
                    // El campo1 está vacío, mostrar un mensaje de error
                    binding.textNumberOne.error = "Campo vacío"
                    return@setOnClickListener
                }
                if (binding.textNumberTwo.text.toString().isEmpty()) {
                    // El campo1 está vacío, mostrar un mensaje de error
                    binding.textNumberTwo.error = "Campo vacío"
                    return@setOnClickListener
                }
                if (binding.textNumberThree.text.toString().isEmpty()) {
                    // El campo1 está vacío, mostrar un mensaje de error
                    binding.textNumberThree.error = "campo vacío"
                    return@setOnClickListener
                }
                if (binding.textNumberFour.text.toString().isEmpty()) {
                    // El campo1 está vacío, mostrar un mensaje de error
                    binding.textNumberFour.error = "Campo vacío"
                    return@setOnClickListener
                }

                val server = binding.textNumberOne.text.toString() + "." + binding.textNumberTwo.text.toString() + "." + binding.textNumberThree.text.toString() + "." + binding.textNumberFour.text.toString()
                testWithAllThePorts(server)
//                Toast.makeText(this, "Todavia no esta implementado", Toast.LENGTH_SHORT).show()
            }

        }



    }

    private fun validateFieldEmpty(){
        if (binding.textNumberOne.text.toString().isEmpty()) {
            // El campo1 está vacío, mostrar un mensaje de error
            binding.textNumberOne.error = "Campo vacío"
            return
        }
    }

    private fun navigateToResultOne(host: String, port: String, isConnected: Boolean) {
        val intent = Intent(this, ResultOneTestActivity::class.java)

        intent.putExtra(HOST, host)
        intent.putExtra(PORT, port)
        intent.putExtra(CONNECTED, isConnected)
        startActivity(intent)
    }

    private fun navigateToResultMany(host: String, portConnected: Array<String>, portNotConnected:Array<String>) {
        val intent = Intent(this, ResultManyTests::class.java)

        intent.putExtra(HOST, host)
        intent.putExtra(PORTSCONNECTED, portConnected)
        intent.putExtra(PORTSNOTCONNECTED, portNotConnected)
        startActivity(intent)
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
        var puertosConnected = mutableListOf<String>()
        var puertosNotConnected = mutableListOf<String>()



        val loading = LoadingDialog(this@MainActivity)
        loading.startLoading()
        CoroutineScope(Dispatchers.IO).launch {

            var test = mutableListOf<TestBody>()
                    for (puerto in puertos) {

//                        val test = listOf<TestBody>(TestBody(server, puerto) )

                        test.add(TestBody(server, puerto))


                    }
            println("test -${test}")
                    val objetbodyTest = Body(test)
                    val call: Response<ServerResponse> = getRetrofit().create(ApiService::class.java).validateServer(objetbodyTest)
                    val bodyResponse = call.body()
                    println(bodyResponse)
                    runOnUiThread{
                        if (call.isSuccessful){

                            val testObject = bodyResponse?.tests
                            // recorre objetos y sacar la respuesta
                            // obtenermos los tests

                            for (t in testObject!!){
                                println(t)
                                val successParameter:Boolean? = t?.success
                                if (successParameter == true){
                                    puertosConnected.add(t.port)
                                    println("puerto agregado ${t.port}")
                                }
                                else{puertosNotConnected.add(t.port)}

                            }
                            println("Response after for ${puertosConnected}")


                            loading.isDismiss()
                            navigateToResultMany(server, puertosConnected.toTypedArray(), puertosNotConnected.toTypedArray())


                        }

                    }

//            }


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
            val responseBody = call.body()
            println(responseBody)
            runOnUiThread{
                if (call.isSuccessful){
//                    binding.textView.text = pokemonBody?.tests.toString()
                    val testObject = responseBody?.tests
                        val host = testObject?.get(0)?.host.toString()
                        val port = testObject?.get(0)?.port.toString()
                        val success = testObject?.get(0)?.success
                        navigateToResultOne(host, port, success!!)

                    }

                else{
                    Toast.makeText(this@MainActivity, "Ha Ocurrido un error", Toast.LENGTH_SHORT).show()
                }

            }
            loading.isDismiss()

        }

    }
}