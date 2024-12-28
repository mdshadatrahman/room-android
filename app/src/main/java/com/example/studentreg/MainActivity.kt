package com.example.studentreg

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentreg.db.Student
import com.example.studentreg.db.StudentDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var clearButton: Button

    private lateinit var viewModel: StudentViewModel

    private lateinit var studentRecyclerView: RecyclerView
    private lateinit var adapter: StudentRecyclerViewAdapter

    private var selectedStudent: Student? = null
    private var isListItemClicked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nameEditText = findViewById(R.id.etName)
        emailEditText = findViewById(R.id.etEmail)
        saveButton = findViewById(R.id.btnSave)
        clearButton = findViewById(R.id.btnClear)
        studentRecyclerView = findViewById(R.id.rvStudent)

        val dao = StudentDatabase.getInstance(application).studentDao()
        val factory = StudentViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[StudentViewModel::class.java]

        saveButton.setOnClickListener {
            if(isListItemClicked && selectedStudent != null){
                updateStudentData(selectedStudent!!)
                clearInput()
            } else {
                saveStudentData()
                clearInput()
            }
        }

        clearButton.setOnClickListener {
            if(isListItemClicked && selectedStudent != null){
                deleteStudent(selectedStudent!!)
                clearInput()
            } else {
                clearInput()
            }
        }

        initRecyclerView()
    }

    private fun saveStudentData() {
        viewModel.insertStudent(
            Student(
                id = 0,
                name = nameEditText.text.toString(),
                email = emailEditText.text.toString()
            )
        )
    }
    private fun updateStudentData(student: Student) {
        student.name = nameEditText.text.toString()
        student.email = emailEditText.text.toString()
        viewModel.updateStudent(student)
        Toast.makeText(
            this,
            "Student Data Updated",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteStudent(student: Student) {
        viewModel.deleteStudent(student)
        Toast.makeText(
            this,
            "Student Data Deleted",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun clearInput() {
        nameEditText.setText("")
        emailEditText.setText("")
        saveButton.text = "SAVE"
        saveButton.setBackgroundColor(Color.BLUE)
        clearButton.text = "CLEAR"
        clearButton.setBackgroundColor(Color.BLUE)
        isListItemClicked = false
        selectedStudent = null
    }

    private fun initRecyclerView() {
        studentRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentRecyclerViewAdapter {
            selectedItem: Student -> listItemClicked(selectedItem)
        }
        studentRecyclerView.adapter = adapter
        displayStudentsList()
    }

    private fun displayStudentsList() {
        viewModel.students.observe(this) {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun listItemClicked(student: Student) {
        selectedStudent = student
        saveButton.text = "UPDATE"
        saveButton.setBackgroundColor(Color.BLUE)
        clearButton.text = "DELETE"
        clearButton.setBackgroundColor(Color.RED)
        isListItemClicked = true
        nameEditText.setText(selectedStudent?.name)
        emailEditText.setText(selectedStudent?.email)
    }
}