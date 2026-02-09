package com.gabriel.employeeapp.data.remote

import com.gabriel.employeeapp.data.model.Student
import retrofit2.Response
import retrofit2.http.*

interface StudentApi {
    @GET("/api/student")
    suspend fun getAllStudents(): Response<List<Student>>
    
    @GET("/api/student/{id}")
    suspend fun getStudentById(@Path("id") id: Long): Response<Student>
    
    @POST("/api/student")
    suspend fun addStudent(@Body student: Student): Response<Student>
    
    @PUT("/api/student/{id}")
    suspend fun updateStudent(@Path("id") id: Long, @Body student: Student): Response<Student>
    
    @DELETE("/api/student/{id}")
    suspend fun deleteStudent(@Path("id") id: Long): Response<Unit>
}