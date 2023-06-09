package com.adikmt.taskBoard.dtos.common.mappers

import com.adikmt.taskBoard.dtos.responses.JWTUserResponse
import com.adikmt.taskBoard.dtos.responses.UserResponse
import com.adikmt.taskBoard.security.JwtSupport
import jooq.generated.tables.records.UsersRecord

fun UsersRecord.toUserResponse() = UserResponse(
    userId = this.id,
    userName = this.userName,
    userPassword = this.userPassword
)

fun jwtUserResponse(userId: Int) = JWTUserResponse(
    userId = userId,
    token = JwtSupport().generate(userId.toString()).value
)