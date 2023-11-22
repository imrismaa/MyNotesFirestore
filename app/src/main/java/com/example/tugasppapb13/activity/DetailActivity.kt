package com.example.tugasppapb13.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.tugasppapb13.Note
import com.example.tugasppapb13.databinding.ActivityDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollectionRef = firestore.collection("notes")
    private var updateId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            btnBack.setOnClickListener {
                onBackPressed()
            }

            if (intent.hasExtra("note")) {
                val note = intent.getSerializableExtra("note") as Note

                updateId = note.id
                editTitle.setText(note.title)
                editDescription.setText(note.description)

                btnSave.setOnClickListener {

                    val title = editTitle.text.toString()
                    val description = editDescription.text.toString()

                    val editedNote = Note(
                        title = title,
                        description = description
                    )

                    updateNote(editedNote)
                    returnToMainActivity(editedNote)
                    Toast.makeText(this@DetailActivity, "Note updated",
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else {
                btnSave.setOnClickListener {
                    if(editTitle.text.toString().isNotEmpty() &&
                        editDescription.text.toString().isNotEmpty()) {

                        val title = editTitle.text.toString()
                        val description = editDescription.text.toString()

                        val newNote = Note(
                            title = title,
                            description = description
                        )
                        insertNote(newNote)
                        returnToMainActivity(newNote)
                        Toast.makeText(this@DetailActivity, "Note saved",
                            Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else {
                        if(editTitle.text.toString().isEmpty()) {
                            editTitle.error = "Title must not be empty"
                        }
                        if(editDescription.text.toString().isEmpty()) {
                            editDescription.error = "Description must not be empty"
                        }
                    }
                }
            }
        }
    }

    private fun returnToMainActivity(note: Note) {
        val detailIntent = Intent().apply {
            putExtra("editeNote", note)
        }
        setResult(RESULT_OK, detailIntent)
        finish()
    }

    private fun insertNote(note: Note) {
        notesCollectionRef.add(note).addOnSuccessListener {
                documentReference ->     //punya properties id
            val createdBudgetId = documentReference.id
            note.id = createdBudgetId  //update id sesuai id dari document reference
            documentReference.set(note).addOnFailureListener {
                Log.d("MainActivity", "Error updating budget id : ", it)
            }
        }.addOnFailureListener {
            Log.d("MainActivity", "Error updating budget id : ", it)
        }
    }

    private fun updateNote(note: Note) {
        note.id = updateId
        notesCollectionRef.document(updateId).set(note).addOnFailureListener {
            Log.d("MainActivity", "Error updating budget", it)
        }
    }
}