package com.example.walkinmyshoes
import Utilities.Channels
import Utilities.User
import Utilities.channelNames
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.channel_row.view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*




lateinit var x:User
lateinit var uid:String


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        addChannelprogressBar. visibility = View.INVISIBLE

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null) {
            mainChannelView.text = ""
            uid = FirebaseAuth.getInstance().currentUser!!.uid
            val currentUser = FirebaseDatabase.getInstance().getReference("/users/$uid/Profile")

            currentUser.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                override fun onDataChange(p0: DataSnapshot){
                    x = p0.getValue(User::class.java)!!
                    userNameNavHeader.setText(x.userName)
                    userE_mailNavHeader.setText(x.email)
                }

            })
            displayChannels()

        }
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )

            drawer_layout.addDrawerListener(toggle)
            toggle.syncState()

    }
    fun displayChannels(){
        val adapter=GroupAdapter<ViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference("/Channels/")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val ChannelDetails = p0.getValue(Channels::class.java)
                    if (ChannelDetails != null)
                        adapter.add(channelItem(ChannelDetails))
                    addChannelRecyclerView.adapter = adapter


                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })




    }

    override fun onBackPressed() {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                System.exit(-1)
            }
        }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.right->{

                FirebaseAuth.getInstance().signOut()
                if(userNameNavHeader.text.toString()=="")
                    Toast.makeText(this,"No user is logged in",Toast.LENGTH_SHORT).show()
                else {
                    val intent = Intent(this, activity_login::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
            return super.onCreateOptionsMenu(menu)
    }


    fun loginBtnNavClicked(view: View) {
        val loginIntent = Intent(this, activity_login::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
    }

    fun addChannelBtnClicked(view: View) {
            if(userNameNavHeader.text.toString()!=""){
                val builder = AlertDialog.Builder(this)
                val dilougeView = layoutInflater.inflate(R.layout.add_channel,null)
                
                builder.setView(dilougeView)
                    .setPositiveButton("Add"){ dilougeInterface, i ->
                        addChannelprogressBar. visibility = View.VISIBLE
                        val chanelName =dilougeView.findViewById<EditText>(R.id.addChannelName)
                        val channelDiscription = dilougeView.findViewById<EditText>(R.id.addChannelDescription)
                        val chName = chanelName.text.toString()
                        val chDescription = channelDiscription.text.toString()
                        val channel = Channels(chName,chDescription)
                        val ChNameRef = FirebaseDatabase.getInstance().getReference("/Channels/$chName")
                        ChNameRef.setValue(channel)
                            .addOnSuccessListener {

                                addChannelprogressBar. visibility = View.INVISIBLE
                            }

                    }
                    .setNegativeButton("Cancel"){dialogInterface, i ->

                    }.show()

            }
            else{
                Toast.makeText(this,"Please Login First",Toast.LENGTH_SHORT).show()

            }
        }

        fun sendMessageBtnClicked(view: View) {

        }

        fun registerBtnNavHeaderClicked(view: View) {
            val registerIntent = Intent(this, activity_creat_user::class.java)
            registerIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(registerIntent)
        }
    }
class channelItem(val channel:Channels):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.channelRowName.text = channel.channelName
        viewHolder.itemView.channelRowDescription.text = channel.channelDescription
    }

    override fun getLayout(): Int {
        return R.layout.channel_row

    }
}

