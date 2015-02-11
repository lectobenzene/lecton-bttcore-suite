package com.tcs.mobility.btt.core.source.models.parent.elements

class Comment {

	public Comment(def text) {
		this.text = text
	}

	/**
	 * Get the text of the comment in string format
	 */
	def text

	def getText(){
		return text.trim()
	}

	def setText(def text){
		this.text = text.trim()
	}
}
