package com.adikmt.taskBoard.controllers

import com.adikmt.taskBoard.dtos.common.wrappers.DbResponseWrapper
import com.adikmt.taskBoard.dtos.requests.BoardRequest
import com.adikmt.taskBoard.dtos.responses.BoardResponse
import com.adikmt.taskBoard.services.boards.BoardService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import reactor.test.StepVerifier

@SpringBootTest
class BoardControllerTest {

    private val boardService = mockk<BoardService>()

    private val boardController = BoardController(boardService)

    private val boardRequest = BoardRequest(title = "Board title")

    private val boardResponse = BoardResponse(boardId = 1, title = "Board title")

    @Test
    fun `create board successfully`() {
        every { boardService.createBoard(boardRequest = boardRequest, userId = 1) } returns (
                DbResponseWrapper.Success(data = 1)
                )
        val response = boardController.createBoard(boardRequest = boardRequest, userId = 1)

        StepVerifier.create(response).consumeNextWith { responseEntity ->
            assert(responseEntity.statusCode == HttpStatus.CREATED)
            assert(responseEntity.body?.data == 1)
        }.verifyComplete()
    }

    @Test
    fun `create board with exception`() {
        every { boardService.createBoard(boardRequest = boardRequest, userId = 1) } returns (
                DbResponseWrapper.DBException(exception = Exception("Exception"))
                )
        val response = boardController.createBoard(boardRequest = boardRequest, userId = 1)

        StepVerifier.create(response).consumeNextWith { responseEntity ->
            assert(responseEntity.statusCode == HttpStatus.INTERNAL_SERVER_ERROR)
            assert(responseEntity.body?.errorMessage == "Exception")
        }.verifyComplete()
    }

    @Test
    fun `Get board successfully`() {
        every { boardService.getBoardById(boardId = 1, userId = 1) } returns (
                DbResponseWrapper.Success(data = boardResponse)
                )
        val response = boardController.getBoardById(id = 1, userId = 1)

        StepVerifier.create(response).consumeNextWith { responseEntity ->
            assert(responseEntity.statusCode == HttpStatus.OK)
            assert(responseEntity.body?.data == boardResponse)
        }.verifyComplete()
    }

    @Test
    fun `Get board with exception`() {
        every { boardService.getBoardById(boardId = 1, userId = 1) } returns (
                DbResponseWrapper.ServerException(exception = Exception("Exception"))
                )
        val response = boardController.getBoardById(id = 1, userId = 1)

        StepVerifier.create(response).consumeNextWith { responseEntity ->
            assert(responseEntity.statusCode == HttpStatus.INTERNAL_SERVER_ERROR)
            assert(responseEntity.body?.errorMessage == "Exception")
        }.verifyComplete()
    }

    @Test
    fun `Get board by user successfully`() {
        every { boardService.getAllBoardsForUser(userId = 1) } returns (
                DbResponseWrapper.Success(data = listOf(boardResponse))
                )
        val response = boardController.getAllBoards(userId = 1)

        StepVerifier.create(response).consumeNextWith { responseEntity ->
            assert(responseEntity.statusCode == HttpStatus.OK)
            assert(responseEntity.body?.data == listOf(boardResponse))
        }.verifyComplete()
    }

    @Test
    fun `Get board by user with exception`() {
        every { boardService.getAllBoardsForUser(userId = 1) } returns (
                DbResponseWrapper.DBException(exception = Exception("Exception"))
                )
        val response = boardController.getAllBoards(userId = 1)

        StepVerifier.create(response).consumeNextWith { responseEntity ->
            assert(responseEntity.statusCode == HttpStatus.INTERNAL_SERVER_ERROR)
            assert(responseEntity.body?.errorMessage == "Exception")
        }.verifyComplete()
    }

    @Test
    fun `search board successfully`() {
        every { boardService.searchBoardByName(userId = 1, boardName = "Board title") } returns (
                DbResponseWrapper.Success(data = listOf(boardResponse))
                )
        val response = boardController.searchBoardByName(boardName = "Board title", userId = 1)

        StepVerifier.create(response).consumeNextWith { responseEntity ->
            assert(responseEntity.statusCode == HttpStatus.OK)
            assert(responseEntity.body?.data == listOf(boardResponse))
        }.verifyComplete()
    }

    @Test
    fun `search board with exception`() {
        every { boardService.searchBoardByName(userId = 1, boardName = "Board title") } returns (
                DbResponseWrapper.DBException(exception = Exception("Exception"))
                )
        val response = boardController.searchBoardByName(boardName = "Board title", userId = 1)

        StepVerifier.create(response).consumeNextWith { responseEntity ->
            assert(responseEntity.statusCode == HttpStatus.INTERNAL_SERVER_ERROR)
            assert(responseEntity.body?.errorMessage == "Exception")
        }.verifyComplete()
    }
}