package nawrot.mateusz.lausannefleet.domain.map


sealed class MapStatus(val statusCode: Int)

class MapOk(statusCode: Int): MapStatus(statusCode)
class MapError(statusCode: Int): MapStatus(statusCode)
