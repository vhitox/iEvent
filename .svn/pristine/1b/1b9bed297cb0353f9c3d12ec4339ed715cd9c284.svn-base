package ievent

class Participants {
    String name
    String surnames
    String country
    String city
    String idCardNumber
    String unity
    String position
    String profession
    String email
    String phoneNumber
    Date registerDate

    static constraints = {
        name nullable: false, maxSize: 300
        surnames nullable: false, maxSize: 300
        idCardNumber nullable: false, maxSize: 20
        country nullable: true, maxSize: 150
        city nullable: true, maxSize: 150
        unity nullable: true, maxSize: 300
        position nullable: true, maxSize: 150
        profession nullable: true, maxSize: 150
        email nullable: false, maxSize: 500, email: true
        phoneNumber nullable: true, maxSize: 20
        registerDate nullable: true
        evet nullable: false
        group nullable: true
    }

    static hasMany = [phase: ParticipantPhases]
    static belongsTo = [evet: Event, group: EventGroup]
}
