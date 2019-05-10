package ievent

class Event {
    transient springSecurityService

    String eventName
    String description
    String eventPlace
    Date startDate
    Date endDate
    Integer phaseOrder
    String type
    Date limitDate
    Boolean limitDateControl
    Integer limitParticipants
    Boolean limitParticipantsControl
    String passwordEvent
    Boolean passwordControl

    String toString(){
        return eventName
    }

    static constraints = {
        eventName nullable: false, maxSize: 500
        description nullable: true, maxSize: 3000
        eventPlace nullable: true, maxSize: 500
        startDate nullable: false
        endDate nullable: true
        limitDate nullable: true
        limitDateControl nullable: true
        limitParticipants nullable: true, max: 5000
        limitParticipantsControl nullable: true
        phase nullable: true
        phaseOrder nullable: true
        type nullable: true, maxSize: 500, inList: ["Conferencia", "Seminario", "Feria", "Congreso","Taller"]
        wildcard nullable: true
        passwordEvent nullable: true
        passwordControl nullable: true
    }

    static hasMany = [phase: EventPhase, participants: Participants, groups: EventGroup]
    static hasOne = [wildcard: Wildcard]

    def beforeInsert() {
        encodePassword()
        limitDateControl = false
        limitParticipantsControl = false
        passwordControl = false
    }

    def beforeUpdate() {
        if (isDirty('passwordEvent')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        passwordEvent = springSecurityService.encodePassword(passwordEvent)
    }
}
