package com.lksh.dev.lkshassistant.utils

import com.lksh.dev.lkshassistant.R
import android.view.View
import com.lksh.dev.lkshassistant.R.id.*
import kotlinx.android.synthetic.main.fragment_building_info.view.*


data class RuleItem (val isPositive: Boolean,
                         val text: String){

}

object RulesLkshData {
    var rules = mutableListOf<RuleItem>()
    init {
        rules.add(RuleItem(true, "Ходить на официальные мероприятия"))
        rules.add(RuleItem(true, "Не жечь домики"))
        rules.add(RuleItem(true, "Не ломать имущество ЛКШ и других школьников"))
        rules.add(RuleItem(true, "Пользоваться ноутбуками только с 8:30 и до 18:00, только в будние дни"))
        rules.add(RuleItem(true, "Носить свой бейджик первую неделю"))
        rules.add(RuleItem(true, "Обращаться к врачу или препу при плохом самочуствии"))
        rules.add(RuleItem(false, "Принимать и хранить алкоголь, сигареты или наркотики"))
        rules.add(RuleItem(false, "Купаться"))
        rules.add(RuleItem(false, "Уходить за территорию лагеря"))
        rules.add(RuleItem(false, "Причинять вред себе или окружающим"))
        rules.add(RuleItem(false, "Играть в компьтерные игры"))
        rules.add(RuleItem(false, "Выносить ноутбуки ЛКШ из комповника"))
        rules.add(RuleItem(false, "Носить чужой бейджик"))
        rules.add(RuleItem(false, "Заниматься спортом в неподходящей обуви"))
        rules.add(RuleItem(false, "Заниматься самолечением, вводить родителей в панику не посещая доктора"))
    }
}