package nawrot.mateusz.lausannefleet.domain.car


data class CarEvent(val id: String, val type: ActionType = ActionType.ADD, val car: Car) {

    companion object {
        fun add(car: Car): CarEvent = CarEvent(car.id, ActionType.ADD, car)

        fun move(car: Car): CarEvent = CarEvent(car.id, ActionType.MOVE, car)

        fun remove(car: Car): CarEvent = CarEvent(car.id, ActionType.REMOVE, car)
    }
}

enum class ActionType {
    ADD, MOVE, REMOVE
}