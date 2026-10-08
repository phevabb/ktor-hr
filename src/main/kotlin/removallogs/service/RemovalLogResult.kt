package com.hr.removallogs.service

sealed interface RemovalLogResult {

    data object Success :
        RemovalLogResult

    data object AccountNotFound :
        RemovalLogResult

    data object ActorAccountNotFound :
        RemovalLogResult

    data object AlreadyInactive :
        RemovalLogResult

    data object UpdateFailed :
        RemovalLogResult

    data object CannotRemoveOwnAccount :
        RemovalLogResult
}