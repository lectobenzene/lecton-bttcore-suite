package com.tcs.mobility.btt.core.source.models.parent

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.List;
import java.util.Set;

import com.tcs.mobility.btt.core.source.models.parent.support.DynamicPropertiesSupport

class SuperParentModel extends DynamicPropertiesSupport{
	
	private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>()

	
	def comments = []

	def addComment(def comment){
		comments.add(comment)
	}

	def nodes = []
	
	def addNode(def node){
		nodes.add(node)
	}

	
	public void notifyListeners(def object, def property, def oldValue, def newValue) {
		for (PropertyChangeListener name : listener) {
			println "Property Change fired"
			name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue))
		}
		firePropertyChange(property, oldValue, newValue)
	}

	public void addChangeListener(PropertyChangeListener newListener) {
		listener.add(newListener)
	}
	
	def obtainAttributes(Set props){
		if(!props){
			props = []
		}
		props.addAll([
			'class',
			'metaclass',
			'listener',
			'comments',
			'nodes',
			'dynamicProperties'
		])
		def map = [:]
		this.properties.each{ key, value ->
			if(key in props || !value){
				return
			}
			map.put(key, value)
		}
		this.dynamicProperties.each{ key, value ->
			map.put(key, value)
		}
		return map
	}
	
//	String toString(){
//		StringWriter writer =  new StringWriter()
//		writer <<'\n'
//		getComments().each { writer << "COMMENT : ${it.text}\n" }
//		getProperties().each {field, value ->
//
//			if(field == 'dynamicProperties'){
//				println value
//				value.each { dKey, dValue ->
//					writer << "FIELD : ${dKey} = ${dValue}\n"
//				}
//			}else if(field != 'class' && field != 'comments'){
//				writer << "FIELD : ${field} = ${value}\n"
//			}
//		}
//		return writer.toString()
//	}
}
