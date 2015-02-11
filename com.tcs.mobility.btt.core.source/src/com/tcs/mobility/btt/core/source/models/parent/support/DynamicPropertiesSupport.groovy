package com.tcs.mobility.btt.core.source.models.parent.support



class DynamicPropertiesSupport extends DataBindingSupport{

	def dynamicProperties= [:]

	def propertyMissing(String name, value) {
		dynamicProperties[name] = value
	}
	def propertyMissing(String name) {
		dynamicProperties[name]
	}
}
