package ievent

class EventGroup {
    String nameGroup

    static constraints = {
        nameGroup maxSize: 1000
    }
    static hasMany = [participant: Participants]
    static belongsTo = [event: Event]
}
