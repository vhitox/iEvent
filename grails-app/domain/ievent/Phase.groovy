package ievent

class Phase {
    String phaseName
    byte[] imagePhase
    String imageName

    String toString(){
        return phaseName
    }

    static constraints = {
        phaseName nullable: false, maxSize: 500
        imagePhase blank:true, nullable: true, maxSize: 1073741824
        imageName nullable: true, maxSize: 500
    }

    static hasMany = [event: EventPhase]
}
