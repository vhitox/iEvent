package ievent



import org.junit.*
import grails.test.mixin.*

@TestFor(EventPhaseController)
@Mock(EventPhase)
class EventPhaseControllerTests {


    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/eventPhase/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.eventPhaseInstanceList.size() == 0
        assert model.eventPhaseInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.eventPhaseInstance != null
    }

    void testSave() {
        controller.save()

        assert model.eventPhaseInstance != null
        assert view == '/eventPhase/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/eventPhase/show/1'
        assert controller.flash.message != null
        assert EventPhase.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/eventPhase/list'


        populateValidParams(params)
        def eventPhase = new EventPhase(params)

        assert eventPhase.save() != null

        params.id = eventPhase.id

        def model = controller.show()

        assert model.eventPhaseInstance == eventPhase
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/eventPhase/list'


        populateValidParams(params)
        def eventPhase = new EventPhase(params)

        assert eventPhase.save() != null

        params.id = eventPhase.id

        def model = controller.edit()

        assert model.eventPhaseInstance == eventPhase
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/eventPhase/list'

        response.reset()


        populateValidParams(params)
        def eventPhase = new EventPhase(params)

        assert eventPhase.save() != null

        // test invalid parameters in update
        params.id = eventPhase.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/eventPhase/edit"
        assert model.eventPhaseInstance != null

        eventPhase.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/eventPhase/show/$eventPhase.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        eventPhase.clearErrors()

        populateValidParams(params)
        params.id = eventPhase.id
        params.version = -1
        controller.update()

        assert view == "/eventPhase/edit"
        assert model.eventPhaseInstance != null
        assert model.eventPhaseInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/eventPhase/list'

        response.reset()

        populateValidParams(params)
        def eventPhase = new EventPhase(params)

        assert eventPhase.save() != null
        assert EventPhase.count() == 1

        params.id = eventPhase.id

        controller.delete()

        assert EventPhase.count() == 0
        assert EventPhase.get(eventPhase.id) == null
        assert response.redirectedUrl == '/eventPhase/list'
    }
}
