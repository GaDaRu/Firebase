package com.example.pruebafirebase

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.pruebafirebase.modelo.Web
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}

private lateinit var database: DatabaseReference

private lateinit var databaseusuario: DatabaseReference

private lateinit var eventListener: ValueEventListener

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val parametros = intent.extras
        val email = parametros?.getString("email")
        val provider = parametros?.getString("provider")

        setup(email ?: "", provider ?: "")

        val pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()
        pref.putString("email", email)
        pref.putString("provider", provider)
        pref.apply()

        database = Firebase.database.reference

        buttonAnadir.setOnClickListener { altaCursoSimple() }
        buttonModificar.setOnClickListener { modificar() }
        buttonEliminar.setOnClickListener { eliminar() }
        buttonMostrar.setOnClickListener { mostrar() }
        buttonborrarDatos.setOnClickListener { borrar() }
    }

    private fun borrar() {
        textViewUsuario.setText("")
        textViewcontrasena.setText("")
    }

    private fun mostrar() {
        val direccion = editTextTextWeb.text.toString();

        if(TextUtils.isEmpty(direccion)){editTextTextWeb.setError("Sin datos")}

        database.child("contrasenas").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //val children = dataSnapshot!!.children

                    val ruta = Web()

                    //children.forEach{
                    ruta.contrasena = dataSnapshot.child(direccion).child("contrasena").value as String
                    ruta.usuario =  dataSnapshot.child(direccion).child("usuario").value as String
                    //}

                    textViewUsuario.setText(ruta.usuario)
                    textViewcontrasena.setText(ruta.contrasena)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    textViewUsuario.setText("Error")
                    textViewcontrasena.setText("Error")
                }
            })
    }

    private fun eliminar() {
        val direccion = editTextTextWeb.text.toString();

        if(TextUtils.isEmpty(direccion)){editTextTextWeb.setError("Sin datos")}

        database.child("contrasenas").child(direccion).removeValue()
    }

    private fun modificar() {
        val direccion = editTextTextWeb.text.toString();
        val usuario = editTextUsuario.text.toString()
        val contrasena = editTextContrasena.text.toString()

        if(TextUtils.isEmpty(direccion)){editTextTextWeb.setError("Sin datos")}
        if(TextUtils.isEmpty(usuario)){editTextUsuario.setError("Sin datos")}
        if(TextUtils.isEmpty(contrasena)){editTextContrasena.setError("Sin datos")}

        val web = Web()
        web.web = direccion
        web.usuario = usuario
        web.contrasena = contrasena

        database.child("contrasenas").child(direccion).child("contrasena").setValue(contrasena)
        database.child("contrasenas").child(direccion).child("usuario").setValue(usuario)
    }

    private fun altaCursoSimple() {
        val direccion = editTextTextWeb.text.toString();
        val usuario = editTextUsuario.text.toString()
        val contrasena = editTextContrasena.text.toString()

        if(TextUtils.isEmpty(direccion)){editTextTextWeb.setError("Sin datos")}
        if(TextUtils.isEmpty(usuario)){editTextUsuario.setError("Sin datos")}
        if(TextUtils.isEmpty(contrasena)){editTextContrasena.setError("Sin datos")}

        val web = Web()
        web.web = direccion
        web.usuario = usuario
        web.contrasena = contrasena

        database.child("contrasenas").child(direccion).setValue(web)
    }

    private fun setup(email: String, provider: String){

        textViewEmail.text = "Cuenta :"+email

        buttonSalir.setOnClickListener {
            val pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE).edit()
            pref.clear()
            pref.apply()

            if(provider == ProviderType.FACEBOOK.name){
                LoginManager.getInstance().logOut()
            }

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}