/*
 * Copyright (c) 2012 David Green
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package castledesigner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * An application for the game "Stronghold Kingdoms" that helps players design
 * their castle layout in advance, and allows them to share their designs
 * through plain alphanumeric strings.
 *
 * @author David Green
 */
public class Editor
{
	public static final String	programVersion	= "1.11";
	private static LandPanel	landPanel;
	private static JFrame		frame;
	private static JFileChooser	saveFileChooser;
	private static JFileChooser	openFileChooser;
	private static File			currentFile;
	private static JPanel		errorPanel;
	private static final String	FILE_EXTENSION	= "png";
	
	public static void main(String[] args)
	{
		setLookAndFeel();
		
		JPanel mainPanel = new JPanel();
		
		landPanel = new LandPanel();
		landPanel.getLandGrid().addDesignListener(new DesignListener()
		{
			public void designChanged()
			{
				updateErrorPanel();
			}
		});
		
		BuildingsPanel buildingsPanel = new BuildingsPanel();
		buildingsPanel.setCastle(landPanel.getLandGrid().getCastle());
		
		buildingsPanel.addPropertyChangeListener(BuildingsPanel.SELECTED_BUILDING, new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				landPanel.getLandGrid().setSelectedBuilding((BuildingType) evt.getNewValue());
			}
		});
		
		landPanel.getLandGrid().addDesignListener(buildingsPanel);
		landPanel.getLandGrid().getCastle().setBuildingsPanel(buildingsPanel);
		
		TipsPanel tipsPanel = new TipsPanel();
		
		errorPanel = new JPanel();
		errorPanel.setBorder(new EmptyBorder(5, 10, 0, 0));
		errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));
		
		JPanel rightPanel = new JPanel();
		GroupLayout layout = new GroupLayout(rightPanel);
		rightPanel.setLayout(layout);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(buildingsPanel).addComponent(errorPanel).addComponent(tipsPanel));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(buildingsPanel)
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(errorPanel).addComponent(tipsPanel));
				
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, landPanel, rightPanel);
		
		mainPanel.add(splitPane);
		
		saveFileChooser = new JFileChooser();
		saveFileChooser.setFileFilter(new FileNameExtensionFilter("Stronghold Kingdoms Castle Design", FILE_EXTENSION));
		saveFileChooser.setAcceptAllFileFilterUsed(false);
		saveFileChooser.addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				File file = saveFileChooser.getSelectedFile();
				if (file != null)
				{
					String path = file.getAbsolutePath();
					if (!path.endsWith('.' + FILE_EXTENSION))
					{
						File newFile = new File(path + '.' + FILE_EXTENSION);
						saveFileChooser.setSelectedFile(newFile);
					}
				}
			}
		});
		
		openFileChooser = new JFileChooser();
		
		// It would be nice to use the following, but for backwards
		// compatibility reasons we can't.
		// openFileChooser.setFileFilter(new FileNameExtensionFilter("Stronghold
		// Kingdoms Castle Design", FILE_EXTENSION));
		
		openFileChooser.setFileFilter(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				String[] s = file.getName().split("\\.");
				return file.isDirectory() || s.length <= 1 || s[s.length - 1].equals(FILE_EXTENSION);
			}
			
			@Override
			public String getDescription()
			{
				return null;
			}
		});
		
		JScrollPane mainScrollPane = new JScrollPane(mainPanel);
		
		frame = new JFrame("Stronghold Kingdoms Castle Designer");
		frame.setJMenuBar(createMenuBar());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainScrollPane);
		frame.pack();
		frame.setVisible(true);
	}
	
	static void updateErrorPanel()
	{
		errorPanel.removeAll();
		List<String> designErrors = landPanel.getLandGrid().getCastle().getDesignErrors();
		for (String designError : designErrors)
		{
			JLabel designErrorLabel = new JLabel(designError);
			designErrorLabel.setForeground(Color.red);
			designErrorLabel.setFont(new Font(designErrorLabel.getFont().getName(), Font.BOLD, designErrorLabel.getFont().getSize()));
			errorPanel.add(designErrorLabel);
		}
		errorPanel.revalidate();
	}
	
	/**
	 * Attempts to set the Look and Feel of the application to the native
	 * platform.
	 */
	private static void setLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex)
		{
			Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex)
		{
			Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex)
		{
			Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex)
		{
			Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * Returns the menu bar for our application's main screen.
	 * 
	 * @return the JMenuBar for our application's main screen
	 */
	private static JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(createFileMenu());
		menuBar.add(createHelpMenu());
		
		return menuBar;
	}
	
	/**
	 * Returns the File menu
	 * 
	 * @return the File JMenu
	 */
	private static JMenu createFileMenu()
	{
		JMenu fileMenu = new JMenu("File");
		
		fileMenu.add(createOpenMenuItem());
		fileMenu.add(createSaveMenuItem());
		fileMenu.add(createSaveAsMenuItem());
		
		fileMenu.add(new JPopupMenu.Separator());
		fileMenu.add(createExportMenuItem());
		fileMenu.add(createImportMenuItem());
		
		fileMenu.add(new JPopupMenu.Separator());
		fileMenu.add(createClearMenuItem());
		
		fileMenu.add(new JPopupMenu.Separator());
		fileMenu.add(createExitMenuItem());
		
		return fileMenu;
	}
	
	/**
	 * Returns the menu item to open/load castles.
	 *
	 * @return the Open menu item
	 */
	private static JMenuItem createOpenMenuItem()
	{
		JMenuItem openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int result = openFileChooser.showOpenDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION)
				{
					File file = openFileChooser.getSelectedFile();
					BufferedReader in = null;
					String errorMessage = null;
					try
					{
						String importString = null;
						if (file.getName().endsWith("." + FILE_EXTENSION))
						{
							BufferedImage bufferedImage = ImageIO.read(file);
							importString = Barcode.extractBarcode(bufferedImage);
						} else
						{
							in = new BufferedReader(new FileReader(file));
							importString = in.readLine();
						}
						importData(importString);
						currentFile = file;
					} catch (IOException ex)
					{
						Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
						errorMessage = ex.getLocalizedMessage();
					} catch (InvalidBarcodeException ex)
					{
						errorMessage = "Choosing a random image is naughty!\n" + file.getAbsolutePath() + " is not a valid castle design image.";
					} catch (UnsupportedVersionException ex)
					{
						errorMessage = ex.getMessage();
					} finally
					{
						if (in != null)
						{
							try
							{
								in.close();
							} catch (IOException ex)
							{
								Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
						if (errorMessage != null)
						{
							JOptionPane.showMessageDialog(frame, errorMessage, "Error Reading File", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		return openMenuItem;
	}
	
	/**
	 * Returns the menu item to save the castle.
	 *
	 * @return the Save menu item.
	 */
	private static JMenuItem createSaveMenuItem()
	{
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (currentFile == null || !currentFile.getName().endsWith('.' + FILE_EXTENSION))
				{
					showSaveDialog();
				} else
				{
					saveFile(currentFile);
				}
			}
		});
		return saveMenuItem;
	}
	
	private static void showSaveDialog()
	{
		int result = saveFileChooser.showSaveDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			saveFile(saveFileChooser.getSelectedFile());
		}
	}
	
	private static void saveFile(File file)
	{
		PrintWriter out = null;
		try
		{
			BufferedImage bufferedImage = landPanel.getDesignImage();
			Barcode.embedBarcode(bufferedImage, generateExportString());
			
			ImageIO.write(bufferedImage, "png", file);
			currentFile = file;
		} catch (IOException ex)
		{
			Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
		} finally
		{
			if (out != null)
			{
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * Returns the menu item to save the castle under a specific filename.
	 *
	 * @return the SaveAs menu item.
	 */
	private static JMenuItem createSaveAsMenuItem()
	{
		JMenuItem saveAsMenuItem = new JMenuItem("Save As");
		saveAsMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showSaveDialog();
			}
		});
		return saveAsMenuItem;
	}
	
	/**
	 * Returns the menu item responsible for showing a text string containing
	 * all the inputted data.
	 *
	 * @return the JMenuItem for exporting
	 */
	private static JMenuItem createExportMenuItem()
	{
		JMenuItem exportMenuItem = new JMenuItem("Create Export Text");
		exportMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String exportString = generateExportString();
				JTextArea textArea = new JTextArea(exportString);
				textArea.setLineWrap(true);
				textArea.setEditable(false);
				
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setPreferredSize(new Dimension(450, 300));
				
				JPanel panel = new JPanel();
				panel.add(scrollPane);
				
				JButton clipboardButton = new JButton("Copy to Clipboard");
				clipboardButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(exportString), null);
					}
				});
				panel.add(clipboardButton);
				
				JOptionPane.showMessageDialog(frame, panel);
			}
		});
		return exportMenuItem;
	}
	
	/**
	 * Returns the menu item responsible for allowing input of a string that
	 * will populate our data.
	 *
	 * @return the JMenuItem for importing
	 */
	private static JMenuItem createImportMenuItem()
	{
		JMenuItem importMenuItem = new JMenuItem("Import Text");
		importMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final JTextArea textArea = new JTextArea();
				textArea.setLineWrap(true);
				
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setPreferredSize(new Dimension(450, 300));
				
				JPanel panel = new JPanel();
				panel.add(scrollPane);
				
				JButton clipboardButton = new JButton("Import");
				clipboardButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							importData(textArea.getText());
						} catch (UnsupportedVersionException ex)
						{
							JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error Importing Design", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				panel.add(clipboardButton);
				
				JOptionPane.showMessageDialog(frame, panel);
			}
		});
		return importMenuItem;
	}
	
	/**
	 * Returns a menu item for exiting the application.
	 *
	 * @return the JMenuItem for exiting the application
	 */
	private static JMenuItem createExitMenuItem()
	{
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame.dispose();
			}
		});
		return exitMenuItem;
	}
	
	/**
	 * Returns a menu item for clearing the data.
	 *
	 * @return the JMenuItem for clearing the data
	 */
	private static JMenuItem createClearMenuItem()
	{
		JMenuItem exitMenuItem = new JMenuItem("Clear");
		exitMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// We don't want to accidently save over a previous design
				currentFile = null;
				
				landPanel.getLandGrid().clearData();
			}
		});
		return exitMenuItem;
	}
	
	/**
	 * Returns the help menu.
	 *
	 * @return the JMenu for the Help menu
	 */
	private static JMenu createHelpMenu()
	{
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JDialog about = new About(frame);
				about.setLocationRelativeTo(frame);
				about.setVisible(true);
			}
		});
		helpMenu.add(aboutMenuItem);
		
		return helpMenu;
	}
	
	private static String generateExportString()
	{
		return landPanel.getLandGrid().getCastle().getGridDataExport();
	}
	
	private static void importData(String text) throws UnsupportedVersionException
	{
		if (text == null || text.length() == 0)
			return;
			
		landPanel.getLandGrid().importData(text);
	}
}