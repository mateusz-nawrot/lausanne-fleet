package nawrot.mateusz.lausannefleet.domain.base


data class ErrorEvent(val errorMessage: String, val mapError: Boolean = false)