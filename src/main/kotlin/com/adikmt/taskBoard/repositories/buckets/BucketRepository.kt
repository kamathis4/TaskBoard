package com.adikmt.taskBoard.repositories.buckets

import com.adikmt.taskBoard.dtos.common.wrappers.DbResponseWrapper
import com.adikmt.taskBoard.dtos.requests.BucketRequest
import com.adikmt.taskBoard.dtos.responses.BucketResponse

interface BucketRepository {
    fun createBucket(bucketRequest: BucketRequest): DbResponseWrapper<Int>
    fun getAllBucketsForBoardId(boardId: Int): DbResponseWrapper<List<BucketResponse>>
}
