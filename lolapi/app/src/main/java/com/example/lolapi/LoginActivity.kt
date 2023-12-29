package com.example.lolapi

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.server.response.SafeParcelResponse.from
import com.google.firebase.auth.FirebaseAuth
import java.util.Date.from

class LoginActivity : AppCompatActivity() {

    private lateinit var txtUser: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var auth:FirebaseAuth

    private var canAuthenticate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtUser=findViewById(R.id.txtUser)
        txtPassword=findViewById(R.id.txtPassword)
        progressBar= findViewById(R.id.progressBar)
        auth= FirebaseAuth.getInstance()
    }
    fun forgotPassword(view: View)
    {
       // startActivity(Intent(this,ForgotPassActivity::class.java))
    }

    fun register(view: View)
    {
        startActivity(Intent(this,RegisterActivity::class.java))
    }
    fun login(view: View)
    {
        loginUser()
    }

    private fun loginUser()
    {
        val user:String=txtUser.text.toString()
        val password:String=txtPassword.text.toString()

        //Comprobando que todos los campos esten llenos
        if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password))
        {

            progressBar.visibility=View.VISIBLE

            //Inicio de sesion
            auth.signInWithEmailAndPassword(user,password).addOnCompleteListener(this)
            {
                task ->

                //Verificando si se han puesto bien las credenciales
                if(task.isSuccessful)
                {
                    action()
                }
                else
                {
                    Toast.makeText(this, "Error en la autentificacion", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun action()
    {
        startActivity(Intent(this,MainActivity::class.java))
    }
}