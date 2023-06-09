package com.adikmt.taskBoard.services.cards

import com.adikmt.taskBoard.dtos.common.UserRole
import com.adikmt.taskBoard.dtos.common.wrappers.DbResponseWrapper
import com.adikmt.taskBoard.dtos.requests.CardRequest
import com.adikmt.taskBoard.dtos.requests.CardUpdateBucketRequest
import com.adikmt.taskBoard.dtos.requests.CardUpdateRequest
import com.adikmt.taskBoard.dtos.requests.CardUpdateUserRequest
import com.adikmt.taskBoard.dtos.responses.CardResponse
import com.adikmt.taskBoard.repositories.boards.BoardRepository
import com.adikmt.taskBoard.repositories.cards.CardRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.time.LocalDateTime

@Service
class CardServiceImpl @Autowired constructor(
    private val cardRepository: CardRepository,
    private val boardRepository: BoardRepository,
    override val updateUserSink: Sinks.Many<CardUpdateUserRequest> = Sinks.many().multicast().directBestEffort(),
    override val updateCardSink: Sinks.Many<CardUpdateBucketRequest> = Sinks.many().multicast().directBestEffort()
) : CardService {
    override fun createCard(cardRequest: CardRequest, userId: Int): DbResponseWrapper<Int> {
        try {
            val userRole = boardRepository.getUserRoleForBoard(userId, cardRequest.boardId)

            return when (userRole) {
                is DbResponseWrapper.Success -> {
                    if (userRole.data?.equals(UserRole.admin) == true) {
                        cardRepository.createCard(cardRequest = cardRequest, userId = userId)
                    } else {
                        DbResponseWrapper.UserException(exception = Exception("Non admins can't add cards to board'"))
                    }
                }

                else -> DbResponseWrapper.UserException(exception = Exception("Something went wrong"))
            }
        } catch (e: Exception) {
            return DbResponseWrapper.ServerException(exception = e)
        }
    }

    override fun getAllCards(
        boardId: Int,
        limit: Int,
        seekAfter: LocalDateTime
    ): List<DbResponseWrapper<CardResponse>> {
        return try {
            cardRepository.getAllCards(boardId = boardId, limit = limit, seekAfter = seekAfter)
        } catch (e: Exception) {
            listOf(
                DbResponseWrapper.ServerException(exception = e)
            )
        }
    }

    override fun getAllCardsAssignedToUserById(
        userId: Int,
        boardId: Int,
        limit: Int,
        seekAfter: LocalDateTime
    ): List<DbResponseWrapper<CardResponse>> {
        return try {
            cardRepository.getAllCardsAssignedToUserById(
                userId = userId,
                boardId = boardId,
                limit = limit,
                seekAfter = seekAfter
            )
        } catch (e: Exception) {
            listOf(
                DbResponseWrapper.ServerException(exception = e)
            )
        }
    }

    override fun updateCardDetails(cardRequest: CardUpdateRequest, userId: Int): DbResponseWrapper<Boolean> {
        return try {
            cardRepository.updateCardDetails(cardRequest)
        } catch (e: Exception) {
            DbResponseWrapper.ServerException(exception = e)
        }
    }

    override fun updateCardBucket(
        cardUpdateBucketRequest: CardUpdateBucketRequest,
        userId: Int
    ): DbResponseWrapper<Boolean> {
        return try {
            updateCardSink.tryEmitNext(cardUpdateBucketRequest)
            cardRepository.updateCardBucket(cardUpdateBucketRequest)
        } catch (e: Exception) {
            DbResponseWrapper.ServerException(exception = e)
        }
    }

    override fun assignCardToAnotherUser(
        cardUpdateUserRequest: CardUpdateUserRequest,
        userId: Int
    ): DbResponseWrapper<Boolean> {
        return try {
            updateUserSink.tryEmitNext(cardUpdateUserRequest)
            cardRepository.assignCardToAnotherUser(cardUpdateUserRequest)
        } catch (e: Exception) {
            DbResponseWrapper.ServerException(exception = e)
        }
    }
}