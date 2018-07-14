package com.lksh.dev.lkshassistant.listeners

import com.lksh.dev.lkshassistant.fragments.HouseInfo

interface OnMapInteractionListener {
    fun dispatchClickBuilding(marker: HouseInfo)
}