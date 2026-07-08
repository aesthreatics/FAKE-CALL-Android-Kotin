package com.sundaesploits.fakecall

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.w3c.dom.Text
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    private val CALL_LOG_PERMISSION_CODE = 101

    private fun hasCallLogPermission(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)
        return readPermission == PackageManager.PERMISSION_GRANTED &&
                writePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCallLogPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG),
            CALL_LOG_PERMISSION_CODE
        )
    }

    fun addCallLogEntry(context: Context, number: String, type: String, time: Long=1, duration:Long=1):String {
        val callType = if(type == "Incoming")1 else if(type =="Outgoing")2 else if(type =="Missed")3 else if(type == "Voice")4 else if(type == "Rejected")5 else if(type == "Block")6 else if(type == "External")7 else 1
        return try{
            val contentResolver: ContentResolver = context.contentResolver
            val values = ContentValues().apply {
                put(CallLog.Calls.NUMBER, number)
                put(CallLog.Calls.TYPE, callType)
                put(CallLog.Calls.DATE, System.currentTimeMillis() - time*60000)
                put(CallLog.Calls.DURATION, duration)
            }
            contentResolver.insert(CallLog.Calls.CONTENT_URI, values)
            "created log  ph: $number \n type : $type \n time:  $time Mins ago \n duration : $duration \n"
        }catch (_: SecurityException) {
            return "CALL LOG Permission denied"
        }catch (e:Exception){
            "error : $e"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val callLogPermissionEnabled = hasCallLogPermission()
        if(!callLogPermissionEnabled){
            requestCallLogPermission()
        }

        //inputs
        val phone_number = findViewById<EditText>(R.id.phonenumber)
        val time_ago = findViewById<EditText>(R.id.timeago)
        val duration = findViewById<EditText>(R.id.duration)
        val calltype = findViewById<Spinner>(R.id.calltype)

        //button
        val buttonSubmit = findViewById<Button>(R.id.addlog)

        //link - github
        val githubLink = findViewById<TextView>(R.id.githubProfile)

        //load spinner values
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.callLogTypes,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        calltype.adapter = adapter


        //button evnet
        buttonSubmit.setOnClickListener {
            val number = phone_number.text.toString()  // get input value
            val time_ago = time_ago.text.toString()
            val duration = duration.text.toString()
            val call_type = calltype.selectedItem.toString()

            if(number!="" && time_ago!="" && duration!="" && call_type!=""){
                try{
                    val duration_long = duration.toLong()
                    val time_ago_long = time_ago.toLong()
                    val result = addCallLogEntry(this,number,call_type,time_ago_long,duration_long)
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                }catch(_: Exception){
                    Toast.makeText(this, "Time Ago and Duration should be number", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(this, "Incomplete fields, check and try again", Toast.LENGTH_SHORT).show()
            }

        }

        //github link text - event
        githubLink.setOnClickListener {
            val url = "https://github.com/sundaesploits"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = url.toUri()
            startActivity(intent)
        }


    }
}