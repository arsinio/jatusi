package org.jatusi.gui;


import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;

import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jatusi.JavaParser;
import org.jatusi.Project;
import org.jatusi.outputProjectTypes.NoOutputProjectType;
import org.jatusi.outputProjectTypes.OutputProjectType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.Box;

public class JatusiGui {

	private JFrame frmJatusiJava;
	private JTextField txtJavaProjDir;
	private JTextField txtProjectName;
	private JTable tblFileList;
	private JButton btnGo = new JButton("Go!");
	private ConsoleOutputArea coa = new ConsoleOutputArea();
	private JRadioButton rdbtnStubsOnly;
	private JRadioButton rdbtnFullImplementationExperimental;
	private JTextField txtOutputDir;
	private JPanel pnlOutputProjectTypes = new JPanel();


	/**
	 * Create the application.
	 */
	public JatusiGui()
	{
		initialize();
		frmJatusiJava.setVisible(true);
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frmJatusiJava = new JFrame();
		frmJatusiJava.setResizable(false);
		frmJatusiJava.setTitle("Jatusi :: Java -> C Converter");
		frmJatusiJava.setBounds(100, 100, 450, 300);
		frmJatusiJava.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setVisible(false);
		frmJatusiJava.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);
		
		JMenuItem mntmLoadConfiguration = new JMenuItem("Load Configuration");
		mntmLoadConfiguration.setIcon(new ImageIcon(JatusiGui.class.getResource("/resources/folder.png")));
		mnuFile.add(mntmLoadConfiguration);
		
		JMenuItem mntmSaveConfiguration = new JMenuItem("Save Configuration");
		mntmSaveConfiguration.setIcon(new ImageIcon(JatusiGui.class.getResource("/resources/stiffy.png")));
		mnuFile.add(mntmSaveConfiguration);
		
		mnuFile.addSeparator();
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				System.exit(0);
			}
		});
		mntmExit.setIcon(new ImageIcon(JatusiGui.class.getResource("/resources/power.png")));
		mnuFile.add(mntmExit);
		
		JMenu mnuHelp = new JMenu("Help");
		menuBar.add(mnuHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnuHelp.add(mntmAbout);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmJatusiJava.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel pnlInputOpts = new JPanel();
		tabbedPane.addTab("Input Options", null, pnlInputOpts, null);
		pnlInputOpts.setLayout(new BoxLayout(pnlInputOpts, BoxLayout.Y_AXIS));
		
		JPanel pnlJavaProjSelection = new JPanel();
		pnlInputOpts.add(pnlJavaProjSelection);
		pnlJavaProjSelection.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblJavaProjectDir = new JLabel("Java Project Dir:");
		pnlJavaProjSelection.add(lblJavaProjectDir, "2, 2, right, default");
		
		txtJavaProjDir = new JTextField();
		txtJavaProjDir.setText("C:\\Users\\Christopher Armenio\\workspace\\Jatusi Test Project");
		pnlJavaProjSelection.add(txtJavaProjDir, "4, 2, fill, default");
		txtJavaProjDir.setColumns(10);
		
		JButton btnProjDirBrowse = new JButton("...");
		btnProjDirBrowse.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Jatusi -- Choose Project Directory");
				if( !txtJavaProjDir.getText().isEmpty() ) fileChooser.setCurrentDirectory( new File(txtJavaProjDir.getText()) );
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if( fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION )
				{
					// update our file name
					txtJavaProjDir.setText(fileChooser.getSelectedFile().getAbsolutePath());
					
					// now update our project name
					txtProjectName.setText(getProjectName(fileChooser.getSelectedFile()));
					
					// now update the list of files
					FileListModel tableModel = ((FileListModel)tblFileList.getModel());
					tableModel.clear();
					String[] extensions = {"java"};
					tableModel.addFiles(FileUtils.listFiles(fileChooser.getSelectedFile(), extensions, true));
				}
			}
		});
		pnlJavaProjSelection.add(btnProjDirBrowse, "6, 2");
		
		JLabel lblProjectName = new JLabel("Project Name:");
		pnlJavaProjSelection.add(lblProjectName, "2, 4, right, default");
		
		txtProjectName = new JTextField();
		// now update our project name (to our default value)
		txtProjectName.setText(getProjectName(new File(txtJavaProjDir.getText())));
		pnlJavaProjSelection.add(txtProjectName, "4, 4, fill, top");
		txtProjectName.setColumns(10);
		
		JPanel pnlFilesToParse = new JPanel();
		pnlFilesToParse.setBorder(new TitledBorder(null, "Select files to parse:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlInputOpts.add(pnlFilesToParse);
		pnlFilesToParse.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrlPaneFileList = new JScrollPane();
		scrlPaneFileList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pnlFilesToParse.add(scrlPaneFileList);
		
		tblFileList = new JTable(new FileListModel());
		tblFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblFileList.setTableHeader(null);
		tblFileList.setShowVerticalLines(false);
		tblFileList.setIntercellSpacing(new Dimension(0, 1));
		tblFileList.getColumnModel().getColumn(0).setMaxWidth(25);
		tblFileList.setRowSelectionAllowed(false);
		tblFileList.setColumnSelectionAllowed(false);
		scrlPaneFileList.setViewportView(tblFileList);
		
		JPanel pnlOutputOpts = new JPanel();
		tabbedPane.addTab("Output Options", null, pnlOutputOpts, null);
		pnlOutputOpts.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlOutputDir = new JPanel();
		pnlOutputDir.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlOutputOpts.add(pnlOutputDir, BorderLayout.NORTH);
		pnlOutputDir.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblOutputDirectory = new JLabel("Output Directory:");
		pnlOutputDir.add(lblOutputDirectory, "2, 2, right, default");
		
		txtOutputDir = new JTextField();
		txtOutputDir.setText("C:\\Users\\Christopher Armenio\\Desktop\\test");
		pnlOutputDir.add(txtOutputDir, "4, 2, fill, default");
		txtOutputDir.setColumns(10);
		
		JButton btnOutputDirBrowse = new JButton("...");
		btnOutputDirBrowse.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Jatusi -- Choose Output Directory");
				if( !txtOutputDir.getText().isEmpty() ) fileChooser.setCurrentDirectory( new File(txtOutputDir.getText()) );
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if( fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION )
				{
					// update our output directory
					txtOutputDir.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		pnlOutputDir.add(btnOutputDirBrowse, "6, 2");
		ButtonGroup grpGenOptions = new ButtonGroup();
		ButtonGroup grpOutputProjectType = new ButtonGroup();
		
		JPanel panel = new JPanel();
		pnlOutputOpts.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel pnlGenOpts = new JPanel();
		panel.add(pnlGenOpts);
		pnlGenOpts.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlGenOpts.setBorder(new TitledBorder(null, "Generation Options:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlGenOpts.setLayout(new BoxLayout(pnlGenOpts, BoxLayout.Y_AXIS));
		
		rdbtnStubsOnly = new JRadioButton("stubs only", true);
		pnlGenOpts.add(rdbtnStubsOnly);
		grpGenOptions.add(rdbtnStubsOnly);
		
		rdbtnFullImplementationExperimental = new JRadioButton("full implementation (experimental)");
		pnlGenOpts.add(rdbtnFullImplementationExperimental);
		grpGenOptions.add(rdbtnFullImplementationExperimental);
		
		panel.add(pnlOutputProjectTypes);
		pnlOutputProjectTypes.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlOutputProjectTypes.setBorder(new TitledBorder(null, "Output Project Type:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlOutputProjectTypes.setLayout(new BoxLayout(pnlOutputProjectTypes, BoxLayout.Y_AXIS));
		
		// add our project types
		if( !this.addProjectOutputTypes(grpOutputProjectType) ) System.exit(-1);
		
		JPanel pnlProcess = new JPanel();
		tabbedPane.addTab("Process", null, pnlProcess, null);
		pnlProcess.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlStartProcess = new JPanel();
		pnlStartProcess.setBorder(new EmptyBorder(0, 0, 5, 0));
		pnlProcess.add(pnlStartProcess, BorderLayout.NORTH);
		pnlStartProcess.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblStartConversionProcess = new JLabel("Start Conversion Process:");
		pnlStartProcess.add(lblStartConversionProcess, "2, 2");
		btnGo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				processConversion();
			}
		});
		pnlStartProcess.add(btnGo, "4, 2");
		
		JPanel pnlOutput = new JPanel();
		pnlOutput.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlProcess.add(pnlOutput);
		pnlOutput.setLayout(new BorderLayout(0, 0));
		
		
		pnlOutput.add(this.coa, BorderLayout.CENTER);
		System.setOut(this.coa.out);
		System.setErr(this.coa.err);
	}

	
	/**
	 * Populates the OutputProjectType panel with all known
	 * project output types
	 * 
	 * @param grpOutputProjectTypeIn the button group which should be
	 * 		used for the selection radio buttons
	 * 
	 * @return true on success, false on failure
	 */
	private boolean addProjectOutputTypes(ButtonGroup grpOutputProjectTypeIn)
	{	
		// always add the "none" option
		pnlOutputProjectTypes.add(new NoOutputProjectType(grpOutputProjectTypeIn));
		
		// iterate through all known project output options and add them
		for(PojoClass pojoClass : PojoClassFactory.enumerateClassesByExtendingType("org.jatusi.outputProjectTypes", OutputProjectType.class, null))
		{
			if(pojoClass.getClazz().equals(NoOutputProjectType.class)) continue;
			
			try
			{
				Constructor<?> constructor = pojoClass.getClazz().getDeclaredConstructor(ButtonGroup.class);
				if( constructor != null )
				{
					OutputProjectType newOutputProjectType = (OutputProjectType)constructor.newInstance(grpOutputProjectTypeIn);
					if( newOutputProjectType != null ) pnlOutputProjectTypes.add(newOutputProjectType);
				}
				else
				{
					System.err.println("Error configuring OutputProject types");
					return false;
				}
				
			}
			catch (Exception e)
			{
				System.err.println("Exception while configuring OutputProject types");
				return false;
			}
		}
		
		// if we made it here we were successful
		return true;
	}
	
	
	/**
	 * Returns which OutputProjectType has been selected by the user.
	 * 
	 * @return the selected output project type or NULL on error
	 */
	private OutputProjectType getSelectedOutputProjectType()
	{
		Component[] comps = pnlOutputProjectTypes.getComponents();
		for( Component currComp : comps )
		{
			if( currComp instanceof OutputProjectType )
			{
				OutputProjectType opt = (OutputProjectType)currComp;
				if( opt.isSelected() ) return opt;
			}
		}
		
		return null;
	}

	
	private void processConversion()
	{
		coa.out.println("Starting project conversion...");
		
		// run in a thread so we don't halt the GUI refresh thread
		Thread processThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// clear our console output window first
				coa.clear();
				
				// time to process our files
				List<File> selectedFiles = ((FileListModel)tblFileList.getModel()).getSelectedFiles();
				
				// make sure we have a valid output directory
				File outputDir = new File(txtOutputDir.getText());
				if( !outputDir.exists() )
				{
					coa.err.println("Invalid output directory");
					coa.err.println("Operation failed!");
					return;
				}
				
				// determine out output project type
				OutputProjectType opt = getSelectedOutputProjectType();
				if( opt == null )
				{
					coa.err.println("No output project type selected");
					coa.err.println("Operation failed!");
					return;
				}
				
				// output our configuration parameters
				coa.out.printf("Project name: %s\r\n", txtProjectName.getText());
				coa.out.printf("Generation options: %s\r\n", (rdbtnStubsOnly.isSelected() ? "stubs only" : "full implementation"));
				coa.out.printf("Output project type: %s\r\n", opt.getName());
				coa.out.printf("Output directory: '%s'\r\n", outputDir.getAbsolutePath());
				coa.out.printf("Parsing %d files\r\n", selectedFiles.size());
				coa.out.println("");
				
				// parse the project
				Project parsedProject = JavaParser.parseProject(selectedFiles);
				if( parsedProject == null )
				{
					coa.err.println("Operation failed!");
					return;
				}
				
				// process the files based upon the user selection
				if( rdbtnStubsOnly.isSelected() )
				{
					if( parsedProject.generateStubFiles(outputDir) )
					{
						coa.out.println("Source generation complete!");
					}
					else
					{
						coa.err.println("Operation failed!");
						return;
					}
				}
				else
				{
					coa.err.println("Operation not supported");
					return;
				}
				
				// now create our output files
				if( !opt.process(txtProjectName.getText(), outputDir, selectedFiles) )
				{
					coa.err.println("Operation failed!");
					return;
				}
				
				// if we made it here, we were successful
				coa.out.println("Operation complete!");
			}
		});
		processThread.start();
	}
	

	/**
	 * Returns a string representing the project name. If an Eclipse
	 * *.project file is present in the specified project directory,
	 * the project name is parsed from the file. If no *.project
	 * file is found, the project name becomes the name of the 
	 * last directory.
	 * 
	 * @param projectDir a directory containing the java files which
	 * 		are to be parsed
	 * 
	 * @return a string representing the project name
	 */
	private static String getProjectName(File projectDir)
	{
		String retVal = "unknown";
		
		File projectFile = new File(projectDir, ".project");
		if( projectFile.exists() )
		{
			// project file exists..parse it!
			try
			{
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(projectFile);
				doc.getDocumentElement().normalize();			// recommended
				
				retVal = XPathFactory.newInstance().newXPath().compile("/projectDescription/name").evaluate(doc);
			}
			catch (Exception e)
			{
				System.err.printf("Error parsing '.project' file: %s\r\n", e.getMessage());
			}
		}
		else
		{
			// project file doesn't exit...return the last folder name
			retVal = projectDir.getName();
		}
		
		return retVal;
	}
}
