package com.ke.hs_tracker.module.entity

sealed interface InsertStackResult {


    /**
     * 插入成功
     */
    object Success : InsertStackResult

    /**
     * 不能插入，例如不在Block内的TAG_CHANGE
     */
    object CanNotInsert : InsertStackResult

    /**
     * 结束了
     * @param powerTag tag
     * @param handled 是否处理了本次log日志，例如 FULL_ENTITY - Updating 跟着一个 FULL_ENTITY - Updating的情况，handled就是true，表示已经处理了，不需要在进行处理；如果是FULL_ENTITY - Updating
     * 跟着一个 TAG_CHANGE，就表示没有处理，需要调用这个方法的自行处理log数据
     */
    data class Over(val powerTag: PowerTag, val handled: Boolean) : InsertStackResult
}