package com.example.callsimulator.model

data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val photoUri: String? = null
)
