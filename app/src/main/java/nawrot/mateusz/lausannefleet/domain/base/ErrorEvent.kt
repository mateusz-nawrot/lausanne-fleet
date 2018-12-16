package nawrot.mateusz.lausannefleet.domain.base


data class ErrorEvent(val errorMessage: String, val errorCode: Int = -1, val mapError: Boolean = false)