package com.example.udpchat

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.udpchat.TokenConsts.TOKEN_SPACE
import kotlinx.android.synthetic.main.activity_msg_box.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class MsgBoxActivity : AppCompatActivity() {
    //  Initializations
    private val msgList=ArrayList<Msg>()
    lateinit var socket:DatagramSocket
    lateinit var serverIp:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msg_box)

        //  Setting strict mode to allow running async task on main thread
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        //  Getting intent extras and opening a socket
        val port=intent.getIntExtra("port",9845)
        socket=DatagramSocket(port)
        serverIp=intent.getStringExtra("ip").toString()
        val username= intent.getStringExtra("name").toString()
        val iPAdd=InetAddress.getByName(serverIp)

        /*  Recycler view setting
            -   Set a layout manager
            -   assign the adapter
         */
        val layoutMan=LinearLayoutManager(this)
        layoutMan.stackFromEnd=true
        recV.layoutManager=layoutMan
        val adapter=MessagesAdapter(msgList,this)
        recV.adapter=adapter


        //  Send Msg click Listener
        sendMsg.setOnClickListener {
            val inMsg=editTextMsg.editableText.toString()
            if (inMsg.isEmpty()){
                Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show()
            }
            else{
                val sendBuf=(username+TOKEN_SPACE+inMsg).toByteArray()
                socket.send(DatagramPacket(sendBuf,sendBuf.size,iPAdd,53123))
                msgList.add(Msg(username,inMsg,1))
                adapter.notifyDataSetChanged()
                editTextMsg.editableText.clear()
                recV.scrollToPosition(msgList.size-1)
            }

        }

        //  Thread for receiving the packets
        Thread{
            while(true){
                val receivebuffer = ByteArray(1024)
                val recvdpkt = DatagramPacket(receivebuffer, receivebuffer.size)
                try{
                    socket.receive(recvdpkt)
                    val recData= String(recvdpkt.data).trim()
                    val (n,m)=recData.split(TOKEN_SPACE)
                    runOnUiThread {
                        msgList.add(Msg(n,m,0))
                        adapter.notifyDataSetChanged()
                        recV.scrollToPosition(msgList.size-1)
                    }
                }catch (_:java.lang.Exception){
                    break
                }
            }
        }.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        //  Logout and close the socket
        socket.send(DatagramPacket(TokenConsts.TOKEN_LOGOUT.toByteArray(), TokenConsts.TOKEN_LOGOUT.toByteArray().size, InetAddress.getByName(serverIp), 9876))
        socket.close()
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        AlertDialog.Builder(this)
            .setTitle("Closing Activity")
            .setMessage("Are you sure you want to close this activity?")
            .setPositiveButton("Yes") { dialog, which -> finishAffinity() }
            .setNegativeButton("No", null)
            .show()
    }
}