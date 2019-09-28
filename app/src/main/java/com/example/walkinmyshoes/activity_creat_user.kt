package com.example.walkinmyshoes


import Utilities.User
import Utilities.UserDataServices
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_creat_user.*
import java.util.*


class activity_creat_user : AppCompatActivity() {
    var selectedPhotoAddress:Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creat_user)
        signUpProgressBar.visibility=View.INVISIBLE
    }

    fun addDpClicked(view: View){
        val getImage =Intent(Intent.ACTION_PICK)
        getImage.type= "image/*"
        startActivityForResult(getImage,0)

    }public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null) {
            selectedPhotoAddress = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoAddress)
            circularDP.setImageBitmap(bitmap)
            creatDPImageView.alpha=0f
        }
    }

    fun creatUserBtnClicked(view: View){
        UserDataServices.username = creatUserNameText.text.toString()
        UserDataServices.usn =creatUsnText.text.toString()
        UserDataServices.email =creatEmailText.text.toString()
        UserDataServices.password=creatPassword.text.toString()
        if(UserDataServices.username!=""&&UserDataServices.usn!=""&&UserDataServices.email!=""&&UserDataServices.password!=""){
            signUpProgressBar.visibility = View.VISIBLE
            val mAuth:FirebaseAuth= FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(UserDataServices.email, UserDataServices.password)
                    .addOnCompleteListener {
                        while(UserDataServices.uid=="")
                            UserDataServices.uid =FirebaseAuth.getInstance().currentUser!!.uid.toString()
                        uploadImageToFirebase()
                    }
        }
        else{
            Toast.makeText(this,"Please fill all Required Feilds",Toast.LENGTH_SHORT).show()

        }
    }

    private fun uploadImageToFirebase(){

        if(selectedPhotoAddress==null)return
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/DisplayPictures/${UserDataServices.username}/$filename")
            val Url = ref.downloadUrl.toString()
        ref.putFile(selectedPhotoAddress!!)
            .addOnSuccessListener {
                    Log.d("User DP","URL for DP that can be used to access Image is ${Url}")
            }
        saveUserToFirebaseDB(Url)
    }


    private fun saveUserToFirebaseDB(profileDpURL:String){

        val homeScrn = Intent(this,MainActivity::class.java)
        val reff =FirebaseAuth.getInstance()
        val uid = reff.uid.toString()
        val ref =FirebaseDatabase.getInstance().getReference("users/$uid/Profile")
        val user = User(uid,creatUserNameText.text.toString(),creatUsnText.text.toString(),profileDpURL,creatEmailText.text.toString())
        ref.setValue(user).addOnSuccessListener {
            signUpProgressBar.visibility = View.INVISIBLE
            Toast.makeText(this,"SignUp Successful",Toast.LENGTH_LONG).show()
            homeScrn.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(homeScrn)
        }


    }

}