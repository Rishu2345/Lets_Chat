package com.example.letschat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel(private val dao : TaskDao): ViewModel() {
    private var _userId by mutableStateOf("")


    fun getMassages(chatId:String): Flow<List<Messages>> {
        return dao.getAllMessages(chatId)
    }

    var _currentProfile: Friend? = null

    fun setCurrentProfile(profile: Friend){
        _currentProfile = profile
    }

    fun getCurrentProfile(): Friend?{
        return _currentProfile
    }

    fun setUserId(id: String) {
        _userId = id
    }
    fun getUserId(): String? {
        if (_userId.isEmpty()) return null
        return _userId
    }

    private var _chatId by mutableStateOf("")
    fun setChatId(id: String){
        _chatId = id

    }

    fun getChatId():String?{
        if(_chatId.isEmpty()) return null
        return _chatId
    }
    fun formateDate(localDate: LocalDate): String {
        if(localDate == LocalDate.now()) return "Today"
        if(localDate.plusDays(1) == LocalDate.now()) return "Yesterday"
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy ")
        return localDate.format(formatter)
    }

    fun generateUniqueCode(length: Int = 8): String {
        val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_#"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun formatLocalDateTime(dateTime: LocalDateTime): String {
        val today = LocalDate.now()
        val inputDate = dateTime.toLocalDate()

        val isToday = inputDate.isEqual(today)
        val isYesterday = inputDate.isEqual(today.minusDays(1))

        return when {
            isToday -> {
                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
                dateTime.format(timeFormatter)
            }
            isYesterday -> "Yesterday"
            else -> {
                val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                dateTime.format(dateFormatter)
            }
        }
    }




}