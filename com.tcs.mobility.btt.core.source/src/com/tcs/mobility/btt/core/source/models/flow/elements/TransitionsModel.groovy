package com.tcs.mobility.btt.core.source.models.flow.elements

import com.tcs.mobility.btt.core.source.models.parent.SuperParentModel;
import com.tcs.mobility.btt.core.source.models.utils.WatchableList;

class TransitionsModel extends SuperParentModel{

	WatchableList transitions;
	
	TransitionsModel(){
		transitions = [];
	}
	
	def addTransition(TransitionModel model){
		transitions.add(model);
	}
	
	def removeTransition(TransitionModel model){
		transitions.remove(model)
	}
	
	def obtainAttributes(){
		Set props = ['transitions']
		super.obtainAttributes(props)
	}
}
