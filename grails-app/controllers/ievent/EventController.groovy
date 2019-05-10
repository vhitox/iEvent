package ievent

import org.springframework.dao.DataIntegrityViolationException

import grails.plugins.springsecurity.Secured

import static java.util.UUID.randomUUID
/*Import para la generacion de PDFs*/
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Header;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.Document;

import com.itextpdf.text.PageSize
import com.itextpdf.text.FontFactory
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.Chunk
import java.awt.Color
import com.itextpdf.text.pdf.BarcodeQRCode
import java.text.SimpleDateFormat

@Secured("ROLE_SUPERADMIN")
class EventController {

    def springSecurityService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def mailService

    def index() {
        redirect(action: "list", params: params)
    }

    @Secured(["ROLE_SUPERADMIN","ROLE_ADMIN","ROLE_OPERATOR"])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [eventInstanceList: Event.list(params), eventInstanceTotal: Event.count()]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def create() {
        [eventInstance: new Event(params)]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def save() {
        def eventInstance = new Event(params)
        if (!eventInstance.save(flush: true)) {
            render(view: "create", model: [eventInstance: eventInstance])
            return
        }
        def wildcardInstance = new Wildcard(event: eventInstance, codeWildcard: eventInstance.id+"-"+eventInstance.startDate.format("ddMMyyyy"),counterWildcard: 0)
        wildcardInstance.save()
        flash.message = message(code: 'default.created.message', args: [message(code: 'event.label', default: 'Event'), eventInstance.id])
        redirect(action: "show", id: eventInstance.id)
    }

    @Secured(["ROLE_ADMIN","ROLE_OPERATOR","ROLE_SUPERADMIN"])
    def show() {
        def eventInstance = Event.get(params.id)
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
            return
        }

        [eventInstance: eventInstance]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def edit() {
        def eventInstance = Event.get(params.id)
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
            return
        }

        [eventInstance: eventInstance]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def update() {
        def eventInstance = Event.get(params.id)
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (eventInstance.version > version) {
                eventInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'event.label', default: 'Event')] as Object[],
                        "Another user has updated this Event while you were editing")
                render(view: "edit", model: [eventInstance: eventInstance])
                return
            }
        }

        eventInstance.properties = params

        if (!eventInstance.save(flush: true)) {
            render(view: "edit", model: [eventInstance: eventInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'event.label', default: 'Event'), eventInstance.id])
        redirect(action: "show", id: eventInstance.id)
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def delete() {
        def eventInstance = Event.get(params.id)
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
            return
        }

        try {
            eventInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def inscription(){
        def eventInstance = Event.read(params.id)
        if (springSecurityService.principal == "anonymousUser"){
            println(eventInstance.limitDateControl)

            if (eventInstance.limitDateControl == true){
                if (new Date() > eventInstance.limitDate){
                    redirect(action: "expiredInscription", id: eventInstance.id)
                    return
                }
            }

            if (eventInstance?.limitParticipantsControl == true){
                println(eventInstance.participants.count {it.id})
                if (eventInstance.limitParticipants.toInteger() <= eventInstance.participants.count {it.id}){
                    println("true")
                    redirect(action: "exceededRegistrations", id: eventInstance.id)
                    return
                }
            }

            if (eventInstance?.passwordControl == true){
                if (params.p){
                    if (springSecurityService.encodePassword(params.p) != eventInstance.passwordEvent){
                        redirect(action: "wrongPassword", id: eventInstance.id)
                        return
                    }
                }else{
                    redirect(action: "passwordControl", id: eventInstance.id)
                    return
                }
            }
        }

        def participantInstance =  new Participants()
        [participantInstance: participantInstance, eventInstance: eventInstance]
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def expiredInscription(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def exceededRegistrations(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def passwordControl(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def wrongPassword(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def duplicatedRegister(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance, name: params.name, email: params.email]
    }



    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def saveInscription(){
        def eventInstance = Event.read(params.idEvent)
        def participantInscane = new Participants(params)
        withForm {
            participantInscane.evet = eventInstance
            participantInscane.registerDate = new Date()
            if (participantInscane.idCardNumber in eventInstance.participants.idCardNumber || participantInscane.email in eventInstance.participants.email){
                println("Usuario Ya registrado")
                redirect(action: "duplicatedRegister", id: eventInstance.id, params: [name: participantInscane.name+" "+participantInscane.surnames,email: participantInscane.email])
                return
            }

            def name = participantInscane.name.trim()
            def nameUpCase = name.substring(0,1).toUpperCase()+name.substring(1)
            participantInscane.name = nameUpCase

            def surname = participantInscane.surnames.trim()
            def surnameUpCase = surname.substring(0,1).toUpperCase()+surname.substring(1)
            participantInscane.surnames = surnameUpCase

            if (!participantInscane.save(flush: true)) {
                render(view: "create", model: [eventInstance: participantInscane])
                return
            }
            eventInstance.phase.each {
                def participantPhase = new ParticipantPhases(participant: participantInscane, phase: it, checkPhase: false, checkDatePhase: null)
                participantPhase.save(flush: true)
            }
            flash.message = message(code: 'default.created.message', args: [message(code: 'event.label', default: 'Event'), participantInscane.id])
            redirect(action: "createPdf", id: participantInscane.id)
        }.invalidToken{
            flash.message = "Su inscripción ha sido realizada, por favor verifique la llegada del correo de confirmación"
            redirect(action: "inscription", id: eventInstance.id)
        }

    }

    def successfullRegister(){
        def participantInstance = Participants.read(params.id)
        [participantInstance: participantInstance]
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def createPdf(){
        def participantInstance = Participants.read(params.id)
        def filename = (randomUUID() as String)+".pdf"

        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename
        Document document = new Document(PageSize.LETTER,0,0,38,0)

        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"
        
        println(fontPath)

        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)

        BaseColor subtitleBackColor = new BaseColor(91,155,213)
        BaseColor subtitleTextColor = new BaseColor(255,255,255)

        Font titleFont = new Font(cambria, 16, Font.BOLD)
        Font subTitleFont = new Font(cambria, 14, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font normalFont = new Font(cambria,8, Font.NORMAL)
        Font normalFontBold = new Font(cambria,8, Font.BOLD)

        try{
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))

            document.setMargins(20, 20, 70, 20)
            document.open()

            PdfContentByte cb = pdfWriter.getDirectContent()
            Barcode128 code128 = new Barcode128();
            def codeParticipant = participantInstance.id+"-"+participantInstance.name.trim().take(1)
            code128.setCode(codeParticipant)
            Image barCodeImage = code128.createImageWithBarcode(cb, null, null)

            println(participantInstance.id+"-"+participantInstance.idCardNumber)
            def pathUmsaImage = createLinkTo(dir: "images", file: "umsa_logo.png",absolute: true)
            Image logoUmsa = Image.getInstance(new URL(pathUmsaImage.toString()))
            def pathDipgisImage = createLinkTo(dir: "images", file: "logodipgis.png",absolute: true)
            Image logoDipgis = Image.getInstance(new URL(pathDipgisImage.toString()))

            PdfPTable tableHeader = new PdfPTable(3)
            tableHeader.setSpacingBefore(100f)
            float[] columnHeader = [40f,460f,50f]
            PdfPCell headerLeft = new PdfPCell(logoUmsa,true)
            headerLeft.setPaddingBottom(10f)
            headerLeft.setBorder(Rectangle.BOTTOM)
            PdfPCell headerCenter = new PdfPCell(new Phrase("UNIVERSIDAD MAYOR DE SAN ANDRÉS \n DEPARTAMENTO DE INVESTIGACIÓN POSTGRADO E INTERACCIÓN SOCIAL",titleFont))
            headerCenter.setBorder(Rectangle.BOTTOM)
            headerCenter.setHorizontalAlignment(1)
            PdfPCell headerRight = new PdfPCell(logoDipgis,true)
            headerRight.setBorder(Rectangle.BOTTOM)
            tableHeader.setWidths(columnHeader)
            tableHeader.addCell(headerLeft)
            tableHeader.addCell(headerCenter)
            tableHeader.addCell(headerRight)

            PdfPTable tableCodeBar = new PdfPTable(3)
            float[] columTres = [200f,150f,200f]
            tableCodeBar.setWidths(columTres)
            tableCodeBar.getDefaultCell().setBorder(0)
            tableCodeBar.setSpacingBefore(50f)
            PdfPCell barCodeContainer = new PdfPCell(barCodeImage)
            barCodeContainer.setBorder(1)
            barCodeContainer.setHorizontalAlignment(1)
            barCodeContainer.setVerticalAlignment(1)
            barCodeContainer.setPaddingTop(10f)
            barCodeContainer.setPaddingBottom(10f)
            tableCodeBar.addCell(new Phrase(""))
            tableCodeBar.addCell(barCodeContainer)
            tableCodeBar.addCell(new Phrase(""))

            PdfPTable tableContent = new PdfPTable(1)
            tableContent.getDefaultCell().setBorder(2)
            tableContent.setSpacingBefore(20f)

            PdfPCell contentTitle = new PdfPCell(new Phrase(message(code: 'default.successfull.message', default: 'Successfull Register'), titleFont))
            contentTitle.setHorizontalAlignment(1)
            contentTitle.setBorder(0)
            PdfPCell eventTitle = new PdfPCell(new Phrase(participantInstance.evet.eventName, titleFont))
            eventTitle.setHorizontalAlignment(1)
            eventTitle.setBorder(0)

            tableContent.addCell(contentTitle)
            tableContent.addCell(eventTitle)


            PdfPTable personalInformation = new PdfPTable(2)
            personalInformation.getDefaultCell().setBorder(1)
            personalInformation.setSpacingBefore(20f)

            PdfPCell titlePersonal = new PdfPCell(new Phrase(message(code: 'personal.information.label',default: 'Personal Information'),subTitleFont))
            titlePersonal.setBackgroundColor(subtitleBackColor)
            titlePersonal.setBorder(0)
            titlePersonal.setColspan(2)

            PdfPCell emptyCell = new PdfPCell(new Phrase(""))
            emptyCell.setBorder(0)


            PdfPCell titleNames = new PdfPCell(new Phrase(message(code: 'default.name.label')+": ",normalFontBold))
            titleNames.setBorder(0)
            PdfPCell names = new PdfPCell(new Phrase(participantInstance.name,normalFont))
            names.setBorder(0)
            PdfPCell titleSurnames = new PdfPCell(new Phrase(message(code: 'default.surnames.label')+": ",normalFontBold))
            titleSurnames.setBorder(0)
            PdfPCell surnames = new PdfPCell(new Phrase(participantInstance.surnames,normalFont))
            surnames.setBorder(0)
            PdfPCell titleId = new PdfPCell(new Phrase(message(code: 'default.idNumber.label')+": ",normalFontBold))
            titleId.setBorder(0)
            PdfPCell idNumber = new PdfPCell(new Phrase(participantInstance.idCardNumber,normalFont))
            idNumber.setBorder(0)
            PdfPCell titleCountry = new PdfPCell(new Phrase(message(code: 'default.country.label')+": ",normalFontBold))
            titleCountry.setBorder(0)
            PdfPCell country = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.country),normalFont))
            country.setBorder(0)
            PdfPCell titleCity = new PdfPCell(new Phrase(message(code: 'default.city.label')+": ",normalFontBold))
            titleCity.setBorder(0)
            PdfPCell city = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.city),normalFont))
            city.setBorder(0)


            personalInformation.addCell(titlePersonal)
            personalInformation.addCell(titleNames)
            personalInformation.addCell(names)
            personalInformation.addCell(titleSurnames)
            personalInformation.addCell(surnames)
            personalInformation.addCell(titleId)
            personalInformation.addCell(idNumber)
            personalInformation.addCell(titleCountry)
            personalInformation.addCell(country)
            personalInformation.addCell(titleCity)
            personalInformation.addCell(city)

            PdfPTable contactInformation = new PdfPTable(2)
            contactInformation.setSpacingBefore(20f)
            PdfPCell contactTitle = new PdfPCell(new Phrase(message(code: 'contact.information.label'),subTitleFont))
            contactTitle.setBackgroundColor(subtitleBackColor)
            contactTitle.setColspan(2)
            contactTitle.setBorder(0)
            PdfPCell titlePhone = new PdfPCell(new Phrase(message(code: 'default.phoneNumber.label')+": ",normalFontBold))
            titlePhone.setBorder(0)
            PdfPCell phone = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.phoneNumber),normalFont))
            phone.setBorder(0)
            PdfPCell titleMail = new PdfPCell(new Phrase(message(code: 'default.email.label')+": ",normalFontBold))
            titleMail.setBorder(0)
            PdfPCell email = new PdfPCell(new Phrase(participantInstance.email,normalFont))
            email.setBorder(0)

            contactInformation.addCell(contactTitle)
            contactInformation.addCell(titlePhone)
            contactInformation.addCell(phone)
            contactInformation.addCell(titleMail)
            contactInformation.addCell(email)

            PdfPTable professionInformation = new PdfPTable(2)
            professionInformation.setSpacingBefore(20f)

            PdfPCell professionalTitle = new PdfPCell(new Phrase(message(code: 'professional.information.label'),subTitleFont))
            professionalTitle.setBackgroundColor(subtitleBackColor)
            professionalTitle.setColspan(2)
            professionalTitle.setBorder(0)
            PdfPCell titleInstitution = new PdfPCell(new Phrase(message(code: 'default.unity.label')+": ",normalFontBold))
            titleInstitution.setBorder(0)
            PdfPCell institution = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.unity),normalFont))
            institution.setBorder(0)
            PdfPCell titlePosition = new PdfPCell(new Phrase(message(code: 'default.position.label')+": ",normalFontBold))
            titlePosition.setBorder(0)
            PdfPCell position = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.position),normalFont))
            position.setBorder(0)
            PdfPCell titleProfession = new PdfPCell(new Phrase(message(code: 'default.profession.label')+": ",normalFontBold))
            titleProfession.setBorder(0)
            PdfPCell profession = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.profession),normalFont))
            profession.setBorder(0)

            professionInformation.addCell(professionalTitle)
            professionInformation.addCell(titleInstitution)
            professionInformation.addCell(institution)
            professionInformation.addCell(titlePosition)
            professionInformation.addCell(position)
            professionInformation.addCell(titleProfession)
            professionInformation.addCell(profession)

            PdfPTable importantNote = new PdfPTable(1)
            importantNote.setSpacingBefore(50f)
            PdfPCell titleNote = new PdfPCell(new Phrase(message(code: 'important.note.label'),subTitleFont))
            titleNote.setBorder(0)
            PdfPCell contentNote
            if (participantInstance.evet.endDate != null){
                contentNote = new PdfPCell(new Phrase(message(code: 'important.note.content', args: [participantInstance.evet.eventName,participantInstance.evet.startDate.format('dd/MM/yyyy'),participantInstance.evet.endDate.format('dd/MM/yyyy'),participantInstance.evet.eventPlace]),normalFont))
            }else{
                contentNote = new PdfPCell(new Phrase(message(code: 'important.note.content.withoutEndDate', args: [participantInstance.evet.eventName,participantInstance.evet.startDate.format('dd/MM/yyyy'),participantInstance.evet.eventPlace]),normalFont))
            }

            contentNote.setBorder(0)
            PdfPCell dateInscription = new PdfPCell(new Phrase(message(code: 'default.dateInscription.label')+": "+participantInstance.registerDate.format('dd/MM/yyyy'),normalFont))
            dateInscription.setHorizontalAlignment(2)
            dateInscription.setBorder(0)
            importantNote.addCell(titleNote)
            importantNote.addCell(contentNote)
            importantNote.addCell(dateInscription)

            PdfPTable footer = new PdfPTable(1)
            footer.setSpacingBefore(50f)
            PdfPCell dipgis = new PdfPCell(new Phrase(message(code: 'dipgis'),normalFont))
            dipgis.setBorder(0)
            dipgis.setHorizontalAlignment(1)
            PdfPCell address = new PdfPCell(new Phrase(message(code: 'address'),normalFont))
            address.setBorder(0)
            address.setHorizontalAlignment(1)
            PdfPCell phoneNumberAndMail = new PdfPCell(new Phrase(message(code: 'phoneDipgis'),normalFont))
            phoneNumberAndMail.setBorder(0)
            phoneNumberAndMail.setHorizontalAlignment(1)
            footer.addCell(dipgis)
            footer.addCell(address)
            footer.addCell(phoneNumberAndMail)

            document.add(tableHeader)
            document.add(tableContent)
            document.add(personalInformation)
            document.add(contactInformation)
            document.add(professionInformation)
            document.add(tableCodeBar)
            document.add(importantNote)
            document.add(footer)
            document.close()
        }catch (Exception e){
            e.printStackTrace();
        }

        def message = "<div style=\"width: 100%; padding: 20px 100px; background-color: #F1F1F1; font-family: Arial\">\n" +
                "\t<div style=\"width: 520px; background-color: #FFFFFF;\">\n" +
                "\t<h2 style=\"width: 500px; background-color: #B98C16; height: 50px; line-height: 50px; padding-left: 20px; color: #FFFFFF;\">DIPGIS</h2>\n" +
                "\t<p style=\"padding: 0px 20px;\">Estimado(a) ${participantInstance.name},</p>\n" +
                "\t<p style=\"padding: 0px 20px;\">Hemos recibido satisfactoriamente su inscripción al evento \"${participantInstance.evet.eventName}\" y hemos generado el respectivo documento de inscripción.</p>\n" +
                "\t<p style=\"padding: 0px 20px;\">Por favor no olvide llevar su <span style=\"font-weight:bold;\">inscripcion impresa</span> el día indicado, para así facilitar su ingreso.</p>\n" +
                "\t<p style=\"padding: 0px 20px;\">Apreciamos su interés y participación.</p>\n" +
                "\t<p style=\"padding: 0px 0px 20px 20px;\">Gracias.</p>\t\n" +
                "\t</div>\t\n" +
                "</div>"
        def sendmail = {
            mailService.sendMail {
                multipart true
                to participantInstance.email
                subject "Registro Exitoso"
                html message
                attachBytes "documento.pdf", "application/pdf",new File(servletContext.getRealPath('images')+"/pdf_files/"+filename).readBytes()
            }
        }
        render sendmail
        redirect( url: createLinkTo(dir: "images/pdf_files", file: filename, absolute: true))
    }

    def participantsList(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def controlParticipantList(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def checkPhase(){
        def phaseParticipantInstance = ParticipantPhases.get(params.id)
        phaseParticipantInstance.checkPhase = true
        return
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def unCheckPhase(){
        def phaseParticipantInstance = ParticipantPhases.get(params.id)
        phaseParticipantInstance.checkPhase = false
        return
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def participantsControl(){
        def eventInstance = Event.read(params.id)
        if (eventInstance.phase.count {it.id}<0){
            flash.message = "Debe asignar etapas al evento"
            redirect(controller: "eventPhase", action: "create",id: eventInstance.id)
            return
        }
        def currentPhase = null
        if (params.currentPhase){
            currentPhase = EventPhase.read(params.currentPhase)
        }

        def activeParticipants = ParticipantPhases.where {
            phase == currentPhase
        }
        activeParticipants.each {
            println(it.participant.name)
        }
        [eventInstance: eventInstance, currentPhase: currentPhase, participantsPhases: activeParticipants]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def checkBarCode(){
        def eventInstance = Event.get(params.eventId)
        def capture = params.dataCapture.toString()        
        if (capture.contains("'")){
            capture = capture.replaceAll("'","-").toString()
        }
        if (capture == eventInstance.wildcard.codeWildcard){
            eventInstance.wildcard.counterWildcard = eventInstance.wildcard.counterWildcard + 1
            flash.message = "Registro Comodín"
            redirect(controller: "event", action: "participantsControl", id: eventInstance.id, params: [currentPhase: params.currentPhase])
            return
        }
        def codeParticipant = capture.split("-")
        def participantInstance = Participants.read(codeParticipant[0].toInteger())
        def phaseInstance = EventPhase.read(params.phaseId)
        if (!participantInstance){
            flash.message = "<div class='alert wrong'><img src='"+createLinkTo(dir: 'images', file: 'alert.png')+"' style='width:30px; margin-right:20px;' /><b>Codigo Incorrecto</b></div>"
            if (params.entity == "participantsControlOne"){
                redirect(action: "participantsControl", params: [id: phaseInstance.event.id, currentPhase: phaseInstance.id])
                return
            }
            if (params.entity == "participantsControlTwo"){
                redirect(action: "participantsPhaseControl", params: [id: phaseInstance.event.id, currentPhase: phaseInstance.id])
                return
            }
        }

        def participantPhaseInsatnce = ParticipantPhases.findByParticipantAndPhase(participantInstance,phaseInstance)

        if (participantPhaseInsatnce.checkPhase != true){
            participantPhaseInsatnce.checkPhase = true
            participantPhaseInsatnce.checkDatePhase = new Date()
            participantPhaseInsatnce.save()
            flash.message = "<div class='alert ok'><img src='"+createLinkTo(dir: 'images', file: 'ok.png')+"' style='width:30px; margin-right:20px;' /><b>"+phaseInstance.phase.phaseName+"</b> registrado para <b>"+participantInstance.name+" "+participantInstance.surnames+"</b> ID: <b> "+participantInstance.idCardNumber+"</b></div>"
        }else{
            flash.message = "<div class='alert wrong'><img src='"+createLinkTo(dir: 'images', file: 'alert.png')+"' style='width:30px; margin-right:20px;' /><b>"+phaseInstance.phase.phaseName+"</b> ya ha sido registrado para <b>"+participantInstance.name+" "+participantInstance.surnames+"</b> ID: <b> "+participantInstance.idCardNumber+"</b></div>"
        }
        println(participantPhaseInsatnce)
        if (params.entity == "participantsControlOne"){
            redirect(action: "participantsControl", params: [id: phaseInstance.event.id, currentPhase: phaseInstance.id])
            return
        }
        if (params.entity == "participantsControlTwo"){
            redirect(action: "participantsPhaseControl", params: [id: phaseInstance.event.id, currentPhase: phaseInstance.id])
            return
        }
        
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def editParticipant(){
        def eventInstance = Event.read(params.event)
        def participantInstance= Participants.read(params.id)
        [eventInstance:eventInstance, participantInstance: participantInstance]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def updateParticipant(){
        def participantInstance = Participants.get(params.id)
        println(params)
        if (!participantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (participantInstance.version > version) {
                participantInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'event.label', default: 'Event')] as Object[],
                        "Another user has updated this Event while you were editing")
                render(view: "edit", model: [eventInstance: participantInstance])
                return
            }
        }

        participantInstance.properties = params
        println(participantInstance.properties)
        if (!participantInstance.save(flush: true)) {
            render(view: "edit", model: [participantInstance: participantInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'event.label', default: 'Event'), participantInstance.id])
        redirect(action: "controlParticipantList", id: participantInstance.evet.id)
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def deleteParticipant(){
        def participantInstance = Participants.get(params.idParticipant)
        if (!participantInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "controlParticipantList",id: participantInstance.evet.id)
            return
        }

        try {
            participantInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "controlParticipantList", id: participantInstance.evet.id)
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "controlParticipantList", id: participantInstance.evet.id)
        }
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def printCodeBarParticipants(){
        def eventInstance = Event.read(params.id)
        def filename = (randomUUID() as String)+".pdf"
        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename
        Rectangle pagesize = new Rectangle(252, 324)
        Document document = new Document(pagesize,20,20,140,20)
        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"
        def calibriPath = servletContext.getRealPath('images')+"/fonts/Calibri.ttf"
        BaseFont calibri = BaseFont.createFont(calibriPath, BaseFont.CP1252, BaseFont.EMBEDDED)
        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)

        BaseColor subtitleBackColor = new BaseColor(91,155,213)
        BaseColor subtitleTextColor = new BaseColor(255,255,255)

        Font titleFont = new Font(cambria, 16, Font.BOLD)
        Font subTitleFont = new Font(cambria, 14, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font calibriFont = new Font(calibri,24, Font.BOLD)
        Font normalFontBold = new Font(cambria,26, Font.BOLD)
        try{
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))
            document.open()
            PdfContentByte cb = pdfWriter.getDirectContent()
            def format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")     // Para impresion por criterio
            def date = format.parse('2017-10-04 13:10:41')
            println(date)           // En caso de imprimir desde una fecha de ultimo regsitro
            eventInstance.participants.sort {it.name}.each {
            /*eventInstance.participants.findAll {it.registerDate > date}.sort {it.registerDate}.each {*/
                def codeParticipant = it.id+"-"+it.name.trim().take(1)
                BarcodeQRCode qrcode = new BarcodeQRCode(codeParticipant, 1, 1, null);
                Image qrcodeImage = qrcode.getImage();
                //`1qrcodeImage.setAbsolutePosition(10,500);
                qrcodeImage.setAbsolutePosition(10,10)
                qrcodeImage.scalePercent(170);
                //doc.add(qrcodeImage);
                Barcode128 code128 = new Barcode128();
                code128.setCode(codeParticipant)
                code128.setBarHeight(30f)
                code128.setX(0.8f) //1,3 solo codigo de barras
                Image barCodeImage = code128.createImageWithBarcode(cb, null, null)
                barCodeImage.setWidthPercentage(100f)

                PdfPTable principal = new PdfPTable(2)
                principal.setWidths(250f,250f)
                //PdfPCell completeName = new PdfPCell(new Phrase("XX",calibriFont))
                def complete = it.name.toUpperCase()+" "+it.surnames.toUpperCase()
                println(complete)
                PdfPCell completeName = new PdfPCell(new Phrase(t.nameReduce(text: complete).toString(),calibriFont))

                completeName.setBorder(1)
                completeName.setHorizontalAlignment(1)
                completeName.setColspan(2)
                PdfPCell barcode = new PdfPCell(barCodeImage)
                barcode.setPaddingTop(20f)
                barcode.setBorder(0)
                barcode.setHorizontalAlignment(1)


                PdfPCell qrCodes = new PdfPCell(qrcodeImage)
                qrCodes.setBorder(0)
                qrCodes.setHorizontalAlignment(2)
                qrCodes.setVerticalAlignment(1)
                qrCodes.setPaddingTop(10f)
                principal.addCell(completeName)
                principal.addCell(barcode)
                principal.addCell(qrCodes)
                document.add(principal)
                document.newPage()
            }
            document.close()

        }catch (Exception e){
            e.printStackTrace();
        }
        
        redirect(action: "checkListCodeBar", params: [id: eventInstance.id, filename: filename])
        return
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def printCodeBarParticipantsRegister(){
        def eventInstance = Event.read(params.id)
        def filename = (randomUUID() as String)+".pdf"
        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename
        Rectangle pagesize = new Rectangle(252, 324)
        Document document = new Document(pagesize,20,20,140,20)
        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"
        def calibriPath = servletContext.getRealPath('images')+"/fonts/Calibri.ttf"
        BaseFont calibri = BaseFont.createFont(calibriPath, BaseFont.CP1252, BaseFont.EMBEDDED)
        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)

        BaseColor subtitleBackColor = new BaseColor(91,155,213)
        BaseColor subtitleTextColor = new BaseColor(255,255,255)

        Font titleFont = new Font(cambria, 16, Font.BOLD)
        Font subTitleFont = new Font(cambria, 14, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font calibriFont = new Font(calibri,24, Font.BOLD)
        Font normalFontBold = new Font(cambria,26, Font.BOLD)
        try{
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))
            document.open()
            PdfContentByte cb = pdfWriter.getDirectContent()
            def format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")     // Para impresion por criterio
            def date = format.parse('2017-10-04 13:10:41')
            println(date)           // En caso de imprimir desde una fecha de ultimo regsitro
            eventInstance.participants.sort {it.registerDate}.each {
                /*eventInstance.participants.findAll {it.registerDate > date}.sort {it.registerDate}.each {*/
                def codeParticipant = it.id+"-"+it.name.trim().take(1)
                BarcodeQRCode qrcode = new BarcodeQRCode(codeParticipant, 1, 1, null);
                Image qrcodeImage = qrcode.getImage();
                //`1qrcodeImage.setAbsolutePosition(10,500);
                qrcodeImage.setAbsolutePosition(10,10)
                qrcodeImage.scalePercent(170);
                //doc.add(qrcodeImage);
                Barcode128 code128 = new Barcode128();
                code128.setCode(codeParticipant)
                code128.setBarHeight(30f)
                code128.setX(0.8f) //1,3 solo codigo de barras
                Image barCodeImage = code128.createImageWithBarcode(cb, null, null)
                barCodeImage.setWidthPercentage(100f)

                PdfPTable principal = new PdfPTable(2)
                principal.setWidths(250f,250f)
                //PdfPCell completeName = new PdfPCell(new Phrase("XX",calibriFont))
                def complete = it.name.toUpperCase()+" "+it.surnames.toUpperCase()
                println(complete)
                PdfPCell completeName = new PdfPCell(new Phrase(t.nameReduce(text: complete).toString(),calibriFont))

                completeName.setBorder(1)
                completeName.setHorizontalAlignment(1)
                completeName.setColspan(2)
                PdfPCell barcode = new PdfPCell(barCodeImage)
                barcode.setPaddingTop(20f)
                barcode.setBorder(0)
                barcode.setHorizontalAlignment(1)


                PdfPCell qrCodes = new PdfPCell(qrcodeImage)
                qrCodes.setBorder(0)
                qrCodes.setHorizontalAlignment(2)
                qrCodes.setVerticalAlignment(1)
                qrCodes.setPaddingTop(10f)
                principal.addCell(completeName)
                principal.addCell(barcode)
                principal.addCell(qrCodes)
                document.add(principal)
                document.newPage()
            }
            document.close()

        }catch (Exception e){
            e.printStackTrace();
        }

        redirect(action: "checkListCodeBar", params: [id: eventInstance.id, filename: filename])
        return
    }


    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def checkListCodeBar(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance, filename: params.filename]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def tempFiles(){
        File tempFile = new File(servletContext.getRealPath('images/pdf_files'))
        def files = []
        tempFile.eachFile {
            files.add(fileName: it.name, createdDate: new Date(it.lastModified()).format("dd/MM/yyyy HH:mm:ss"))
        }
        [files : files.sort {it.createdDate}]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def deleteTempFile(){
        def archivos = params.delete_file
        if (archivos!=null){
            if(!(archivos instanceof String)){
                archivos.each {
                    File tempFile = new File(servletContext.getRealPath('images/pdf_files')+"/"+it)
                    if (tempFile.delete()){
                        println("archivo eliminado")
                    }else{
                        println("no se pudo eliminar")
                    }
                }
            }else{
                File tempFile = new File(servletContext.getRealPath('images/pdf_files')+"/"+archivos)
                tempFile.delete()
            }
        }
        redirect(controller: "event", action: "tempFiles")
        return
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def participantsPdfList(){
        def eventInstance = Event.read(params.id)
        def filename = (randomUUID() as String)+".pdf"
        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename
        Document document = new Document(PageSize.LETTER.rotate(),0,0,50,40)

        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"

        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)

        BaseColor subtitleBackColor = new BaseColor(91,155,213)
        BaseColor subtitleTextColor = new BaseColor(255,255,255)

        Font titleFont = new Font(cambria, 16, Font.BOLD)
        Font subTitleFont = new Font(cambria, 12, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font normalFont = new Font(cambria,12, Font.NORMAL)
        try{

            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))
            document.open()
            PdfContentByte cb = pdfWriter.getDirectContent()

            def pathUmsaImage = createLinkTo(dir: "images", file: "umsa_logo.png",absolute: true)
            Image logoUmsa = Image.getInstance(new URL(pathUmsaImage.toString()))
            def pathDipgisImage = createLinkTo(dir: "images", file: "logodipgis.png",absolute: true)
            Image logoDipgis = Image.getInstance(new URL(pathDipgisImage.toString()))

            PdfPTable tableHeader = new PdfPTable(3)
            tableHeader.setSpacingBefore(100f)
            float[] columnHeader = [40f,560f,50f]
            PdfPCell headerLeft = new PdfPCell(logoUmsa,true)
            headerLeft.setPaddingBottom(10f)
            headerLeft.setBorder(Rectangle.BOTTOM)
            PdfPCell headerCenter = new PdfPCell(new Phrase("UNIVERSIDAD MAYOR DE SAN ANDRÉS \n DEPARTAMENTO DE INVESTIGACIÓN POSTGRADO E INTERACCIÓN SOCIAL \n \""+eventInstance.eventName.toUpperCase()+"\"",titleFont))
            headerCenter.setBorder(Rectangle.BOTTOM)
            headerCenter.setHorizontalAlignment(1)
            PdfPCell headerRight = new PdfPCell(logoDipgis,true)
            headerRight.setBorder(Rectangle.BOTTOM)
            tableHeader.setWidths(columnHeader)
            tableHeader.addCell(headerLeft)
            tableHeader.addCell(headerCenter)
            tableHeader.addCell(headerRight)

            PdfPCell numberTitle = new PdfPCell(new Phrase("Nª",subTitleFont))
            numberTitle.setHorizontalAlignment(1)
            numberTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell surnameTitle = new PdfPCell(new Phrase("APELLIDOS",subTitleFont))
            surnameTitle.setHorizontalAlignment(1)
            surnameTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell nameTitle = new PdfPCell(new Phrase("NOMBRES",subTitleFont))
            nameTitle.setHorizontalAlignment(1)
            nameTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell IDTitle = new PdfPCell(new Phrase("CI",subTitleFont))
            IDTitle.setHorizontalAlignment(1)
            IDTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell mailTitle = new PdfPCell(new Phrase("CORREO",subTitleFont))
            mailTitle.setHorizontalAlignment(1)
            mailTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell phoneTitle = new PdfPCell(new Phrase("TELEFONO",subTitleFont))
            phoneTitle.setHorizontalAlignment(1)
            phoneTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell unityTitle = new PdfPCell(new Phrase("UNIDAD",subTitleFont))
            unityTitle.setHorizontalAlignment(1)
            unityTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell singTitle = new PdfPCell(new Phrase("FIRMA",subTitleFont))
            singTitle.setHorizontalAlignment(1)
            singTitle.setVerticalAlignment(1)
            singTitle.setBackgroundColor(subtitleBackColor)
            PdfPTable list = new PdfPTable(8)
            float[] seven = [40f,250f,250f,130f,300f,150f,150f,150f]
            list.setWidths(seven)

            document.add(tableHeader)
            list.addCell(numberTitle)
            list.addCell(surnameTitle)
            list.addCell(nameTitle)
            list.addCell(IDTitle)
            list.addCell(mailTitle)
            list.addCell(phoneTitle)
            list.addCell(unityTitle)
            list.addCell(singTitle)
            def c = 1
            eventInstance.participants.sort {it.surnames}.each{

                PdfPCell number = new PdfPCell(new Phrase((c).toString(),normalFont))
                number.setFixedHeight(40f)
                PdfPCell surname = new PdfPCell(new Phrase(it.surnames,normalFont))
                PdfPCell name = new PdfPCell(new Phrase(it.name,normalFont))

                PdfPCell ID = new PdfPCell(new Phrase(it.idCardNumber,normalFont))
                PdfPCell mail = new PdfPCell(new Phrase(it.email,normalFont))
                PdfPCell phone = new PdfPCell(new Phrase(it.phoneNumber,normalFont))
                PdfPCell unity = new PdfPCell(new Phrase(it.unity,normalFont))
                PdfPCell sing = new PdfPCell(new Phrase("",normalFont))

                list.addCell(number)
                list.addCell(surname)
                list.addCell(name)
                list.addCell(ID)
                list.addCell(mail)
                list.addCell(phone)
                list.addCell(unity)
                list.addCell(sing)
                c++
            }
            document.add(list)
            document.close()
        }catch (Exception e){
            e.printStackTrace();
        }
        redirect(action: "checkListCodeBar", params: [id: eventInstance.id, filename: filename])
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def participantsPdfListTwo(){
        def eventInstance = Event.read(params.id)
        def filename = (randomUUID() as String)+".pdf"
        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename
        Document document = new Document(PageSize.LETTER.rotate(),0,0,50,40)

        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"

        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)

        BaseColor subtitleBackColor = new BaseColor(91,155,213)
        BaseColor subtitleTextColor = new BaseColor(255,255,255)

        Font titleFont = new Font(cambria, 16, Font.BOLD)
        Font subTitleFont = new Font(cambria, 12, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font normalFont = new Font(cambria,12, Font.NORMAL)
        try{

            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))
            document.open()
            PdfContentByte cb = pdfWriter.getDirectContent()

            def pathUmsaImage = createLinkTo(dir: "images", file: "umsa_logo.png",absolute: true)
            Image logoUmsa = Image.getInstance(new URL(pathUmsaImage.toString()))
            def pathDipgisImage = createLinkTo(dir: "images", file: "logodipgis.png",absolute: true)
            Image logoDipgis = Image.getInstance(new URL(pathDipgisImage.toString()))

            PdfPTable tableHeader = new PdfPTable(3)
            tableHeader.setSpacingBefore(100f)
            float[] columnHeader = [40f,560f,50f]
            PdfPCell headerLeft = new PdfPCell(logoUmsa,true)
            headerLeft.setPaddingBottom(10f)
            headerLeft.setBorder(Rectangle.BOTTOM)
            PdfPCell headerCenter = new PdfPCell(new Phrase("UNIVERSIDAD MAYOR DE SAN ANDRÉS \n DEPARTAMENTO DE INVESTIGACIÓN POSTGRADO E INTERACCIÓN SOCIAL \n \""+eventInstance.eventName.toUpperCase()+"\"",titleFont))
            headerCenter.setBorder(Rectangle.BOTTOM)
            headerCenter.setHorizontalAlignment(1)
            PdfPCell headerRight = new PdfPCell(logoDipgis,true)
            headerRight.setBorder(Rectangle.BOTTOM)
            tableHeader.setWidths(columnHeader)
            tableHeader.addCell(headerLeft)
            tableHeader.addCell(headerCenter)
            tableHeader.addCell(headerRight)

            PdfPCell numberTitle = new PdfPCell(new Phrase("Nª",subTitleFont))
            numberTitle.setHorizontalAlignment(1)
            numberTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell surnameTitle = new PdfPCell(new Phrase("APELLIDOS",subTitleFont))
            surnameTitle.setHorizontalAlignment(1)
            surnameTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell nameTitle = new PdfPCell(new Phrase("NOMBRES",subTitleFont))
            nameTitle.setHorizontalAlignment(1)
            nameTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell IDTitle = new PdfPCell(new Phrase("CI",subTitleFont))
            IDTitle.setHorizontalAlignment(1)
            IDTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell mailTitle = new PdfPCell(new Phrase("CORREO",subTitleFont))
            mailTitle.setHorizontalAlignment(1)
            mailTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell phoneTitle = new PdfPCell(new Phrase("TELEF.",subTitleFont))
            phoneTitle.setHorizontalAlignment(1)
            phoneTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell unityTitle = new PdfPCell(new Phrase("UNIDAD",subTitleFont))
            unityTitle.setHorizontalAlignment(1)
            unityTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell singTitle = new PdfPCell(new Phrase("INGRESO",subTitleFont))
            singTitle.setHorizontalAlignment(1)
            singTitle.setVerticalAlignment(1)
            singTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell singTitleTwo = new PdfPCell(new Phrase("SALIDA",subTitleFont))
            singTitleTwo.setHorizontalAlignment(1)
            singTitleTwo.setVerticalAlignment(1)
            singTitleTwo.setBackgroundColor(subtitleBackColor)
            PdfPTable list = new PdfPTable(9)
            float[] seven = [60f,250f,250f,130f,300f,150f,150f,150f,150f]
            list.setWidths(seven)

            document.add(tableHeader)
            list.addCell(numberTitle)
            list.addCell(surnameTitle)
            list.addCell(nameTitle)
            list.addCell(IDTitle)
            list.addCell(mailTitle)
            list.addCell(phoneTitle)
            list.addCell(unityTitle)
            list.addCell(singTitle)
            list.addCell(singTitleTwo)
            def c = 1
            eventInstance.participants.sort {it.surnames}.each{

                PdfPCell number = new PdfPCell(new Phrase((c).toString(),normalFont))
                number.setFixedHeight(40f)
                PdfPCell name = new PdfPCell(new Phrase(it.name,normalFont))
                PdfPCell surname = new PdfPCell(new Phrase(it.surnames,normalFont))
                PdfPCell ID = new PdfPCell(new Phrase(it.idCardNumber,normalFont))
                PdfPCell mail = new PdfPCell(new Phrase(it.email,normalFont))
                PdfPCell phone = new PdfPCell(new Phrase(it.phoneNumber,normalFont))
                PdfPCell unity = new PdfPCell(new Phrase(it.unity,normalFont))
                PdfPCell sing = new PdfPCell(new Phrase("",normalFont))

                list.addCell(number)
                list.addCell(surname)
                list.addCell(name)
                list.addCell(ID)
                list.addCell(mail)
                list.addCell(phone)
                list.addCell(unity)
                list.addCell(sing)
                list.addCell(sing)
                c++
            }
            document.add(list)
            document.close()
        }catch (Exception e){
            e.printStackTrace();
        }
        redirect(action: "checkListCodeBar", params: [id: eventInstance.id, filename: filename])
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def participantsPdfListGroup(){
        def eventInstance = Event.read(params.id)
        def filename = (randomUUID() as String)+".pdf"
        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename
        Document document = new Document(PageSize.LETTER.rotate(),0,0,50,40)

        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"

        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)

        BaseColor subtitleBackColor = new BaseColor(91,155,213)
        BaseColor subtitleTextColor = new BaseColor(255,255,255)

        Font titleFont = new Font(cambria, 16, Font.BOLD)
        Font subTitleFont = new Font(cambria, 12, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font normalFont = new Font(cambria,12, Font.NORMAL)
        try{

            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))
            document.open()
            PdfContentByte cb = pdfWriter.getDirectContent()

            def pathUmsaImage = createLinkTo(dir: "images", file: "umsa_logo.png",absolute: true)
            Image logoUmsa = Image.getInstance(new URL(pathUmsaImage.toString()))
            def pathDipgisImage = createLinkTo(dir: "images", file: "logodipgis.png",absolute: true)
            Image logoDipgis = Image.getInstance(new URL(pathDipgisImage.toString()))

            PdfPTable tableHeader = new PdfPTable(3)
            tableHeader.setSpacingBefore(100f)
            float[] columnHeader = [40f,560f,50f]
            PdfPCell headerLeft = new PdfPCell(logoUmsa,true)
            headerLeft.setPaddingBottom(10f)
            headerLeft.setBorder(Rectangle.BOTTOM)
            PdfPCell headerCenter = new PdfPCell(new Phrase("UNIVERSIDAD MAYOR DE SAN ANDRÉS \n DEPARTAMENTO DE INVESTIGACIÓN POSTGRADO E INTERACCIÓN SOCIAL \n \""+eventInstance.eventName.toUpperCase()+"\"",titleFont))
            headerCenter.setBorder(Rectangle.BOTTOM)
            headerCenter.setHorizontalAlignment(1)
            PdfPCell headerRight = new PdfPCell(logoDipgis,true)
            headerRight.setBorder(Rectangle.BOTTOM)
            tableHeader.setWidths(columnHeader)
            tableHeader.addCell(headerLeft)
            tableHeader.addCell(headerCenter)
            tableHeader.addCell(headerRight)

            PdfPCell numberTitle = new PdfPCell(new Phrase("Nª",subTitleFont))
            numberTitle.setHorizontalAlignment(1)
            numberTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell surnameTitle = new PdfPCell(new Phrase("APELLIDOS",subTitleFont))
            surnameTitle.setHorizontalAlignment(1)
            surnameTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell nameTitle = new PdfPCell(new Phrase("NOMBRES",subTitleFont))
            nameTitle.setHorizontalAlignment(1)
            nameTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell IDTitle = new PdfPCell(new Phrase("ID",subTitleFont))
            IDTitle.setHorizontalAlignment(1)
            IDTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell mailTitle = new PdfPCell(new Phrase("CORREO",subTitleFont))
            mailTitle.setHorizontalAlignment(1)
            mailTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell phoneTitle = new PdfPCell(new Phrase("TELEFONO",subTitleFont))
            phoneTitle.setHorizontalAlignment(1)
            phoneTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell unityTitle = new PdfPCell(new Phrase("UNIDAD",subTitleFont))
            unityTitle.setHorizontalAlignment(1)
            unityTitle.setBackgroundColor(subtitleBackColor)
            PdfPCell singTitle = new PdfPCell(new Phrase("FIRMA",subTitleFont))
            singTitle.setHorizontalAlignment(1)
            singTitle.setVerticalAlignment(1)
            singTitle.setBackgroundColor(subtitleBackColor)



            
            eventInstance.groups.sort {it.nameGroup}.each {
                def c = 1
                document.add(tableHeader)
                PdfPTable list = new PdfPTable(8)
                PdfPCell groupName = new PdfPCell(new Phrase(it.nameGroup,subTitleFont))
                groupName.setColspan(8)
                groupName.setHorizontalAlignment(1)
                groupName.setBackgroundColor(subtitleBackColor)
                float[] seven = [40f,250f,250f,130f,300f,150f,150f,150f]
                list.setWidths(seven)
                list.addCell(groupName)
                list.addCell(numberTitle)
                list.addCell(surnameTitle)
                list.addCell(nameTitle)
                list.addCell(IDTitle)
                list.addCell(mailTitle)
                list.addCell(phoneTitle)
                list.addCell(unityTitle)
                list.addCell(singTitle)

                it.participant.sort {it.surnames}.each {
                    PdfPCell number = new PdfPCell(new Phrase((c).toString(),normalFont))
                    number.setFixedHeight(40f)
                    PdfPCell surname = new PdfPCell(new Phrase(it.surnames,normalFont))
                    PdfPCell name = new PdfPCell(new Phrase(it.name,normalFont))
                    PdfPCell ID = new PdfPCell(new Phrase(it.idCardNumber,normalFont))
                    PdfPCell mail = new PdfPCell(new Phrase(it.email,normalFont))
                    PdfPCell phone = new PdfPCell(new Phrase(it.phoneNumber,normalFont))
                    PdfPCell unity = new PdfPCell(new Phrase(it.unity,normalFont))
                    PdfPCell sing = new PdfPCell(new Phrase("",normalFont))

                    list.addCell(number)
                    list.addCell(surname)
                    list.addCell(name)
                    list.addCell(ID)
                    list.addCell(mail)
                    list.addCell(phone)
                    list.addCell(unity)
                    list.addCell(sing)
                    c++
                }
                document.add(list)
                document.newPage()
            }
            

            document.close()
        }catch (Exception e){
            e.printStackTrace();
        }
        redirect(action: "checkListCodeBar", params: [id: eventInstance.id, filename: filename])
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def printInscription(){
        def participantInstance = Participants.read(params.id)
        def filename = (randomUUID() as String)+".pdf"

        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename
        Document document = new Document(PageSize.LETTER,0,0,38,0)

        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"

        println(fontPath)

        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)

        BaseColor subtitleBackColor = new BaseColor(91,155,213)
        BaseColor subtitleTextColor = new BaseColor(255,255,255)

        Font titleFont = new Font(cambria, 16, Font.BOLD)
        Font subTitleFont = new Font(cambria, 14, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font normalFont = new Font(cambria,8, Font.NORMAL)
        Font normalFontBold = new Font(cambria,8, Font.BOLD)

        try{
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))

            document.setMargins(20, 20, 70, 20)
            document.open()

            PdfContentByte cb = pdfWriter.getDirectContent()
            Barcode128 code128 = new Barcode128();
            def codeParticipant = participantInstance.id+"-"+participantInstance.name.trim().take(1)
            code128.setCode(codeParticipant)
            Image barCodeImage = code128.createImageWithBarcode(cb, null, null)

            println(participantInstance.id+"-"+participantInstance.idCardNumber)
            def pathUmsaImage = createLinkTo(dir: "images", file: "umsa_logo.png",absolute: true)
            println(pathUmsaImage)
            Image logoUmsa = Image.getInstance(new URL(pathUmsaImage.toString()))
            def pathDipgisImage = createLinkTo(dir: "images", file: "logodipgis.png",absolute: true)
            println(pathDipgisImage)
            Image logoDipgis = Image.getInstance(new URL(pathDipgisImage.toString()))

            PdfPTable tableHeader = new PdfPTable(3)
            tableHeader.setSpacingBefore(100f)
            float[] columnHeader = [40f,460f,50f]
            PdfPCell headerLeft = new PdfPCell(logoUmsa,true)
            headerLeft.setPaddingBottom(10f)
            headerLeft.setBorder(Rectangle.BOTTOM)
            PdfPCell headerCenter = new PdfPCell(new Phrase("UNIVERSIDAD MAYOR DE SAN ANDRÉS \n DEPARTAMENTO DE INVESTIGACIÓN POSTGRADO E INTERACCIÓN SOCIAL",titleFont))
            headerCenter.setBorder(Rectangle.BOTTOM)
            headerCenter.setHorizontalAlignment(1)
            PdfPCell headerRight = new PdfPCell(logoDipgis,true)
            headerRight.setBorder(Rectangle.BOTTOM)
            tableHeader.setWidths(columnHeader)
            tableHeader.addCell(headerLeft)
            tableHeader.addCell(headerCenter)
            tableHeader.addCell(headerRight)

            PdfPTable tableCodeBar = new PdfPTable(3)
            float[] columTres = [200f,150f,200f]
            tableCodeBar.setWidths(columTres)
            tableCodeBar.getDefaultCell().setBorder(0)
            tableCodeBar.setSpacingBefore(50f)
            PdfPCell barCodeContainer = new PdfPCell(barCodeImage)
            barCodeContainer.setBorder(1)
            barCodeContainer.setHorizontalAlignment(1)
            barCodeContainer.setVerticalAlignment(1)
            barCodeContainer.setPaddingTop(10f)
            barCodeContainer.setPaddingBottom(10f)
            tableCodeBar.addCell(new Phrase(""))
            tableCodeBar.addCell(barCodeContainer)
            tableCodeBar.addCell(new Phrase(""))

            PdfPTable tableContent = new PdfPTable(1)
            tableContent.getDefaultCell().setBorder(2)
            tableContent.setSpacingBefore(20f)

            PdfPCell contentTitle = new PdfPCell(new Phrase(message(code: 'default.successfull.message', default: 'Successfull Register'), titleFont))
            contentTitle.setHorizontalAlignment(1)
            contentTitle.setBorder(0)
            PdfPCell eventTitle = new PdfPCell(new Phrase(participantInstance.evet.eventName, titleFont))
            eventTitle.setHorizontalAlignment(1)
            eventTitle.setBorder(0)

            tableContent.addCell(contentTitle)
            tableContent.addCell(eventTitle)


            PdfPTable personalInformation = new PdfPTable(2)
            personalInformation.getDefaultCell().setBorder(1)
            personalInformation.setSpacingBefore(20f)

            PdfPCell titlePersonal = new PdfPCell(new Phrase(message(code: 'personal.information.label',default: 'Personal Information'),subTitleFont))
            titlePersonal.setBackgroundColor(subtitleBackColor)
            titlePersonal.setBorder(0)
            titlePersonal.setColspan(2)

            PdfPCell emptyCell = new PdfPCell(new Phrase(""))
            emptyCell.setBorder(0)


            PdfPCell titleNames = new PdfPCell(new Phrase(message(code: 'default.name.label')+": ",normalFontBold))
            titleNames.setBorder(0)
            PdfPCell names = new PdfPCell(new Phrase(participantInstance.name,normalFont))
            names.setBorder(0)
            PdfPCell titleSurnames = new PdfPCell(new Phrase(message(code: 'default.surnames.label')+": ",normalFontBold))
            titleSurnames.setBorder(0)
            PdfPCell surnames = new PdfPCell(new Phrase(participantInstance.surnames,normalFont))
            surnames.setBorder(0)
            PdfPCell titleId = new PdfPCell(new Phrase(message(code: 'default.idNumber.label')+": ",normalFontBold))
            titleId.setBorder(0)
            PdfPCell idNumber = new PdfPCell(new Phrase(participantInstance.idCardNumber,normalFont))
            idNumber.setBorder(0)
            PdfPCell titleCountry = new PdfPCell(new Phrase(message(code: 'default.country.label')+": ",normalFontBold))
            titleCountry.setBorder(0)
            PdfPCell country = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.country),normalFont))
            country.setBorder(0)
            PdfPCell titleCity = new PdfPCell(new Phrase(message(code: 'default.city.label')+": ",normalFontBold))
            titleCity.setBorder(0)
            PdfPCell city = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.city),normalFont))
            city.setBorder(0)


            personalInformation.addCell(titlePersonal)
            personalInformation.addCell(titleNames)
            personalInformation.addCell(names)
            personalInformation.addCell(titleSurnames)
            personalInformation.addCell(surnames)
            personalInformation.addCell(titleId)
            personalInformation.addCell(idNumber)
            personalInformation.addCell(titleCountry)
            personalInformation.addCell(country)
            personalInformation.addCell(titleCity)
            personalInformation.addCell(city)

            PdfPTable contactInformation = new PdfPTable(2)
            contactInformation.setSpacingBefore(20f)
            PdfPCell contactTitle = new PdfPCell(new Phrase(message(code: 'contact.information.label'),subTitleFont))
            contactTitle.setBackgroundColor(subtitleBackColor)
            contactTitle.setColspan(2)
            contactTitle.setBorder(0)
            PdfPCell titlePhone = new PdfPCell(new Phrase(message(code: 'default.phoneNumber.label')+": ",normalFontBold))
            titlePhone.setBorder(0)
            PdfPCell phone = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.phoneNumber),normalFont))
            phone.setBorder(0)
            PdfPCell titleMail = new PdfPCell(new Phrase(message(code: 'default.email.label')+": ",normalFontBold))
            titleMail.setBorder(0)
            PdfPCell email = new PdfPCell(new Phrase(participantInstance.email,normalFont))
            email.setBorder(0)

            contactInformation.addCell(contactTitle)
            contactInformation.addCell(titlePhone)
            contactInformation.addCell(phone)
            contactInformation.addCell(titleMail)
            contactInformation.addCell(email)

            PdfPTable professionInformation = new PdfPTable(2)
            professionInformation.setSpacingBefore(20f)

            PdfPCell professionalTitle = new PdfPCell(new Phrase(message(code: 'professional.information.label'),subTitleFont))
            professionalTitle.setBackgroundColor(subtitleBackColor)
            professionalTitle.setColspan(2)
            professionalTitle.setBorder(0)
            PdfPCell titleInstitution = new PdfPCell(new Phrase(message(code: 'default.unity.label')+": ",normalFontBold))
            titleInstitution.setBorder(0)
            PdfPCell institution = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.unity),normalFont))
            institution.setBorder(0)
            PdfPCell titlePosition = new PdfPCell(new Phrase(message(code: 'default.position.label')+": ",normalFontBold))
            titlePosition.setBorder(0)
            PdfPCell position = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.position),normalFont))
            position.setBorder(0)
            PdfPCell titleProfession = new PdfPCell(new Phrase(message(code: 'default.profession.label')+": ",normalFontBold))
            titleProfession.setBorder(0)
            PdfPCell profession = new PdfPCell(new Phrase(""+t.outNull(text: participantInstance.profession),normalFont))
            profession.setBorder(0)

            professionInformation.addCell(professionalTitle)
            professionInformation.addCell(titleInstitution)
            professionInformation.addCell(institution)
            professionInformation.addCell(titlePosition)
            professionInformation.addCell(position)
            professionInformation.addCell(titleProfession)
            professionInformation.addCell(profession)

            PdfPTable importantNote = new PdfPTable(1)
            importantNote.setSpacingBefore(50f)
            PdfPCell titleNote = new PdfPCell(new Phrase(message(code: 'important.note.label'),subTitleFont))
            titleNote.setBorder(0)
            PdfPCell contentNote
            if (participantInstance.evet.endDate != null){
                contentNote = new PdfPCell(new Phrase(message(code: 'important.note.content', args: [participantInstance.evet.eventName,participantInstance.evet.startDate.format('dd/MM/yyyy'),participantInstance.evet.endDate.format('dd/MM/yyyy'),participantInstance.evet.eventPlace]),normalFont))
            }else{
                contentNote = new PdfPCell(new Phrase(message(code: 'important.note.content.withoutEndDate', args: [participantInstance.evet.eventName,participantInstance.evet.startDate.format('dd/MM/yyyy'),participantInstance.evet.eventPlace]),normalFont))
            }
            contentNote.setBorder(0)
            PdfPCell dateInscription = new PdfPCell(new Phrase(message(code: 'default.dateInscription.label')+": "+participantInstance.registerDate.format('dd/MM/yyyy'),normalFont))
            dateInscription.setHorizontalAlignment(2)
            dateInscription.setBorder(0)
            importantNote.addCell(titleNote)
            importantNote.addCell(contentNote)
            importantNote.addCell(dateInscription)

            PdfPTable footer = new PdfPTable(1)
            footer.setSpacingBefore(50f)
            PdfPCell dipgis = new PdfPCell(new Phrase(message(code: 'dipgis'),normalFont))
            dipgis.setBorder(0)
            dipgis.setHorizontalAlignment(1)
            PdfPCell address = new PdfPCell(new Phrase(message(code: 'address'),normalFont))
            address.setBorder(0)
            address.setHorizontalAlignment(1)
            PdfPCell phoneNumberAndMail = new PdfPCell(new Phrase(message(code: 'phoneDipgis'),normalFont))
            phoneNumberAndMail.setBorder(0)
            phoneNumberAndMail.setHorizontalAlignment(1)
            footer.addCell(dipgis)
            footer.addCell(address)
            footer.addCell(phoneNumberAndMail)

            document.add(tableHeader)
            document.add(tableContent)
            document.add(personalInformation)
            document.add(contactInformation)
            document.add(professionInformation)
            document.add(tableCodeBar)
            document.add(importantNote)
            document.add(footer)
            document.close()
        }catch (Exception e){
            e.printStackTrace();
        }
        redirect(url: createLinkTo(dir: "images/pdf_files", file: filename, absolute: true))
    }

    @Secured(["ROLE_SUPERADMIN"])
    def printParticipantsInscription(){
        def eventInstance = Event.read(params.id)
        def filename = (randomUUID() as String)+".pdf"
        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename

        //def participantInstance = Participants.read(params.id)

        Document document = new Document(PageSize.LETTER,0,0,38,0)

        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"

        println(fontPath)

        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)

        BaseColor subtitleBackColor = new BaseColor(91,155,213)
        BaseColor subtitleTextColor = new BaseColor(255,255,255)

        Font titleFont = new Font(cambria, 16, Font.BOLD)
        Font subTitleFont = new Font(cambria, 14, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font normalFont = new Font(cambria,8, Font.NORMAL)
        Font normalFontBold = new Font(cambria,8, Font.BOLD)

        try{
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))

            document.setMargins(20, 20, 70, 20)
            document.open()

            eventInstance.participants.sort {it.name}.each {
                PdfContentByte cb = pdfWriter.getDirectContent()
                Barcode128 code128 = new Barcode128();
                def codeParticipant = it.id+"-"+it.name.trim().take(1)
                code128.setCode(codeParticipant)
                Image barCodeImage = code128.createImageWithBarcode(cb, null, null)

                println(it.id+"-"+it.idCardNumber)
                def pathUmsaImage = createLinkTo(dir: "images", file: "umsa_logo.png",absolute: true)
                println(pathUmsaImage)
                Image logoUmsa = Image.getInstance(new URL(pathUmsaImage.toString()))
                def pathDipgisImage = createLinkTo(dir: "images", file: "logodipgis.png",absolute: true)
                println(pathDipgisImage)
                Image logoDipgis = Image.getInstance(new URL(pathDipgisImage.toString()))

                PdfPTable tableHeader = new PdfPTable(3)
                tableHeader.setSpacingBefore(100f)
                float[] columnHeader = [40f,460f,50f]
                PdfPCell headerLeft = new PdfPCell(logoUmsa,true)
                headerLeft.setPaddingBottom(10f)
                headerLeft.setBorder(Rectangle.BOTTOM)
                PdfPCell headerCenter = new PdfPCell(new Phrase("UNIVERSIDAD MAYOR DE SAN ANDRÉS \n DEPARTAMENTO DE INVESTIGACIÓN POSTGRADO E INTERACCIÓN SOCIAL",titleFont))
                headerCenter.setBorder(Rectangle.BOTTOM)
                headerCenter.setHorizontalAlignment(1)
                PdfPCell headerRight = new PdfPCell(logoDipgis,true)
                headerRight.setBorder(Rectangle.BOTTOM)
                tableHeader.setWidths(columnHeader)
                tableHeader.addCell(headerLeft)
                tableHeader.addCell(headerCenter)
                tableHeader.addCell(headerRight)

                PdfPTable tableCodeBar = new PdfPTable(3)
                float[] columTres = [200f,150f,200f]
                tableCodeBar.setWidths(columTres)
                tableCodeBar.getDefaultCell().setBorder(0)
                tableCodeBar.setSpacingBefore(50f)
                PdfPCell barCodeContainer = new PdfPCell(barCodeImage)
                barCodeContainer.setBorder(1)
                barCodeContainer.setHorizontalAlignment(1)
                barCodeContainer.setVerticalAlignment(1)
                barCodeContainer.setPaddingTop(10f)
                barCodeContainer.setPaddingBottom(10f)
                tableCodeBar.addCell(new Phrase(""))
                tableCodeBar.addCell(barCodeContainer)
                tableCodeBar.addCell(new Phrase(""))

                PdfPTable tableContent = new PdfPTable(1)
                tableContent.getDefaultCell().setBorder(2)
                tableContent.setSpacingBefore(20f)

                PdfPCell contentTitle = new PdfPCell(new Phrase(message(code: 'default.successfull.message', default: 'Successfull Register'), titleFont))
                contentTitle.setHorizontalAlignment(1)
                contentTitle.setBorder(0)
                PdfPCell eventTitle = new PdfPCell(new Phrase(it.evet.eventName, titleFont))
                eventTitle.setHorizontalAlignment(1)
                eventTitle.setBorder(0)

                tableContent.addCell(contentTitle)
                tableContent.addCell(eventTitle)


                PdfPTable personalInformation = new PdfPTable(2)
                personalInformation.getDefaultCell().setBorder(1)
                personalInformation.setSpacingBefore(20f)

                PdfPCell titlePersonal = new PdfPCell(new Phrase(message(code: 'personal.information.label',default: 'Personal Information'),subTitleFont))
                titlePersonal.setBackgroundColor(subtitleBackColor)
                titlePersonal.setBorder(0)
                titlePersonal.setColspan(2)

                PdfPCell emptyCell = new PdfPCell(new Phrase(""))
                emptyCell.setBorder(0)


                PdfPCell titleNames = new PdfPCell(new Phrase(message(code: 'default.name.label')+": ",normalFontBold))
                titleNames.setBorder(0)
                PdfPCell names = new PdfPCell(new Phrase(it.name,normalFont))
                names.setBorder(0)
                PdfPCell titleSurnames = new PdfPCell(new Phrase(message(code: 'default.surnames.label')+": ",normalFontBold))
                titleSurnames.setBorder(0)
                PdfPCell surnames = new PdfPCell(new Phrase(it.surnames,normalFont))
                surnames.setBorder(0)
                PdfPCell titleId = new PdfPCell(new Phrase(message(code: 'default.idNumber.label')+": ",normalFontBold))
                titleId.setBorder(0)
                PdfPCell idNumber = new PdfPCell(new Phrase(it.idCardNumber,normalFont))
                idNumber.setBorder(0)
                PdfPCell titleCountry = new PdfPCell(new Phrase(message(code: 'default.country.label')+": ",normalFontBold))
                titleCountry.setBorder(0)
                PdfPCell country = new PdfPCell(new Phrase(""+t.outNull(text: it.country),normalFont))
                country.setBorder(0)
                PdfPCell titleCity = new PdfPCell(new Phrase(message(code: 'default.city.label')+": ",normalFontBold))
                titleCity.setBorder(0)
                PdfPCell city = new PdfPCell(new Phrase(""+t.outNull(text: it.city),normalFont))
                city.setBorder(0)


                personalInformation.addCell(titlePersonal)
                personalInformation.addCell(titleNames)
                personalInformation.addCell(names)
                personalInformation.addCell(titleSurnames)
                personalInformation.addCell(surnames)
                personalInformation.addCell(titleId)
                personalInformation.addCell(idNumber)
                personalInformation.addCell(titleCountry)
                personalInformation.addCell(country)
                personalInformation.addCell(titleCity)
                personalInformation.addCell(city)

                PdfPTable contactInformation = new PdfPTable(2)
                contactInformation.setSpacingBefore(20f)
                PdfPCell contactTitle = new PdfPCell(new Phrase(message(code: 'contact.information.label'),subTitleFont))
                contactTitle.setBackgroundColor(subtitleBackColor)
                contactTitle.setColspan(2)
                contactTitle.setBorder(0)
                PdfPCell titlePhone = new PdfPCell(new Phrase(message(code: 'default.phoneNumber.label')+": ",normalFontBold))
                titlePhone.setBorder(0)
                PdfPCell phone = new PdfPCell(new Phrase(""+t.outNull(text: it.phoneNumber),normalFont))
                phone.setBorder(0)
                PdfPCell titleMail = new PdfPCell(new Phrase(message(code: 'default.email.label')+": ",normalFontBold))
                titleMail.setBorder(0)
                PdfPCell email = new PdfPCell(new Phrase(it.email,normalFont))
                email.setBorder(0)

                contactInformation.addCell(contactTitle)
                contactInformation.addCell(titlePhone)
                contactInformation.addCell(phone)
                contactInformation.addCell(titleMail)
                contactInformation.addCell(email)

                PdfPTable professionInformation = new PdfPTable(2)
                professionInformation.setSpacingBefore(20f)

                PdfPCell professionalTitle = new PdfPCell(new Phrase(message(code: 'professional.information.label'),subTitleFont))
                professionalTitle.setBackgroundColor(subtitleBackColor)
                professionalTitle.setColspan(2)
                professionalTitle.setBorder(0)
                PdfPCell titleInstitution = new PdfPCell(new Phrase(message(code: 'default.unity.label')+": ",normalFontBold))
                titleInstitution.setBorder(0)
                PdfPCell institution = new PdfPCell(new Phrase(""+t.outNull(text: it.unity),normalFont))
                institution.setBorder(0)
                PdfPCell titlePosition = new PdfPCell(new Phrase(message(code: 'default.position.label')+": ",normalFontBold))
                titlePosition.setBorder(0)
                PdfPCell position = new PdfPCell(new Phrase(""+t.outNull(text: it.position),normalFont))
                position.setBorder(0)
                PdfPCell titleProfession = new PdfPCell(new Phrase(message(code: 'default.profession.label')+": ",normalFontBold))
                titleProfession.setBorder(0)
                PdfPCell profession = new PdfPCell(new Phrase(""+t.outNull(text: it.profession),normalFont))
                profession.setBorder(0)

                professionInformation.addCell(professionalTitle)
                professionInformation.addCell(titleInstitution)
                professionInformation.addCell(institution)
                professionInformation.addCell(titlePosition)
                professionInformation.addCell(position)
                professionInformation.addCell(titleProfession)
                professionInformation.addCell(profession)

                PdfPTable importantNote = new PdfPTable(1)
                importantNote.setSpacingBefore(50f)
                PdfPCell titleNote = new PdfPCell(new Phrase(message(code: 'important.note.label'),subTitleFont))
                titleNote.setBorder(0)
                PdfPCell contentNote
                if (it.evet.endDate != null){
                    contentNote = new PdfPCell(new Phrase(message(code: 'important.note.content', args: [it.evet.eventName,it.evet.startDate.format('dd/MM/yyyy'),it.evet.endDate.format('dd/MM/yyyy'),it.evet.eventPlace]),normalFont))
                }else{
                    contentNote = new PdfPCell(new Phrase(message(code: 'important.note.content.withoutEndDate', args: [it.evet.eventName,it.evet.startDate.format('dd/MM/yyyy'),it.evet.eventPlace]),normalFont))
                }
                contentNote.setBorder(0)
                PdfPCell dateInscription = new PdfPCell(new Phrase(message(code: 'default.dateInscription.label')+": "+it.registerDate.format('dd/MM/yyyy'),normalFont))
                dateInscription.setHorizontalAlignment(2)
                dateInscription.setBorder(0)
                importantNote.addCell(titleNote)
                importantNote.addCell(contentNote)
                importantNote.addCell(dateInscription)

                PdfPTable footer = new PdfPTable(1)
                footer.setSpacingBefore(50f)
                PdfPCell dipgis = new PdfPCell(new Phrase(message(code: 'dipgis'),normalFont))
                dipgis.setBorder(0)
                dipgis.setHorizontalAlignment(1)
                PdfPCell address = new PdfPCell(new Phrase(message(code: 'address'),normalFont))
                address.setBorder(0)
                address.setHorizontalAlignment(1)
                PdfPCell phoneNumberAndMail = new PdfPCell(new Phrase(message(code: 'phoneDipgis'),normalFont))
                phoneNumberAndMail.setBorder(0)
                phoneNumberAndMail.setHorizontalAlignment(1)
                footer.addCell(dipgis)
                footer.addCell(address)
                footer.addCell(phoneNumberAndMail)

                document.add(tableHeader)
                document.add(tableContent)
                document.add(personalInformation)
                document.add(contactInformation)
                document.add(professionInformation)
                document.add(tableCodeBar)
                document.add(importantNote)
                document.add(footer)
                document.newPage()
            }


            document.close()
        }catch (Exception e){
            e.printStackTrace();
        }
        redirect(url: createLinkTo(dir: "images/pdf_files", file: filename, absolute: true))
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def groupList(){
        def eventInstance = Event.get(params.id)
        [eventInstance:eventInstance]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def saveGroup(){
        def eventGroupInstance = new EventGroup(params)
        if (!eventGroupInstance.save(flush: true)) {
            render(view: "groupList", model: [eventPhaseInstance: eventGroupInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'eventGroup.label', default: 'EventGropu'), eventGroupInstance.event.id])
        redirect(action: "groupList", id: eventGroupInstance.event.id)
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def editGroup(){
        def eventGroupInstance = EventGroup.get(params.id)
        [eventGroupInstance: eventGroupInstance, eventInstance: eventGroupInstance.event]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def updateGroup(){
        def eventGroupInstance = EventGroup.get(params.id)
        if (!eventGroupInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'eventPhase.label', default: 'EventPhase'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (eventGroupInstance.version > version) {
                eventGroupInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'eventPhase.label', default: 'EventPhase')] as Object[],
                        "Another user has updated this EventPhase while you were editing")
                render(view: "edit", model: [eventPhaseInstance: eventGroupInstance])
                return
            }
        }

        eventGroupInstance.properties = params

        if (!eventGroupInstance.save(flush: true)) {
            render(view: "editGroup", model: [eventPhaseInstance: eventGroupInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'eventGroup.label', default: 'EventGroup'), eventGroupInstance.event.id])
        redirect(action: "groupList", id: eventGroupInstance.event.id)
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def deleteGroup(){
        def eventGroupInstance = EventGroup.get(params.id)
        if (!eventGroupInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'eventPhase.label', default: 'EventPhase'), params.id])
            redirect(action: "list")
            return
        }

        try {
            eventGroupInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'eventPhase.label', default: 'EventPhase'), params.id])
            redirect(action: "groupList", id: eventGroupInstance.event.id)
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'eventPhase.label', default: 'EventPhase'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def groupListParticipants(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(["ROLE_SUPERADMIN"])
    def pdfwildCard(){
        def eventInstance = Event.read(params.id)
        def filename = (randomUUID() as String)+".pdf"
        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename
        Rectangle pagesize = new Rectangle(252, 324)
        Document document = new Document(pagesize,20,20,140,20)
        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"
        def calibriPath = servletContext.getRealPath('images')+"/fonts/Calibri.ttf"
        BaseFont calibri = BaseFont.createFont(calibriPath, BaseFont.CP1252, BaseFont.EMBEDDED)
        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)

        BaseColor subtitleBackColor = new BaseColor(91,155,213)
        BaseColor subtitleTextColor = new BaseColor(255,255,255)

        Font titleFont = new Font(cambria, 16, Font.BOLD)
        Font subTitleFont = new Font(cambria, 14, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font calibriFont = new Font(calibri,24, Font.BOLD)
        Font normalFontBold = new Font(cambria,26, Font.BOLD)
        try{
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))
            document.open()
            PdfContentByte cb = pdfWriter.getDirectContent()
            BarcodeQRCode qrcode = new BarcodeQRCode(eventInstance.wildcard.codeWildcard, 1, 1, null);
            Image qrcodeImage = qrcode.getImage();
            qrcodeImage.setAbsolutePosition(10,10)
            qrcodeImage.scalePercent(170);
            Barcode128 code128 = new Barcode128();
            code128.setCode(eventInstance.wildcard.codeWildcard)
            code128.setBarHeight(30f)
            code128.setX(0.8f) //1,3 solo codigo de barras
            Image barCodeImage = code128.createImageWithBarcode(cb, null, null)
            barCodeImage.setWidthPercentage(100f)
            PdfPTable principal = new PdfPTable(2)
            principal.setWidths(250f,250f)
            PdfPCell completeName = new PdfPCell(new Phrase("COMODÍN".toUpperCase(),calibriFont))
            completeName.setBorder(1)
            completeName.setHorizontalAlignment(1)
            completeName.setColspan(2)
            PdfPCell barcode = new PdfPCell(barCodeImage)
            barcode.setPaddingTop(20f)
            barcode.setBorder(0)
            barcode.setHorizontalAlignment(1)
            PdfPCell qrCodes = new PdfPCell(qrcodeImage)
            qrCodes.setBorder(0)
            qrCodes.setHorizontalAlignment(2)
            qrCodes.setVerticalAlignment(1)
            qrCodes.setPaddingTop(10f)
            principal.addCell(completeName)
            principal.addCell(barcode)
            principal.addCell(qrCodes)
            document.add(principal)
            document.newPage()
            document.close()

        }catch (Exception e){
            e.printStackTrace();
        }

        redirect(action: "checkListCodeBar", params: [id: eventInstance.id, filename: filename])
        return
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def participantsControlTwo(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def participantsPhaseControl(){
        def eventInstance = Event.read(params.id)
        def phaseInstance = EventPhase.read(params.currentPhase)
        def firstPhase = eventInstance.phase.sort{it.orderPhase}.first()

        def activeParticipants = ParticipantPhases.where {
            phase == phaseInstance && checkPhase == true
        }

        def registerParticipants = ParticipantPhases.where {
            phase == firstPhase && checkPhase == true
        }

        [eventInstance: eventInstance, phaseInstance: phaseInstance, activeParticipants: activeParticipants, registerParticipants: registerParticipants]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def participantsHtmlList(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN"])
    def completeNewPhase(){
        def eventInstance = Event.read(params.id)
        def phase = EventPhase.read(params.idPhase)
        eventInstance.participants.each {
            def participantPhase = ParticipantPhases.findByParticipantAndPhase(it,phase)
            if (!participantPhase){
                def participantsPhaseNew = new ParticipantPhases(participant: it, phase: phase, checkPhase: false, checkDatePhase: null)
                participantsPhaseNew.save()
            }
        }
        redirect(action: "controlParticipantList",id: eventInstance.id)
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def printSelected(){
        def eventInstance = Event.read(params.id)
        [eventInstance: eventInstance]
    }

    @Secured(["ROLE_ADMIN","ROLE_SUPERADMIN","ROLE_OPERATOR"])
    def printCodeBarSelected(){
        def participants = params.participants
        def participantsList = []
        if (participants == null){
            render message(code: "default.selected.label" , default: "Select a item")
            return
        }
        if (participants.properties.bytes){
            participantsList.add(participants)
        }else{
            participants.each {
                participantsList.add(it)
            }
        }

        def numResult = participantsList.size()
        println(participants.properties)
        println("----------- "+ participantsList +" ------------ ")

        def filename = (randomUUID() as String)+".pdf"
        def pathPdf = servletContext.getRealPath('images')+"/pdf_files/"+filename
        Rectangle pagesize = new Rectangle(252, 324)
        Document document = new Document(pagesize,20,20,140,20)
        def fontPath = servletContext.getRealPath('images')+"/fonts/Cambria.ttf"
        def calibriPath = servletContext.getRealPath('images')+"/fonts/Calibri.ttf"
        BaseFont calibri = BaseFont.createFont(calibriPath, BaseFont.CP1252, BaseFont.EMBEDDED)
        BaseFont cambria = BaseFont.createFont(fontPath, BaseFont.CP1252, BaseFont.EMBEDDED)


        BaseColor subtitleTextColor = new BaseColor(255,255,255)


        Font subTitleFont = new Font(cambria, 14, Font.BOLD)
        subTitleFont.setColor(subtitleTextColor)
        Font calibriFont = new Font(calibri,24, Font.BOLD)


        try{
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathPdf))
            document.open()
            PdfContentByte cb = pdfWriter.getDirectContent()

            participantsList.each {
                def participant = Participants.read(it)
                def codeParticipant = participant.id+"-"+participant.name.trim().take(1)
                BarcodeQRCode qrcode = new BarcodeQRCode(codeParticipant, 1, 1, null);
                Image qrcodeImage = qrcode.getImage();

                qrcodeImage.setAbsolutePosition(10,10)
                qrcodeImage.scalePercent(170);

                Barcode128 code128 = new Barcode128();
                code128.setCode(codeParticipant)
                code128.setBarHeight(30f)
                code128.setX(0.8f) //1,3 solo codigo de barras
                Image barCodeImage = code128.createImageWithBarcode(cb, null, null)
                barCodeImage.setWidthPercentage(100f)

                PdfPTable principal = new PdfPTable(2)
                principal.setWidths(250f,250f)
                def complete = participant.name.toUpperCase()+" "+participant.surnames.toUpperCase()
                println(complete)
                PdfPCell completeName = new PdfPCell(new Phrase(t.nameReduce(text: complete).toString(),calibriFont))

                completeName.setBorder(1)
                completeName.setHorizontalAlignment(1)
                completeName.setColspan(2)
                PdfPCell barcode = new PdfPCell(barCodeImage)
                barcode.setPaddingTop(20f)
                barcode.setBorder(0)
                barcode.setHorizontalAlignment(1)


                PdfPCell qrCodes = new PdfPCell(qrcodeImage)
                qrCodes.setBorder(0)
                qrCodes.setHorizontalAlignment(2)
                qrCodes.setVerticalAlignment(1)
                qrCodes.setPaddingTop(10f)
                principal.addCell(completeName)
                principal.addCell(barcode)
                principal.addCell(qrCodes)
                document.add(principal)
                println(numResult)
                if (numResult>1){
                    document.newPage()
                }
            }
            document.close()

        }catch (Exception e){
            e.printStackTrace();
        }
        render "<iframe style='width: 100%; height:600px;' src='"+createLinkTo(dir: "images/pdf_files", file: filename, absolute: true)+"'></iframe>"
        return
    }
}
