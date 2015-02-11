package com.tcs.mobility.btt.logger.core.source.controller

import com.tcs.mobility.btt.logger.core.source.models.LogRecords
import com.tcs.mobility.btt.logger.core.source.parser.CentralParser

import groovy.xml.XmlUtil

class Controller {

	public static void main(args){
		println 'Started...'
		Controller ct = new Controller()
		List logRecords = ct.getLogRecords('C:\\Users\\Saravana\\Desktop\\log.txt')

		println logRecords.size()

		logRecords.each { LogRecords logRecord ->
			println logRecord.wasReturnCode
		}

		Node root = ct.parseFile('C:\\Users\\Saravana\\Desktop\\log.txt')
		def goal = ct.getLogRecord(root, 225)
		CentralParser parser = new CentralParser()
		println parser.getFormattedSourceXML(goal)
	}

	public List<LogRecords> getLogRecords(String logFilePath){
		println 'file path = '+logFilePath
		File logFile = new File(logFilePath)
		return getLogRecords(logFile)
	}

	public List<LogRecords> getLogRecords(File logFile){
		Node root = parseFile(logFile)
		return getLogRecords(root)
	}

	public List<LogRecords> getLogRecords(Node root) {
		List<LogRecords> logRecords = new ArrayList<LogRecords>()
		LogRecords logRecord
		root.'log-record'.each { Node record ->
			logRecord = new LogRecords()
			logRecord.time = record.time.text()
			logRecord.userId = record.userId.text()
			logRecord.psp = record.psp.text()
			logRecord.flow = record.flow.text()
			logRecord.state = record.state.text()
			logRecord.wasReturnCode = record.wasReturnCode.text()
			logRecord.text = record.text.text()
			logRecord.message = record.message.text()
			logRecord.logLevel = record.logLevel.text()
			logRecord.sequence = record.sequence.text()
			logRecords.add(logRecord)
		}
		return logRecords
	}

	public Node parseFile(File file){
		Node root = new XmlParser().parseText("<ROOT>${file.text}</ROOT>")
		return root
	}

	public Node parseFile(String logFilePath){
		File logFile = new File(logFilePath)
		return parseFile(logFile)
	}

	public Node getLogRecord(Node root, int sequence){
		return root.'log-record'[sequence-1]
	}
}
