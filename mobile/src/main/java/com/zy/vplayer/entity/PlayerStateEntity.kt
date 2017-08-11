package com.zy.vplayer.entity

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.zy.vplayer.widget.LController

class PlayerStateEntity: BaseObservable() {
    private var state:Int = LController.PlayerState.STATE_EMPTY

    fun setState(state:Int){
        this.state = state
        notifyPropertyChanged(state)
    }

    @Bindable
    fun getState():Int{
        return state
    }

}