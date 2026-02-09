package com.gabriel.employeeapp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.gabriel.employeeapp.data.model.Student
import android.app.Application
import com.gabriel.employeeapp.data.repository.StudentRepositoryImpl

class StudentViewModel(
    private val application: Application
) : ViewModel() {
    
    private val repository = StudentRepositoryImpl(application)
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> get() = _students
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    init {
        fetchStudents()
    }

    fun fetchStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.getAllStudents()
                result.onSuccess { newStudents ->
                    _students.value = newStudents
                }.onFailure { error ->
                    _errorMessage.value = error.message
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addStudent(student: Student) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.addStudent(student)
                result.onSuccess { newStudent ->
                    _students.value = _students.value + newStudent
                }.onFailure { error ->
                    _errorMessage.value = error.message
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.updateStudent(student)
                result.onSuccess { updatedStudent ->
                    val currentStudents = _students.value.toMutableList()
                    val index = currentStudents.indexOfFirst { it.id == updatedStudent.id }
                    if (index >= 0) {
                        currentStudents[index] = updatedStudent
                        _students.value = currentStudents
                    }
                }.onFailure { error ->
                    _errorMessage.value = error.message
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteStudent(studentId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.deleteStudent(studentId)
                result.onSuccess {
                    _students.value = _students.value.filter { it.id != studentId }
                }.onFailure { error ->
                    _errorMessage.value = error.message
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteStudent(student: Student) {
        student.id?.let { deleteStudent(it) }
    }

    fun searchStudents(query: String) {
        _searchQuery.value = query
    }

    fun clearError() {
        _errorMessage.value = null
    }
}