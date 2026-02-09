package com.gabriel.employeeapp.domain.repository

import com.gabriel.employeeapp.data.model.Student

interface StudentRepository {
    suspend fun getAllStudents(): Result<List<Student>>
    suspend fun getStudentById(id: Long): Result<Student>
    suspend fun addStudent(student: Student): Result<Student>
    suspend fun updateStudent(student: Student): Result<Student>
    suspend fun deleteStudent(id: Long): Result<Unit>
}