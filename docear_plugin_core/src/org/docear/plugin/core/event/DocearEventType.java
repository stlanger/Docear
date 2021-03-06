/**
 * author: Marcel Genzmehr
 * 22.08.2011
 */
package org.docear.plugin.core.event;

/**
 * 
 */
public enum DocearEventType {
	NULL
	, SHOW_DIALOG
	, CLOSE_DIALOG
	, NEW_LIBRARY
	, LIBRARY_NEW_MINDMAP_INDEXING_REQUEST
	, LIBRARY_NEW_PROJECT_INDEXING_REQUEST
	, LIBRARY_NEW_REFERENCES_INDEXING_REQUEST
	, NEW_MY_PUBLICATIONS
	, NEW_INCOMING
	, NEW_LITERATURE_ANNOTATIONS
	, LIBRARY_EMPTY_MINDMAP_INDEX_REQUEST
	, MINDMAP_ADD_PDF_TO_NODE
	, PROPERTY_CHANGED
	, LIBRARY_CHANGED
	, APPLICATION_CLOSING
	, APPLICATION_CLOSING_ABORTED
	, FINISH_THREADS
	, SHOW_LICENSES
	, IMPORT_TO_LIBRARY
	, UPDATE_MAP

}
