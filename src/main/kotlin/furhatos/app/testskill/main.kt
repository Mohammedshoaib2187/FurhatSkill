package furhatos.app.testskill

import furhatos.app.testskill.flow.Init
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill

class TestskillSkill : Skill() {
    override fun start() {
        Flow().run(Init)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
