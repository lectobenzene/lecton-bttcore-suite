package com.tcs.mobility.btt.core.source.models.flow

import com.tcs.mobility.btt.core.source.models.flow.elements.StateModel;
import com.tcs.mobility.btt.core.source.models.flow.elements.TransitionModel;
import com.tcs.mobility.btt.core.source.models.parent.SuperParentModel;
import com.tcs.mobility.btt.core.source.models.utils.WatchableList;

class FlowModel extends SuperParentModel{

	String id;
	WatchableList states;
	
	FlowModel(){
		states = []
	}
	
	def addState(StateModel model){
		states.add(model);
	}
	
	def removeState(StateModel model){
		states.remove(model)
	}

	def obtainAttributes(){
		Set props = ['states']
		super.obtainAttributes(props)
	}
	
}
