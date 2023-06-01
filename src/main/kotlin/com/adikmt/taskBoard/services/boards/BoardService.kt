package com.adikmt.taskBoard.services.boards

import com.adikmt.taskBoard.dtos.common.DbResponseWrapper
import com.adikmt.taskBoard.dtos.common.UserRole
import com.adikmt.taskBoard.dtos.requests.BoardRequest
import com.adikmt.taskBoard.dtos.responses.BoardResponse

interface BoardService {

    fun createBoard(boardRequest: BoardRequest, userId: Int): DbResponseWrapper<Int>

    fun addUserToBoard(userId: Int, boardId: Int, role: UserRole): DbResponseWrapper<Boolean>

    fun getBoardById(boardId: Int, userId: Int): DbResponseWrapper<BoardResponse?>

    fun searchBoardByName(boardName: String, userId: Int): DbResponseWrapper<List<BoardResponse>?>

    fun getAllBoardsForUser(userId: Int): DbResponseWrapper<List<BoardResponse>?>
}