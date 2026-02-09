package com.gabriel.employeeapp.data.repository

import android.app.Application
import com.gabriel.employeeapp.data.remote.StudentApi
import com.gabriel.employeeapp.data.model.Student
import com.gabriel.employeeapp.domain.repository.StudentRepository
import com.gabriel.employeeapp.di.RetrofitClient

class StudentRepositoryImpl(
    private val appContext: Application
) : StudentRepository {
    
    // Using real Retrofit API for backend connection
    private val api: StudentApi = RetrofitClient.instance.create(StudentApi::class.java)
    
    override suspend fun getAllStudents(): Result<List<Student>> {
        return try {
            val response = api.getAllStudents()
            if (response.isSuccessful) {
                response.body()?.let { students ->
                    Result.success(students)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to fetch students: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getStudentById(id: Long): Result<Student> {
        return try {
            val response = api.getStudentById(id)
            if (response.isSuccessful) {
                response.body()?.let { student ->
                    Result.success(student)
                } ?: Result.failure(Exception("Student not found"))
            } else {
                Result.failure(Exception("Failed to fetch student: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addStudent(student: Student): Result<Student> {
        return try {
            val response = api.addStudent(student)
            if (response.isSuccessful) {
                response.body()?.let { createdStudent ->
                    Result.success(createdStudent)
                } ?: Result.failure(Exception("Failed to create student"))
            } else {
                Result.failure(Exception("Failed to create student: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateStudent(student: Student): Result<Student> {
        return try {
            val response = api.updateStudent(student.id ?: 0L, student)
            if (response.isSuccessful) {
                response.body()?.let { updatedStudent ->
                    Result.success(updatedStudent)
                } ?: Result.failure(Exception("Failed to update student"))
            } else {
                Result.failure(Exception("Failed to update student: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteStudent(id: Long): Result<Unit> {
        return try {
            val response = api.deleteStudent(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete student: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}