package ievent

class EventPhase {

    Integer orderPhase
    Date datePhase

    static constraints = {
        orderPhase nullable: true
        datePhase nullable: true
    }

    static belongsTo = [event: Event, phase:Phase]
    static hasMany = [participant: ParticipantPhases]
}
