package com.adikmt.taskBoard.services.users

import com.adikmt.taskBoard.dtos.common.wrappers.DbResponseWrapper
import com.adikmt.taskBoard.dtos.requests.LoginUserRequest
import com.adikmt.taskBoard.dtos.requests.UserRequest
import com.adikmt.taskBoard.dtos.responses.UserResponse
import com.adikmt.taskBoard.repositories.users.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl
@Autowired constructor(private val userRepository: UserRepository) : UserService {
    override fun login(loginUserRequest: LoginUserRequest): DbResponseWrapper<out UserResponse> {
        try {
            val user = userRepository.getUserByUserName(loginUserRequest.userID)

            return when (user) {
                is DbResponseWrapper.Success -> {
                    if ((user.data?.userName?.equals(loginUserRequest.userName) == true) && user.data.userName.equals(
                            loginUserRequest.userName
                        )
                    ) {
                        DbResponseWrapper.Success(data = user.data)
                    } else {
                        DbResponseWrapper.UserException(Exception("Incorrect username or password"))
                    }
                }

                else -> DbResponseWrapper.UserException(Exception("Something went wrong"))
            }
        } catch (e: Exception) {
            return DbResponseWrapper.ServerException(exception = e)
        }
    }

    override fun registerUser(userRequest: UserRequest): DbResponseWrapper<out Int?> {
        return try {
            userRepository.createUser(userRequest)
        } catch (e: Exception) {
            DbResponseWrapper.ServerException(exception = e)
        }
    }
}