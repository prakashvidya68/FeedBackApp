package com.example.walkinmyshoes

import Utilities.UserDataServices
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
var auth:FirebaseAuth= FirebaseAuth.getInstance()
class activity_login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        LoginProgressBar.visibility =View.INVISIBLE
    }



    fun loginLoginBtnClicked(view: View) {

        if(loginEmailText.text.toString()!="" && loginPasswordText.text.toString()!=""){

            LoginProgressBar.visibility =View.VISIBLE

            auth.signInWithEmailAndPassword(loginEmailText.text.toString(),loginPasswordText.text.toString())
                .addOnCompleteListener{

                    LoginProgressBar.visibility =View.INVISIBLE
                    Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
                    val homePage = Intent(this,MainActivity::class.java)
                    homePage.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(homePage)
                }
                .addOnFailureListener {
                    LoginProgressBar.visibility =View.INVISIBLE
                    Toast.makeText(this," Email and password don't match to " +
                            "any of the existing users.",Toast.LENGTH_LONG).show()

                }
        }

        else
            Toast.makeText(this,"Please Fill All Required Fields",Toast.LENGTH_LONG).show()
    }

    fun loginCreatUserBtnClicked(view: View){
        val RegisterPageIntent = Intent(this,activity_creat_user::class.java)
        RegisterPageIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(RegisterPageIntent)
    }
}
