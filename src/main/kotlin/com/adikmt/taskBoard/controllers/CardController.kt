package com.adikmt.taskBoard.controllers

import com.adikmt.taskBoard.dtos.common.wrappers.ResponseStatus
import com.adikmt.taskBoard.dtos.common.wrappers.ResponseWrapper
import com.adikmt.taskBoard.dtos.common.wrappers.unwrap
import com.adikmt.taskBoard.dtos.requests.CardRequest
import com.adikmt.taskBoard.dtos.requests.CardUpdateBucketRequest
import com.adikmt.taskBoard.dtos.requests.CardUpdateRequest
import com.adikmt.taskBoard.dtos.requests.CardUpdateUserRequest
import com.adikmt.taskBoard.dtos.responses.CardResponse
import com.adikmt.taskBoard.services.cards.CardService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/cards")
class CardController @Autowired constructor(private val cardService: CardService) {

    @PostMapping
    fun createCard(
        @Valid @RequestBody cardRequest: CardRequest,
        @RequestParam userId: Int
    ): ResponseEntity<ResponseWrapper<Int>> {
        return try {
            cardService.createCard(cardRequest = cardRequest, userId = userId)
                .unwrap(successResponseStatus = ResponseStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity(ResponseWrapper(errorMessage = e.message), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/{boardId}")
    fun getAllCards(
        @PathVariable boardId: Int,
        @RequestParam(required = false) userId: Int? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam seekAfter: LocalDateTime
    ): List<ResponseEntity<ResponseWrapper<CardResponse>>> {
        try {
            val headers = HttpHeaders().also {
                it.add(HttpHeaders.CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
            }
            return if (userId != null) {
                cardService.getAllCardsAssignedToUserById(
                    userId = userId,
                    boardId = boardId,
                    limit = limit,
                    seekAfter = seekAfter
                ).map {
                    it.unwrap(header = headers)
                }
            } else {
                cardService.getAllCards(boardId = boardId, limit = limit, seekAfter = seekAfter)
                    .map {
                        it.unwrap(header = headers)
                    }
            }
        } catch (e: Exception) {
            return listOf(
                ResponseEntity(ResponseWrapper(errorMessage = e.message), HttpStatus.INTERNAL_SERVER_ERROR)
            )
        }
    }

    @PutMapping("/update")
    fun updateCard(
        @Valid @RequestBody cardUpdateRequest: CardUpdateRequest,
        @RequestParam userId: Int
    ): ResponseEntity<ResponseWrapper<Boolean>> {
        return try {
            cardService.updateCardDetails(
                cardRequest = cardUpdateRequest
            ).unwrap()
        } catch (e: Exception) {
            ResponseEntity(ResponseWrapper(errorMessage = e.message), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PatchMapping("/updateBucket")
    fun updateCardBucket(
        @Valid @RequestBody cardUpdateBucketRequest: CardUpdateBucketRequest,
        @RequestParam userId: Int
    ): ResponseEntity<ResponseWrapper<Boolean>> {
        return try {
            cardService.updateCardBucket(
                cardUpdateBucketRequest = cardUpdateBucketRequest
            ).unwrap()
        } catch (e: Exception) {
            ResponseEntity(ResponseWrapper(errorMessage = e.message), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PatchMapping("/updateUser")
    fun assignCardToAnotherUser(
        @Valid @RequestBody cardUpdateUserRequest: CardUpdateUserRequest,
        @RequestParam userId: Int
    ): ResponseEntity<ResponseWrapper<Boolean>> {
        return try {
            cardService.assignCardToAnotherUser(
                cardUpdateUserRequest = cardUpdateUserRequest
            ).unwrap()
        } catch (e: Exception) {
            ResponseEntity(ResponseWrapper(errorMessage = e.message), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}