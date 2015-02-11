package test

import com.tcs.mobility.btt.core.source.models.context.ContextModel
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.KeyedCollectionModel
import com.tcs.mobility.btt.core.source.models.context.elements.dataelements.parent.DataElementModel
import com.tcs.mobility.btt.core.source.models.context.elements.reference.RefKCollModel
import com.tcs.mobility.btt.core.source.parsers.ServiceParser
import com.tcs.mobility.btt.core.source.parsers.context.ContextParser

class ServiceParserTest {

	static void main(def args){
		println 'running...'
		ServiceParser parser  = new ServiceParser()

		def file = new File('temp/GetUserInfoOp.xml')
		parser.parse(file.text)

		//		def model = parser.getProcessorModel()
		//		ProcessorParser processorParser = new ProcessorParser()
		//		String processorContent = processorParser.build(model)
		//
		//		parser.setProcessorModel(model)
		//		String content = parser.build()
		//		println content


		//		trial()

		ContextModel model = parser.getContextModel()
		ContextParser contextParser = new ContextParser()
		String contextContent = contextParser.build(model)
		println contextContent

		model.getRefKColls().each{ RefKCollModel refKColl ->
			KeyedCollectionModel rootKColl = refKColl.getRootKColl()
			println rootKColl.getId()
			rootKColl.getChildren().each{ DataElementModel child ->
				println 'COMP ID = '+child.getCompositeId()
				child.getChildren().each{ DataElementModel grandChild ->
				}
			}

		}

		//		def content = parser.build()
		//		println '---'*3
		//		println content

		//		FlowModel model = parser.getFlowModel()
		//		FlowParser flowParser = new FlowParser()
		//		String flowContent = flowParser.build(model)
		//		println flowContent

	}



	def static trial(){
		ServiceParser parser  = new ServiceParser()

		def file = new File('temp/NewFile.xml')
		parser.parse(file.text)
	}
}
