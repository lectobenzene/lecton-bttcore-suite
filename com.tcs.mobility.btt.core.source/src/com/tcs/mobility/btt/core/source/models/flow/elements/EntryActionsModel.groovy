package com.tcs.mobility.btt.core.source.models.flow.elements

import com.tcs.mobility.btt.core.source.models.flow.elements.types.parent.StateType
import com.tcs.mobility.btt.core.source.models.parent.SuperParentModel
import com.tcs.mobility.btt.core.source.models.utils.WatchableList;

class EntryActionsModel extends SuperParentModel{

	WatchableList entryActions;
	
	EntryActionsModel(){
		entryActions = []
	}
	
	def addEntryAction(StateType model){
		entryActions.add(model);
	}
	
	def removeTransition(StateType model){
		entryActions.remove(model)
	}
	
	def obtainAttributes(){
		Set props = ['entryActions']
		super.obtainAttributes(props)
	}
	
}
