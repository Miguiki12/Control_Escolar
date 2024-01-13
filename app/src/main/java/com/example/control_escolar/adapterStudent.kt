package com.example.control_escolar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapterStudent(private val students: ArrayList<Student>) :
    RecyclerView.Adapter<adapterStudent.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.student_photo)
        val nameTextView: TextView = itemView.findViewById(R.id.student_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.photoImageView.setImageResource(student.photo)
        holder.nameTextView.text = student.name
    }

    override fun getItemCount() = students.size

}