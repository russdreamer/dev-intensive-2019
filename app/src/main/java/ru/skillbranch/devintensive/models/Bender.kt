package ru.skillbranch.devintensive.models

class Bender (var status: Status = Status.NORMAL, var question: Question = Question.NAME){

    fun askQuestion():String = question.question

    fun listenAnswer(answer:String):Pair<String, Triple<Int, Int, Int>>{
        return when(question){
            Question.IDLE -> question.question to status.color
            else -> "${checkAnswer(answer)}\n${question.question}" to status.color
        }
    }

    private fun checkAnswer(answer: String): String {
        return if (question.answer.contains(answer)) {
            question = question.nextQuestion()
            "Отлично - ты справился"
        }
        else {
            if (status == Status.CRITICAL){
                resetStates()
                "Это неправильный ответ. Давай все по новой"
            }
            else{
                status = status.nextStatus()
                "Это неправильный ответ"
            }
        }
    }

    private fun resetStates() {
        status = Status.NORMAL
        question = Question.NAME
    }

    enum class Status(val color: Triple<Int, Int, Int>){
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status{
            return if (this.ordinal < values().lastIndex)
                values()[this.ordinal + 1]
            else values()[0]
        }
    }

    enum class Question(val question: String, val answer: List<String>){
        NAME("Как меня зовут?", listOf("бендер", "bender")) {
            override fun nextQuestion(): Question = PROFESSION
            override fun validate(answer: String): Boolean = answer.trim().firstOrNull()?.isUpperCase() ?: false
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")){
            override fun nextQuestion(): Question = MATERIAL
            override fun validate(answer: String): Boolean = answer.trim().firstOrNull()?.isLowerCase() ?: false
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "iron", "wood", "metal")){
            override fun nextQuestion(): Question = BDAY
            override fun validate(answer: String): Boolean = answer.trim().contains(Regex("\\d")).not()
        },
        BDAY("Когда меня создали?", listOf("2993")){
            override fun nextQuestion(): Question = SERIAL
            override fun validate(answer: String): Boolean = answer.trim().contains(Regex("^[0-9]*$"))
        },
        SERIAL("Мой серийный номер?", listOf("2716057")){
            override fun nextQuestion(): Question = IDLE
            override fun validate(answer: String): Boolean = answer.trim().contains(Regex("^[0-9]{7}$"))
        },
        IDLE("На этом все, вопросов больше нет", listOf()){
            override fun nextQuestion(): Question = IDLE
            override fun validate(answer: String): Boolean = true
        };

        abstract fun nextQuestion():Question
        abstract fun validate(answer: String):Boolean
    }
}