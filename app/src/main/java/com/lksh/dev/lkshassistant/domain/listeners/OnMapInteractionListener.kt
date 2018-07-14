package com.lksh.dev.lkshassistant.domain.listeners

import com.lksh.dev.lkshassistant.domain.model.HouseInfo

interface OnMapInteractionListener {
    fun dispatchClickBuilding(marker: HouseInfo)
}