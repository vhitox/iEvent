package ievent

class Wildcard {
    String codeWildcard
    Integer counterWildcard
    static constraints = {
        codeWildcard maxSize: 250
        counterWildcard max: 500
    }
    static belongsTo = [event: Event]
}
