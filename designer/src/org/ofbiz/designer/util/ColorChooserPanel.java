package org.ofbiz.designer.util;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class ColorChooserPanel extends WFPanel{
	public static final int defaultHeight = 75;
	private final int sliderHeight = 22;
	private final int componentSpacingY = 4;
	private final int componentSpacingX = 10;
	private final int margin = 0;

	private JSlider redSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
	private JSlider greenSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
	private JSlider blueSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);

	private JPanel colorPane;

	public ColorChooserPanel(Dimension dimension, Color originalColor){
		int xCood = margin;
		int yCood = margin;

		setLayout(null);
		Dimension sliderDimension = new Dimension(dimension.width/2, sliderHeight);
		Dimension labelDimension = new Dimension(dimension.width/10, sliderHeight);
		Dimension colorPanelDimension = new Dimension(dimension.width/10, dimension.width/10);

		JLabel redLabel = new JLabel("Red", SwingConstants.RIGHT);
		JLabel greenLabel = new JLabel("Green", SwingConstants.RIGHT);
		JLabel blueLabel = new JLabel("Blue", SwingConstants.RIGHT);
		redLabel.setBounds(xCood, yCood, labelDimension.width, labelDimension.height);
		add(redLabel);
		yCood += sliderDimension.height + componentSpacingY;
		greenLabel.setBounds(xCood, yCood, labelDimension.width, labelDimension.height);
		add(greenLabel);
		yCood += sliderDimension.height + componentSpacingY;
		blueLabel.setBounds(xCood, yCood, labelDimension.width, labelDimension.height);
		add(blueLabel);
		yCood = margin;

		xCood += labelDimension.width + componentSpacingX;
		JPanel redPanel = new JPanel(new GridLayout(1, 1, 0, 0));
		redPanel.add(redSlider);
		redPanel.setBounds(xCood, yCood, sliderDimension.width, sliderDimension.height);
		add(redPanel);
		yCood += sliderDimension.height + componentSpacingY;
		JPanel greenPanel = new JPanel(new GridLayout(1, 1, 0, 0));
		greenPanel.add(greenSlider);
		greenPanel.setBounds(xCood, yCood, sliderDimension.width, sliderDimension.height);
		add(greenPanel);
		yCood += sliderDimension.height + componentSpacingY;
		JPanel bluePanel = new JPanel(new GridLayout(1, 1, 0, 0));
		bluePanel.add(blueSlider);
		bluePanel.setBounds(xCood, yCood, sliderDimension.width, sliderDimension.height);
		add(bluePanel);
		yCood = margin;

		xCood += sliderDimension.width + componentSpacingX;
		colorPane = new JPanel();
		colorPane.setBounds(xCood, margin, colorPanelDimension.width, colorPanelDimension.height);
		setColor(originalColor);
		add(colorPane);

		redSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
			Color color = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
			colorPane.setBackground(color);
			colorPane.repaint();
			}
			});

		greenSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
			Color color = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
			colorPane.setBackground(color);
			colorPane.repaint();
			}
			});

		blueSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
			Color color = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
			colorPane.setBackground(color);
			colorPane.repaint();
			}
			});
	}


	public Color getColor(){
		return new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
	}

	public void setColor(Color color){
		colorPane.setBackground(color);
		colorPane.repaint();
		redSlider.setValue(color.getRed());
		greenSlider.setValue(color.getGreen());
		blueSlider.setValue(color.getBlue());
		repaint();
	}
}

