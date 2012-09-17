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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author David Green
 */
public class LandGrid extends JPanel
{
	private static final int tileWidth = 14;
	private int gridOffsetX = 20;
	private int gridOffsetY = 20;

	private Castle castle = new Castle();
	
	private String coordinates = "[0, 0]";
	private BuildingType selectedBuilding = BuildingType.STONE_WALL;

	private static final Color GRASS = new Color(0, 125, 0);

	private Set<DesignListener> designListeners = new HashSet<DesignListener>();

	private int button = MouseEvent.NOBUTTON;
	private int[] mouseCoords = {0, 0};

	public LandGrid()
	{
		resetGridData();

		this.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseCoords = getCoords(e.getX(), e.getY());
				
				coordinates = "[" + mouseCoords[0] + ", " + mouseCoords[1] + "]";
				LandGrid.this.repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				mouseCoords = getCoords(e.getX(), e.getY());
				coordinates = "[" + mouseCoords[0] + ", " + mouseCoords[1] + "]";
				changeGridData(e);
				LandGrid.this.repaint();
			}
		});
		
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				button = e.getButton();
				changeGridData(e);
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				//We need to set this here for mouseDragged. Oddly e.getButton() returns nothing for the mouseDragged's event
				button = e.getButton();
				changeGridData(e);
			}
		});

		this.setMinimumSize(new Dimension(
			tileWidth*Castle.CASTLE_BOUNDRY_LENGTH + gridOffsetX,
			tileWidth*Castle.CASTLE_BOUNDRY_LENGTH + gridOffsetY));
	}

	public void addDesignListener(DesignListener designListener)
	{
		designListeners.add(designListener);
	}

	public void removeDesignListener(DesignListener designListener)
	{
		designListeners.remove(designListener);
	}
	
	public void setSelectedBuilding(BuildingType building)
	{
		if (building == null) throw new IllegalArgumentException("Invalid argument (null) to setSelectedBuilding");
		this.selectedBuilding = building;
	}

	public BuildingType getSelectedBuilding()
	{
		return selectedBuilding;
	}
	
	private void changeGridData(MouseEvent e)
	{
		mouseCoords = getCoords(e.getX(), e.getY());

		//If left click
		if (button == MouseEvent.BUTTON1)
		{
			if (isValidCoords(mouseCoords))
			{
				Dimension dimension = selectedBuilding.getDimension();
				int[] hotspot = selectedBuilding.getHotspot();

				Set<Point> buildingCoords = new HashSet<Point>();

				for (int i=mouseCoords[0] - hotspot[0]; i<mouseCoords[0] - hotspot[0] + dimension.getWidth(); i++)
				{
					for (int j=mouseCoords[1] - hotspot[1]; j<mouseCoords[1] - hotspot[1] + dimension.getHeight(); j++)
					{
						buildingCoords.add(new Point(i, j));
					}
				}
				castle.addBuilding(buildingCoords, selectedBuilding);
				
				LandGrid.this.repaint();
				notifyDesignListeners();
			}
		}
		else if (button == MouseEvent.BUTTON3) //else if right click
		{
			if (fitsInGrid(mouseCoords, new Dimension(1, 1), new int[] {0, 0}))
			{
				TileBuilding building = castle.getGridData(mouseCoords[0], mouseCoords[1]);
				if (building != null && building.getBuildingType() != BuildingType.KEEP)
				{
					castle.removeBuilding(building);
				}
				LandGrid.this.repaint();
				notifyDesignListeners();
			}
		}
	}

	/**
	 * Checks that these coords are valid for the selected building. Note
	 * that these will typically be the center of the building.
	 *
	 * @param coords
	 * @return 
	 */
	private boolean isValidCoords(int[] coords)
	{
		Dimension dimension = selectedBuilding.getDimension();
		int[] hotspot = selectedBuilding.getHotspot();
		
		if (fitsInGrid(coords, dimension, hotspot) &&
			isBuildable(coords, dimension, hotspot))
		{
			if (selectedBuilding.isGapRequired())
			{
				return isGapSatisfied(coords, dimension, hotspot);
			}
			else return true;
		}
		else return false;
	}
	
	private boolean fitsInGrid(int[] coords, Dimension dimension, int[] hotspot)
	{
		return (coords[0] - hotspot[0] >= 0 &&
			coords[0] - hotspot[0] <= Castle.CASTLE_BOUNDRY_LENGTH - dimension.getWidth() &&
			coords[1] - hotspot[1] >= 0 &&
			coords[1] - hotspot[1] <= Castle.CASTLE_BOUNDRY_LENGTH - dimension.getHeight());
	}
	
	private boolean isBuildable(int[] coords, Dimension dimension, int[] hotspot)
	{
		for (int i=coords[0] - hotspot[0]; i<coords[0] - hotspot[0] + dimension.getWidth(); i++)
		{
			for (int j=coords[1] - hotspot[1]; j<coords[1] - hotspot[1] +dimension.getHeight(); j++)
			{
				TileBuilding tileBuilding = castle.getGridData(i, j);
				
				if (tileBuilding != null &&
					tileBuilding.getBuildingType() != BuildingType.WOODEN_WALL &&
					tileBuilding.getBuildingType() != BuildingType.STONE_WALL)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isGapSatisfied(int[] coords, Dimension dimension, int[] hotspot)
	{
		for (int i=coords[0] - hotspot[0] -1; i<coords[0] - hotspot[0] +dimension.getWidth()+1; i++)
		{
			for (int j=coords[1] - hotspot[1] - 1; j<coords[1] - hotspot[1] +dimension.getHeight()+1; j++)
			{
				if (i >= 0 && i < Castle.CASTLE_BOUNDRY_LENGTH && j >= 0 && j < Castle.CASTLE_BOUNDRY_LENGTH)
				{
					TileBuilding tileBuilding = castle.getGridData(i, j);

					if (tileBuilding != null && tileBuilding.getBuildingType().isGapRequired()) return false;
				}
			}
		}
		return true;
	}
	
	private int[] getCoords(int ex, int ey)
	{
		return new int[] {(int)Math.floor(((double)(ex - gridOffsetX)) / tileWidth),
			(int)Math.floor(((double)(ey - gridOffsetY)) / tileWidth)};
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.drawString(coordinates, 10, 15);
		
		g.translate(gridOffsetX, gridOffsetY);
		
		//Draw any plain colours, e.g. grass & keep 
		for (int i=0; i<Castle.CASTLE_BOUNDRY_LENGTH; i++)
		{
			for (int j=0; j<Castle.CASTLE_BOUNDRY_LENGTH; j++)
			{
				TileBuilding tileBuilding = castle.getGridData(i, j);
				
				if (tileBuilding != null && tileBuilding.getBuildingType().getImage() == null)
				{
					//This is unlikely to be called now since everything has an image
					g.setColor(tileBuilding.getBuildingType().getColour());
				}
				else
				{
					//Fill with grass anyway since we might have transparent images (i.e. turret)
					g.setColor(GRASS);
				}
				
				g.fillRect(i*tileWidth, j*tileWidth, tileWidth, tileWidth);
			}
		}
		
		//Draw the grid
		g.setColor(Color.black);
		for (int i=0; i<Castle.CASTLE_BOUNDRY_LENGTH+1; i++)
		{
			g.drawLine(i*tileWidth, 0, i*tileWidth, tileWidth*Castle.CASTLE_BOUNDRY_LENGTH);
			g.drawLine(0, i*tileWidth, tileWidth*Castle.CASTLE_BOUNDRY_LENGTH, i*tileWidth);
		}

		//Draw the buildings
		Set<Integer> ids = new HashSet<Integer>();
		for (int i=0; i<Castle.CASTLE_BOUNDRY_LENGTH; i++)
		{
			for (int j=0; j<Castle.CASTLE_BOUNDRY_LENGTH; j++)
			{
				TileBuilding tileBuilding = castle.getGridData(i, j);
				if (tileBuilding != null)
				{
					if ((tileBuilding.getBuildingType().getImage() != null) &&
						!ids.contains(tileBuilding.getBuildingId()))
					{
						ids.add(tileBuilding.getBuildingId());
						g.drawImage(tileBuilding.getBuildingType().getImage(), i*tileWidth+1, j*tileWidth+1, null);
					}
				}
			}
		}

		//Draw the mouse-overlay
		Image overlay;
		if (isValidCoords(mouseCoords)) overlay = selectedBuilding.getValidOverlay();
		else overlay = selectedBuilding.getInvalidOverlay();

		int[] hotspot = selectedBuilding.getHotspot();

		g.drawImage(overlay, (mouseCoords[0] - hotspot[0])*tileWidth+1, (mouseCoords[1] - hotspot[1])*tileWidth+1, null);
	}

	/**
	 * Clears the data to leave just the Keep and empty land.
	 *
	 */
	public void clearData()
	{
		resetGridData();
		repaint();
	}

	private void resetGridData()
	{
		castle.resetGridData();
		notifyDesignListeners();
	}

	public void importData(String text)
	{
		castle.importData(text);
		notifyDesignListeners();
		
		this.repaint();
	}

	private void notifyDesignListeners()
	{
		for (DesignListener designListener : designListeners)
		{
			designListener.designChanged();
		}
	}

	public Castle getCastle()
	{
		return castle;
	}
}