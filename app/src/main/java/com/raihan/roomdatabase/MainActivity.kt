package com.raihan.roomdatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.raihan.roomdatabase.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var studentDatabase:StudentDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        studentDatabase = StudentDatabase.getDatabase(this)


        binding.savebtn.setOnClickListener {
            saveData()
        }
        binding.searchbutton.setOnClickListener {
            searchData()
        }
        binding.deleteAllbtn.setOnClickListener {
            GlobalScope.launch {
                studentDatabase.studentDao().deleteAll()
            }
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun searchData() {
       val rollNo = binding.searchbybtnEtxt.text.toString()
        if(rollNo.isNotEmpty()){
            lateinit var student:Student
            GlobalScope.launch {
                student = studentDatabase.studentDao().findByRoll(rollNo.toInt())
                if(studentDatabase.studentDao().isEmpty())
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@MainActivity,"Database have no data", Toast.LENGTH_SHORT).show()
                    }
                else{
                    displayData(student)
                }
            }
        }
    }

    private suspend fun displayData(student: Student) {
        withContext(Dispatchers.Main){
            binding.fastnameEtxt.setText(student.firstName.toString())
            binding.lastnameEtxt.setText(student.lastName.toString())
            binding.rollnumberEtxt.setText(student.rollNo.toString())
        }

    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun saveData() {
        val firstName = binding.fastnameEtxt.text.toString()
        val lastName = binding.lastnameEtxt.text.toString()
        val rollNo = binding.rollnumberEtxt.text.toString()

        if(firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()){
            val student = Student(null,firstName,lastName,rollNo.toInt())
            GlobalScope.launch(Dispatchers.IO){
                studentDatabase.studentDao().insert(student)
            }
            binding.fastnameEtxt.text.clear()
            binding.lastnameEtxt.text.clear()
            binding.rollnumberEtxt.text.clear()

            Toast.makeText(this@MainActivity,"Data saved", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this@MainActivity,"Please enter all the data", Toast.LENGTH_SHORT).show()
        }


    }
}