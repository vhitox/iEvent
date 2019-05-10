package ievent

class HelpFunctionsTagLib {
    static namespace = "t"

    def outNull = {attrs, body ->
        if (attrs.text!= null){
            out<< attrs.text
        }else{
            out<< ""
        }
    }

    def nameReduce = {attrs, body ->
        def text = attrs.text.toString()
        def splitText = text.split(" ")        
        if (splitText.length > 4){
            text = ""
            for (def i = 0; i<= 3; i++){
                text+=splitText[i]+" "
            }
            text = text.trim()
        }
        out << text
    }
}
