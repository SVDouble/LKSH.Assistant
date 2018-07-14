package com.lksh.dev.lkshassistant.listeners

import com.lksh.dev.lkshassistant.model.HouseInfo

interface OnMapInteractionListener {
    fun dispatchClickBuilding(marker: HouseInfo)
}