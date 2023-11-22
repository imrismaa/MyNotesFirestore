package com.example.tugasppapb13.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tugasppapb13.Note
import com.example.tugasppapb13.NoteAdapter
import com.example.tugasppapb13.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollectionRef = firestore.collection("notes")
    private val noteListLiveData: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            btnAdd.setOnClickListener {
                startActivity(Intent(
                    this@MainActivity, DetailActivity::class.java))
            }
        }
        observeNotes()
        getAllNotes()
    }

    private fun getAllNotes() {
        observeBudgetsChange()
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }

    private fun observeBudgetsChange() {
        notesCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity",
                    "Error Listening for budget changes:", error)
            }
            val budgets = snapshots?.toObjects(Note::class.java)
            if (budgets != null) {
                noteListLiveData.postValue(budgets)
            }
        }
    }

    //update adapter tiap livedata berubah
    private fun observeNotes() {
        noteListLiveData.observe(this) { notes ->
            val adapterNote = NoteAdapter(notes) { note ->
                startActivity(Intent(this@MainActivity, DetailActivity::class.java)
                    .putExtra("note", note)
                )
            }

            adapterNote.setOnClickDeleteListener { note ->
                deleteNote(note)
            }

            with(binding) {
                recyclerView.apply {
                    adapter = adapterNote
                    layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }
        }
    }

    private fun deleteNote(note: Note) {
        if(note.id.isEmpty()) {
            Log.d("MainActivity", "Error deleting item: budget Id is empty!")
            return
        }
        notesCollectionRef.document(note.id).delete().addOnFailureListener {
            Log.d("MainActivity", "Error deleting budget", it)
        }
        Toast.makeText(this@MainActivity, "Note deleted",
            Toast.LENGTH_SHORT).show()
    }
}