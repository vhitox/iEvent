package ievent



import org.junit.*
import grails.test.mixin.*

@TestFor(PhaseController)
@Mock(Phase)
class PhaseControllerTests {


    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/phase/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.phaseInstanceList.size() == 0
        assert model.phaseInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.phaseInstance != null
    }

    void testSave() {
        controller.save()

        assert model.phaseInstance != null
        assert view == '/phase/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/phase/show/1'
        assert controller.flash.message != null
        assert Phase.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/phase/list'


        populateValidParams(params)
        def phase = new Phase(params)

        assert phase.save() != null

        params.id = phase.id

        def model = controller.show()

        assert model.phaseInstance == phase
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/phase/list'


        populateValidParams(params)
        def phase = new Phase(params)

        assert phase.save() != null

        params.id = phase.id

        def model = controller.edit()

        assert model.phaseInstance == phase
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/phase/list'

        response.reset()


        populateValidParams(params)
        def phase = new Phase(params)

        assert phase.save() != null

        // test invalid parameters in update
        params.id = phase.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/phase/edit"
        assert model.phaseInstance != null

        phase.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/phase/show/$phase.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        phase.clearErrors()

        populateValidParams(params)
        params.id = phase.id
        params.version = -1
        controller.update()

        assert view == "/phase/edit"
        assert model.phaseInstance != null
        assert model.phaseInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/phase/list'

        response.reset()

        populateValidParams(params)
        def phase = new Phase(params)

        assert phase.save() != null
        assert Phase.count() == 1

        params.id = phase.id

        controller.delete()

        assert Phase.count() == 0
        assert Phase.get(phase.id) == null
        assert response.redirectedUrl == '/phase/list'
    }
}
