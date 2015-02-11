package com.tcs.mobility.btt.logger.core.source.parser

import groovy.xml.XmlUtil

import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class CentralParser {
	private StringWriter buffer
	private Transformer transformer
	private TransformerFactory transFactory

	public static final String REQUEST_RESPONSE_END_STRING = '</jdf:root>'
	public static final String REQUEST_RESPONSE_lOG_LEVEL = '1'

	public String getRawSourceXML(def logRecordNode){
		return XmlUtil.serialize(logRecordNode)
	}

	public String getReqestResponseXML(String message){
		int index = message.indexOf(REQUEST_RESPONSE_END_STRING)
		message = message.substring(0, index+REQUEST_RESPONSE_END_STRING.length())
		Node nMessage = new XmlParser().parseText(message)
		return XmlUtil.serialize(nMessage)
	}

	public String getMessageFromLogRecord(def logRecordNode){
		def logLevel = logRecordNode.logLevel.text()
		def message = logRecordNode.message.text()
		if(REQUEST_RESPONSE_lOG_LEVEL.equalsIgnoreCase(logLevel)){
			return getReqestResponseXML(message)
		}else{
			return message
		}
	}
	
	public String getFormattedSourceXML(def logRecordNode){
		String output = getRawSourceXML(logRecordNode)
		// Removes the XML Declaration part
		if(output.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")){
			output = output.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		}
		return output.replaceAll(/<message>.*<\/message>/, "<message>"+getMessageFromLogRecord(logRecordNode)+"</message>")
	}
}
