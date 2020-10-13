package com.unitec.apptextoavoz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    //ESTE OBJETO ES EL INTERMEDARIO ENTRE NUESTRA APP Y TEXTOTOSPEECH
    private var tts:TextToSpeech?=null
    //el siguiente codigo de peticion es un entero, que nos va ayudar a garantizar el objeto TextoToSpeech
    //Se inicio completamente
    private val CODIGO_PETICION=100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Iniciamos ahora i la variable tts para que ya no este null
        tts= TextToSpeech(this,this)
        Hablar.setOnClickListener{
            val intent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            try{
                startActivityForResult(intent,CODIGO_PETICION)
            }catch (e:Exception){ }
        }

     //programamos el click del boton para que interprete lo inscrito
        interpretar.setOnClickListener{
            if(fraseEscrita.text.isEmpty()){
                Toast.makeText( this, "Debes escribir algo para que lo hable",Toast.LENGTH_SHORT).show()

            }else{
                //Este método ahorita la vamos a implementar
                hablarTexto(fraseEscrita.text.toString())

            }
        }

        //KEMOSION!!! VAMOS ESCUCHAR ESA VOCESITA DE ANDROID, DE BIENVENIDA


        Timer("Bienvenida",false).schedule(1000){
           tts!!.speak(
               "Hola bienvenido a mi aplicación",
               TextToSpeech.QUEUE_FLUSH,
               null,
               ""

           )
        }
    }

    override fun onInit(estado: Int) {
        //ESTE O FUNCION SIRVE PARA QUE SE INICIALIZE LA CONFIGURACION AL ARRANCAR LA APP.(IDIOMA)
        if(estado==TextToSpeech.SUCCESS){
//si el if se cumplio la ejeccucion seguira aqui adrentro
            var local=Locale("spa","MEX")
            //la siguiente variables es para que internamente sepamos que la app va bien
            var resultado=tts!!.setLanguage(local)
            if(resultado==TextToSpeech.LANG_MISSING_DATA){
                Log.i("MALO","NOOOOOO, NO FUNCIONO EL LENGUAJE ALGO ANDA MAL")
            }

        }
    }
    //esta funcion es la que nos ayuda interpretar lo que se escriba en el texto de la frase

    fun hablarTexto(textHablar:String){
        tts!!.speak(textHablar, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    //Este metodo es opcional pero lo recomiendo para limpiar memoria de esta app cuando la cierren
    override fun onDestroy(){
        super.onDestroy()
        if(tts!=null){
            //en el caso de las aplicaciones de espionaje estos dos renglones NUNCA SE APAGAN
            tts!!.stop()
            tts!!.shutdown()
        }
    }
    //EL SIGUIENTE METODO SIRVE PARA INICIAR OTRA ACTIVIDAD SOBRE ESTA (ESTA SERA EL MICROFONO DE GOOGLE)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CODIGO_PETICION->{
                if(resultCode== Activity.RESULT_OK &&null!=data){
                    val result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    //finalmente le vamos a decir a nuestro texto estatico que aqui nos muestre lo
                    //dijimos pero en texto
                    textoInterpletado.setText(result!![0])
                }
            }
        }
    }
}