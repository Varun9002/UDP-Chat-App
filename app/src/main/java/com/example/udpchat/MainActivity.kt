package com.example.udpchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        editTextUsername.apply {
            alpha=0f
            translationY=500f
            animate().setInterpolator(LinearInterpolator()).alpha(1f).translationYBy(-500f).setDuration(1000).setListener(null)
        }
        editTextServerIp.apply {
            alpha=0f
            translationY=500f
            animate().setInterpolator(LinearInterpolator()).alpha(1f).translationYBy(-500f).setDuration(1000).setListener(null)
        }
        buttonStart.apply {
            alpha=0f
            translationY=500f
            animate().setInterpolator(LinearInterpolator()).alpha(1f).translationYBy(-500f).setDuration(1000).setListener(null)
        }

        val regex = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$"
        val p: Pattern = Pattern.compile(regex)

        buttonStart.setOnClickListener{
            val username=editTextUsername.editableText.toString()
            val ip=editTextServerIp.editableText.toString()

            if (username.isEmpty()){
                Toast.makeText(this, "Enter Username", Toast.LENGTH_LONG).show()
            } else if(!p.matcher(ip).matches()){
                Toast.makeText(this, "Invalid IP address", Toast.LENGTH_LONG).show()
            } else {
                progBar.visibility=View.VISIBLE
                connect(username, ip)
            }
        }
    }
    private fun connect(user:String, ip:String){
        val ipAdd=InetAddress.getByName(ip)
        val socket=DatagramSocket()
//        var flag=false

        val t=Thread{
            try {
                val buf=(TokenConsts.TOKEN_LOGIN+user).toByteArray()
                socket.send(DatagramPacket(buf,buf.size,ipAdd,53123))
                socket.soTimeout=10000
                val recbuf=ByteArray(1024)
                val rec=DatagramPacket(recbuf,recbuf.size)
                socket.receive(rec)
                if (String(rec.data).contains(TokenConsts.TOKEN_ACK)){
                runOnUiThread {
                    val msgboxIntent=Intent(this,MsgBoxActivity::class.java)
                    msgboxIntent.putExtra("name",user)
                    msgboxIntent.putExtra("ip",ip)
                    msgboxIntent.putExtra("port",  socket.localPort)
                    socket.close()
                    startActivity(msgboxIntent)
                    }
                }
            }catch (e:java.lang.Exception){
                runOnUiThread {
                    Toast.makeText(this, "Unable to connect to Server", Toast.LENGTH_SHORT).show()
                }
            }
            finally {
                runOnUiThread {
                    progBar.visibility=View.GONE
                }
            }
        }
        t.start()
    }
}