package com.adikmt.taskBoard.dtos.responses

data class UserResponse(
    val userId: Int?,
    val userName: String?,
    val userPassword: String?,
    val jwtToken: String
)
