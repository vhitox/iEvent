package ievent

class ParticipantPhases {

    Boolean checkPhase
    Date checkDatePhase

    static constraints = {
        checkPhase nullable: false
        checkDatePhase nullable: true
    }

    static belongsTo = [participant: Participants, phase: EventPhase]
}
