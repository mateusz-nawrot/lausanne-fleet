package nawrot.mateusz.lausannefleet.domain.car


data class CarAction(val id: Int, val actionType: ActionType = ActionType.ADD, val car: Car) {

    companion object {
        fun add(car: Car): CarAction = CarAction(car.id, ActionType.ADD, car)

        fun move(car: Car): CarAction = CarAction(car.id, ActionType.MOVE, car)

        fun remove(car: Car): CarAction = CarAction(car.id, ActionType.REMOVE, car)
    }
}

enum class ActionType {
    ADD, MOVE, REMOVE
}