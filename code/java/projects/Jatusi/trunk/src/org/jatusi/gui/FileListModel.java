/**
 * 
 */
package org.jatusi.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author Christopher Armenio
 *
 */
public class FileListModel extends AbstractTableModel
{
	private class ListEntry
	{
		private Boolean isSelected;
		private File file;
		
		private ListEntry(File fileIn)
		{
			this.file = fileIn;
			this.isSelected = true;
		}
	}
	
	
	private ArrayList<ListEntry> objectData = new ArrayList<ListEntry>();
	
	
	@Override
	public int getColumnCount()
	{
		return 2;
	}

	
	@Override
	public int getRowCount()
	{
		return objectData.size();
	}

	
	@Override
	public Object getValueAt(int rowIn, int colIn)
	{
		ListEntry currEntry = objectData.get(rowIn);
		if( currEntry == null ) return null;
		
		if( colIn == 0 ) return currEntry.isSelected;
		else if( colIn == 1) return currEntry.file.getName();
		
		// if we made it here, we have an invalid column
		return null;
	}

	
	@Override
	public Class getColumnClass(int colIn)
	{
		if( colIn == 0 ) return Boolean.class;
		else if( colIn == 1 ) return String.class;
		
		return null;
	}
	
	
	@Override
	public boolean isCellEditable(int rowIn, int colIn)
	{
		return (colIn == 0) ? true : false;
	}
	
	
	@Override
	public void setValueAt(Object valIn, int rowIn, int colIn)
	{
		if( (colIn == 0) && (valIn instanceof Boolean) )
		{
			ListEntry currEntry = objectData.get(rowIn);
			if( currEntry != null ) currEntry.isSelected = (Boolean)valIn;
		}
	}
	
	
	public void addFiles(Collection<File> filesIn)
	{
		for( File currFile : filesIn )
		{
			this.objectData.add(new ListEntry(currFile));
		}
		this.fireTableDataChanged();
	}
	
	
	public void clear()
	{
		this.objectData.clear();
		this.fireTableDataChanged();
	}
	
	
	public List<File> getSelectedFiles()
	{
		List<File> retVal = new ArrayList<File>();
		
		for( ListEntry currEntry : this.objectData )
		{
			if( currEntry.isSelected ) retVal.add(currEntry.file);
		}
		
		return retVal;
	}
}
