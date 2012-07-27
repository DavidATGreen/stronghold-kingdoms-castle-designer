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

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author David Green
 */
public class About extends JDialog
{
	private JButton closeButton;
	private JLabel programTitle;
	private JTextField projectURL;
	private JLabel versionTitleLabel;
	private JLabel versionDateTitleLabel;
	private JLabel authorTitleLabel;
	private JLabel artistTitleLabel;
	private JLabel versionLabel;
	private JLabel versionDateLabel;
	private JLabel authorLabel;
	private JLabel artistLabel;
	
	public About(Frame owner)
	{
		super(owner);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		versionTitleLabel = new JLabel("Version:");
		versionDateTitleLabel = new JLabel("Date:");
		authorTitleLabel = new JLabel("Author:");
		artistTitleLabel = new JLabel("Artwork By:");
		programTitle = new JLabel("Stronghold Kingdoms Castle Designer");
		projectURL = new JTextField("http://code.google.com/p/stronghold-kingdoms-castle-designer/");
		projectURL.setEditable(false);
		projectURL.setBorder(null);
		projectURL.setBackground(null);
		projectURL.setCaretPosition(0);
		versionLabel = new JLabel(Editor.programVersion);
		versionDateLabel = new JLabel("27th July 2012");
		authorLabel = new JLabel("David Green (tempestua)");
		artistLabel = new JLabel("DavidSpy");

		setFonts();

		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).
				addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(programTitle)
					.addComponent(projectURL)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(versionTitleLabel)
							.addComponent(versionDateTitleLabel)
							.addComponent(authorTitleLabel)
							.addComponent(artistTitleLabel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(versionLabel)
							.addComponent(versionDateLabel)
							.addComponent(authorLabel)
							.addComponent(artistLabel))
						)
					)
				.addComponent(closeButton)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(programTitle)
				.addComponent(projectURL)
				.addGap(15)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(versionTitleLabel)
					.addComponent(versionLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(versionDateTitleLabel)
					.addComponent(versionDateLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(authorTitleLabel)
					.addComponent(authorLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(artistTitleLabel)
					.addComponent(artistLabel))
				.addGap(10)
				.addComponent(closeButton)
		);
		pack();

		getRootPane().setDefaultButton(closeButton);
	}

	private void setFonts()
	{
		versionTitleLabel.setFont(new Font(versionTitleLabel.getFont().getName(), Font.BOLD, versionTitleLabel.getFont().getSize()));
		versionDateTitleLabel.setFont(new Font(versionDateTitleLabel.getFont().getName(), Font.BOLD, versionDateTitleLabel.getFont().getSize()));
		authorTitleLabel.setFont(new Font(authorTitleLabel.getFont().getName(), Font.BOLD, authorTitleLabel.getFont().getSize()));
		artistTitleLabel.setFont(new Font(artistTitleLabel.getFont().getName(), Font.BOLD, artistTitleLabel.getFont().getSize()));
		programTitle.setFont(new Font(programTitle.getFont().getName(), Font.BOLD, programTitle.getFont().getSize()+4));
		projectURL.setFont(new Font(projectURL.getFont().getName(), Font.PLAIN, projectURL.getFont().getSize()));
		versionLabel.setFont(new Font(versionLabel.getFont().getName(), Font.PLAIN, versionLabel.getFont().getSize()));
		versionDateLabel.setFont(new Font(versionDateLabel.getFont().getName(), Font.PLAIN, versionDateLabel.getFont().getSize()));
		authorLabel.setFont(new Font(authorLabel.getFont().getName(), Font.PLAIN, authorLabel.getFont().getSize()));
		artistLabel.setFont(new Font(artistLabel.getFont().getName(), Font.PLAIN, artistLabel.getFont().getSize()));
	}
}