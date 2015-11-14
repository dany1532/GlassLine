package gui.v2;
//Common Imports
import gui.panels.ControlPanel;
import gui.panels.FactoryPanel;

import gui.panels.subcontrolpanels.StatePanel;
import gui.panels.subcontrolpanels.TracePanel;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import shared.Recipe;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

//import factory.dexter.demo.src.opDataObject;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**********************************
- Inspired by: Tim Righettini and Factory Group 2012 (cs200)
**********************************/

/*****Updates - Kevin Schwindt*****/
/*
- Overhauled panel colors
*/


public class RecipeMaker extends v2Panel implements TReceiver{

	public ArrayList<ConveyorFamily> conveyorFams;
	public Transducer transducer;
	public ControlPanel controlPanel;
	public JPanel stateSliderPanel;
	public FactoryPanel factoryPanel;

	protected String employeeName; // Will be used to hold the name of the employee
	protected JPanel cardPanel = new JPanel(); // This will be used as a middle man to hold all card layouts
	protected CardLayout cl = new CardLayout();
	
	static int yOffset = -160;

	// For all Panels
	protected JLabel titleLabel = new JLabel("", JLabel.CENTER);

	// break panel Data
	protected JButton goBreakStuffButton = new JButton("Go Break Stuff");
	protected JButton returnFromBreakButton = new JButton("Return");
	
	public JComboBox breakerSelect = new JComboBox();
	public JComboBox breakerNumberSelect = new JComboBox();
	
		protected JPanel breakManagementPanel = new JPanel();

		protected JButton breakButton = new JButton("Do that Stuff");
		protected JComboBox breakFixSelect = new JComboBox();
		protected JComboBox breakFixSelect2 = new JComboBox();

		protected JScrollPane breakRecipeScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		protected JPanel breakRecipeScrollPanel = new JPanel();
		
	// First Panel Data
	protected JPanel RecipeManagementPanel = new JPanel();

	//protected JButton addNewRecipeButton = new JButton("Add New recipe");
	protected JLabel editRecipeLabel = new JLabel("Recipes:", JLabel.CENTER);
	protected JScrollPane editRecipeScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	protected JPanel editRecipeScrollPanel = new JPanel();

	// For editScrollPane
	protected ArrayList<JPanel> editRecipePanels = new ArrayList<JPanel>();
	protected ArrayList<JLabel> editRecipeNames = new ArrayList<JLabel>();
	protected ArrayList<JButton> editRecipeButtons = new ArrayList<JButton>();
	protected ArrayList<JButton> editRecipeDoButtons = new ArrayList<JButton>();

	// Second & Third Panel Data
	protected JPanel createRecipePanel = new JPanel();
	protected JPanel editRecipePanel = new JPanel();

	protected JLabel RecipeNameLabel = new JLabel("Name:", JLabel.LEFT); 
	protected JTextField RecipeNameTextField = new JTextField(30);

	protected JLabel RecipeDescriptionLabel = new JLabel("Recipe Description:", JLabel.LEFT); 
	protected JTextArea RecipeDescriptionTextField = new JTextArea(5,30);

	protected JPanel editTitlePanel = new JPanel();
	protected JPanel namePanel = new JPanel();

	// Panels specific to this manager
	protected JPanel recipeImagesLabelPanel = new JPanel();
	protected JPanel recipeImagesPanelContainer = new JPanel(); 
	protected JPanel recipeImagesPanel = new JPanel();
	protected JScrollPane recipeScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);   
	protected JPanel recipeScrollPanel = new JPanel();

	// For recipeScrollPane
	protected JLabel opRecipeLabel = new JLabel("Choose Operations for recipe:");
	protected ArrayList<JPanel> opRecipePanels = new ArrayList<JPanel>();
	protected ArrayList<JLabel> opRecipeNames = new ArrayList<JLabel>();
	protected ArrayList<JButton> opRecipeButtons = new ArrayList<JButton>();

	// Data specific to these four panels 
	protected JLabel recipeImagesLabel = new JLabel("Click to Remove Operation", JLabel.CENTER);
	protected ArrayList<JLabel> recipeImages = new ArrayList<JLabel>();

	// Bottom Panels
	protected JPanel descriptionPanel = new JPanel();
	protected JPanel editButtonsPanel = new JPanel();

	// For Modify recipe Panel Only
	protected JLabel editRecipePanelSubTitle = new JLabel("Edit existing recipe", JLabel.CENTER); 
	protected JButton modifyButton = new JButton("Modify");
	protected JButton deleteButton = new JButton("Delete");

	// For both panels
	protected JButton cancelButton = new JButton("Cancel");

	// Other data variables	

	protected String recipeImageName;
	protected int selectedRecipe;
	
	protected ArrayList<String> tempOps = new ArrayList<String>();
	protected ArrayList<String> operations = new ArrayList<String>();
	protected ArrayList<Recipe> recipes = new ArrayList<Recipe>();
	protected ArrayList<String> listnameImagerecipes = new ArrayList<String>();

	// Instantiate the test class 
	recipeManagerTestGUI pTest = new recipeManagerTestGUI();
	
	// New Border/Color Code for GUI overhaul
	Border border = BorderFactory.createMatteBorder(1,1,1,1,Color.black); // Regular Border for most panels
	Border whiteBorder = BorderFactory.createMatteBorder(2,2,2,2,Color.white); // Bigger border for more important Panels
	Border blackBorder = BorderFactory.createMatteBorder(2,2,2,2,Color.black); // Button Borders, doing this requires the MouseListener for mouseOver color changing
	Border blackBorderSmall = BorderFactory.createMatteBorder(1,1,1,1,Color.black); // Button Borders, doing this requires the MouseListener for mouseOver color changing

	//background -- JL
	Border redBorder = BorderFactory.createMatteBorder(2,2,2,2,Color.blue);
	
	Color selectedColor = Color.orange; // Buttons will turn into this color when the mouse hovers over them
	Color regularColor = Color.yellow; // Buttons will turn into this color when the mouse stops hovering over
	Color clickedColor = Color.black; // Buttons will turn into this color when the mouse is clicked
	ColorMouseListener cml = new ColorMouseListener(); // This will be used to swap button colors when a user is over it

	//background -- JL
	JPanel background1;
	JPanel background2;
	JPanel background3;
	JPanel first;
	JPanel second;
	JPanel third;
	JPanel breaker;
	

		
	public RecipeMaker(int xVal, int yVal, ArrayList<ConveyorFamily> convs, Transducer fTransducer, FactoryPanel fp) {
		
		transducer = fTransducer;
		transducer.register(this, TChannel.CONTROL_PANEL);
		//transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);

		
		factoryPanel = fp;
		controlPanel = new ControlPanel(factoryPanel,transducer);
		
		conveyorFams = convs;
		
		
		
		background1 = new JPanel();
		background1.setLayout(new FlowLayout());
		background1.setPreferredSize(new Dimension(500, 600));
		background1.setMinimumSize(new Dimension(500, 600));
		background1.setMaximumSize(new Dimension(500, 600));
		background1.add(new JLabel(new ImageIcon("imageicons/gui/glass.gif")));

		background2 = new JPanel();
		background2.setLayout(new FlowLayout());
		background2.setPreferredSize(new Dimension(500, 600));
		background2.setMinimumSize(new Dimension(500, 600));
		background2.setMaximumSize(new Dimension(500, 600));
		background2.add(new JLabel(new ImageIcon("imageicons/gui/Broken-Glass-Wallpaper.jpg")));
		
		background3 = new JPanel();
		background3.setLayout(new FlowLayout());
		background3.setPreferredSize(new Dimension(500, 600));
		background3.setMinimumSize(new Dimension(500, 600));
		background3.setMaximumSize(new Dimension(500, 600));
		background3.add(new JLabel(new ImageIcon("imageicons/gui/glass.gif")));

		cardPanel.setLayout(cl);
		
		buildFirstPanel();
		buildBreakerPanel();
		cl.show(cardPanel, "RecipeManagementPanel");

		buildSecondandThirdPanels(true);
		buildSecondandThirdPanels(false);

		gotoRecipePanel();

		//super.setTitle(super.employeeName);  JKEVIN
		super.requestFocusInWindow();	
		setUpScrollData();
		setUpColors();
		//pack(); JKEVIN
	}

	public void buildBreakerPanel() {
		
		breaker = new JPanel();
		breaker.setPreferredSize(new Dimension(500, 600));
		breaker.setMinimumSize(new Dimension(500, 600));
		breaker.setMaximumSize(new Dimension(500, 600));	
		breaker.setLayout(new GridBagLayout());
		

		
		breakButton.setPreferredSize(new Dimension(300, 50));
		breakButton.setMaximumSize(new Dimension(300, 50));
		breakButton.setMinimumSize(new Dimension(300, 50));

		breakFixSelect.setPreferredSize(new Dimension(250, 50));
		breakFixSelect.setMaximumSize(new Dimension(250, 50));
		breakFixSelect.setMinimumSize(new Dimension(250, 50));
		
		breakFixSelect2.setPreferredSize(new Dimension(250, 50));
		breakFixSelect2.setMaximumSize(new Dimension(250, 50));
		breakFixSelect2.setMinimumSize(new Dimension(250, 50));
		
		breakerSelect.setPreferredSize(new Dimension(250, 50));
		breakerSelect.setMaximumSize(new Dimension(250, 50));
		breakerSelect.setMinimumSize(new Dimension(250, 50));
		
		breakerNumberSelect.setPreferredSize(new Dimension(100, 100));
		breakerNumberSelect.setMaximumSize(new Dimension(100, 100));
		breakerNumberSelect.setMinimumSize(new Dimension(100, 100));

		breakRecipeScrollPane.setPreferredSize(new Dimension(450, 500 + yOffset));
		breakRecipeScrollPane.setMaximumSize(new Dimension(450, 500 + yOffset));
		breakRecipeScrollPane.setMinimumSize(new Dimension(450, 500 + yOffset));

		// Set text in applicable areas

		breakButton.setFont(new Font("Times New Roman", Font.PLAIN, 25));

		breakFixSelect.setFont(new Font("Times New Roman", Font.ITALIC, 24));

		breakFixSelect2.setFont(new Font("Times New Roman", Font.ITALIC, 24));

		breakButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		breakFixSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		breakFixSelect2.setAlignmentX(Component.CENTER_ALIGNMENT);

		breakRecipeScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		breakerSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		returnFromBreakButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		for (int i = 0; i < conveyorFams.size(); i++){
			breakerNumberSelect.addItem(i);
		}
		

		// Add Action Listeners for the buttons, even if they are recipe of the other panels
		breakButton.addActionListener(pTest);
		returnFromBreakButton.addActionListener(pTest);


		// Add components to the panel
		breakRecipeScrollPane.validate();

		breakManagementPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;

		breakManagementPanel.add(titleLabel, c);
		
		breakFixSelect.addItem("Break");
		breakFixSelect.addItem("Fix");
		
		breakFixSelect2.addItem("NA");
		breakFixSelect2.addItem("Glass");
		breakFixSelect2.addItem("Expectations");
		breakFixSelect2.addItem("Full");
		
		

		c.gridy = 0;
		c.weighty = 0;
		breakManagementPanel.add(returnFromBreakButton, c);
		
		c.gridy = 1;
		c.weighty = 0;
		breakManagementPanel.add(breakFixSelect, c);
		
		c.gridy = 2;
		c.weighty = 0;
		breakManagementPanel.add(breakFixSelect2, c);

		
		breakerSelect.addItem("Conveyor");
		breakerSelect.addItem("Popup");
		breakerSelect.addItem("Inline Machine");
		breakerSelect.addItem("Top Offline Machine");
		breakerSelect.addItem("Bottom Offline Machine");
		breakerSelect.addItem("Truck");

		
		
		c.gridy = 3;
		c.weighty = 0;
		breakManagementPanel.add(breakerSelect, c);
		
		
		c.gridy = 4;
		c.weighty = 0;
		breakManagementPanel.add(breakerNumberSelect, c);
		
		c.gridy = 5;
		c.weighty = 0;
		breakManagementPanel.add(breakButton, c);
		
		

//		c.gridy = 4;
//		c.weighty = 0;
//		breakManagementPanel.add(breakRecipeScrollPane, c);	

		
		//background -- JL
		breakManagementPanel.setOpaque(false);
		GridBagConstraints k = new GridBagConstraints();
		k.gridx = 0;
		k.gridy = 0;
		breaker.add(breakManagementPanel, k);
		breaker.add(background2, k);
		
		// Add this Panel into the Abstract Client card layout panel
		//super.cardPanel.add(RecipeManagementPanel, "RecipeManagementPanel");
		//background -- JL
		cardPanel.add(breaker, "breakManagementPanel");
	}
	
	public void setConveyorFam(ArrayList<ConveyorFamily> convs){
		conveyorFams = convs;
		
	}
	

	//TODO breakerSelect.getSelectedRecipe()
	public void buildFirstPanel() {
		//background -- JL
		first = new JPanel();
		first.setPreferredSize(new Dimension(500, 600));
		first.setMinimumSize(new Dimension(500, 600));
		first.setMaximumSize(new Dimension(500, 600));	
		first.setLayout(new GridBagLayout());
		
		// Set component sizes
		titleLabel.setPreferredSize(new Dimension(300, 100));
		titleLabel.setMaximumSize(new Dimension(300, 100));
		titleLabel.setMinimumSize(new Dimension(300, 100));	
		
		//background -- JL
		titleLabel.setOpaque(false);
		
//		addNewRecipeButton.setPreferredSize(new Dimension(300, 50));
//		addNewRecipeButton.setMaximumSize(new Dimension(300, 50));
//		addNewRecipeButton.setMinimumSize(new Dimension(300, 50));

		editRecipeLabel.setPreferredSize(new Dimension(250, 50));
		editRecipeLabel.setMaximumSize(new Dimension(250, 50));
		editRecipeLabel.setMinimumSize(new Dimension(250, 50));

		editRecipeScrollPane.setPreferredSize(new Dimension(450, 500 + yOffset));
		editRecipeScrollPane.setMaximumSize(new Dimension(450, 500 + yOffset));
		editRecipeScrollPane.setMinimumSize(new Dimension(450, 500 + yOffset));

		// Set text in applicable areas
		titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));

//		addNewRecipeButton.setFont(new Font("Times New Roman", Font.PLAIN, 25));

		editRecipeLabel.setFont(new Font("Times New Roman", Font.ITALIC, 24));

		// Set component alignment
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		//titleLabel.setBorder(border);

//		addNewRecipeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		editRecipeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		editRecipeScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Add Action Listeners for the buttons, even if they are recipe of the other panels
//		addNewRecipeButton.addActionListener(pTest);
		modifyButton.addActionListener(pTest);
		deleteButton.addActionListener(pTest);
		cancelButton.addActionListener(pTest);
		goBreakStuffButton.addActionListener(pTest);

		// Add components to the panel
		editRecipeScrollPane.validate();

		RecipeManagementPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;

		RecipeManagementPanel.add(titleLabel, c);

//		c.gridy = 1;
//		c.weighty = 1;
//		RecipeManagementPanel.add(addNewRecipeButton, c);
		
		
		

		c.gridy = 0;
		c.weighty = 0;
		RecipeManagementPanel.add(editRecipeLabel, c);

		c.gridy = 1;
		c.weighty = 0;
		RecipeManagementPanel.add(editRecipeScrollPane, c);	
		
		stateSliderPanel = (controlPanel.getStatePanel()).getStateSliderPanel();
				
		c.gridy = 2;
		c.weighty = 0;
		RecipeManagementPanel.add(controlPanel.getStatePanel(), c); //JKevin
		 
		c.gridy = 4;
		c.weighty = 1;
		RecipeManagementPanel.add(goBreakStuffButton, c);

		
		//background -- JL
		RecipeManagementPanel.setOpaque(false);
		GridBagConstraints k = new GridBagConstraints();
		k.gridx = 0;
		k.gridy = 0;
		first.add(RecipeManagementPanel, k);
		first.add(background1, k);
		
		// Add this Panel into the Abstract Client card layout panel
		//super.cardPanel.add(RecipeManagementPanel, "RecipeManagementPanel");
		//background -- JL
		cardPanel.add(first, "RecipeManagementPanel");
	}
	


	public void buildSecondandThirdPanels(boolean buildVal) {
		
		//background start -- JL
		second = new JPanel();
		second.setPreferredSize(new Dimension(500, 600));
		second.setMinimumSize(new Dimension(500, 600));
		second.setMaximumSize(new Dimension(500, 600));	
		second.setLayout(new GridBagLayout());
		third = new JPanel();
		third.setPreferredSize(new Dimension(500, 600));
		third.setMinimumSize(new Dimension(500, 600));
		third.setMaximumSize(new Dimension(500, 600));	
		third.setLayout(new GridBagLayout());
		
		//background end -- JL
		
		

		// Initialize any components
		RecipeNameTextField.setText("");
		RecipeDescriptionTextField.setLineWrap(true);

		// Create Sub Panels for all of the components 

		editTitlePanel.setLayout(new BoxLayout(editTitlePanel, BoxLayout.X_AXIS));
		editTitlePanel.add(editRecipePanelSubTitle);		

		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		namePanel.add(RecipeNameLabel);
		namePanel.add(RecipeNameTextField);


		recipeImagesLabelPanel.add(recipeImagesLabel);

		editButtonsPanel.setLayout(new BoxLayout(editButtonsPanel, BoxLayout.X_AXIS));
		editButtonsPanel.add(modifyButton);
		editButtonsPanel.add(deleteButton);
		if (buildVal == true) {
			editButtonsPanel.add(cancelButton);
		}

		// Set Fonts in applicable areas
		editRecipePanelSubTitle.setFont(new Font("Times New Roman", Font.ITALIC, 30));
		RecipeNameLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));

		recipeImagesLabel.setFont(new Font("Times New Roman", Font.ITALIC, 16));		
		opRecipeLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));

		RecipeDescriptionLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
		RecipeDescriptionTextField.setFont(new Font("Times New Roman", Font.PLAIN, 18));

		// editTitlePanel
		editTitlePanel.setPreferredSize(new Dimension(500, 60));
		editTitlePanel.setMaximumSize(new Dimension(500, 60));
		editTitlePanel.setMinimumSize(new Dimension(500, 60));

		editRecipePanelSubTitle.setPreferredSize(new Dimension(500, 50));
		editRecipePanelSubTitle.setMaximumSize(new Dimension(500, 50));
		editRecipePanelSubTitle.setMinimumSize(new Dimension(500, 50));

		// namePanel
		namePanel.setPreferredSize(new Dimension(490, 50));
		namePanel.setMaximumSize(new Dimension(490, 50));
		namePanel.setMinimumSize(new Dimension(490, 50));

		RecipeNameLabel.setPreferredSize(new Dimension(75, 25));
		RecipeNameLabel.setMaximumSize(new Dimension(75, 25));
		RecipeNameLabel.setMinimumSize(new Dimension(75, 25));

		RecipeNameTextField.setPreferredSize(new Dimension(300, 25));
		RecipeNameTextField.setMaximumSize(new Dimension(300, 25));
		RecipeNameTextField.setMinimumSize(new Dimension(300, 25));



		// recipeImagesLabelPanel
		recipeImagesLabelPanel.setPreferredSize(new Dimension(500, 50));
		recipeImagesLabelPanel.setMaximumSize(new Dimension(500, 50));
		recipeImagesLabelPanel.setMinimumSize(new Dimension(500, 50));

		recipeImagesLabel.setPreferredSize(new Dimension(500, 50));
		recipeImagesLabel.setMaximumSize(new Dimension(500, 50));
		recipeImagesLabel.setMinimumSize(new Dimension(500, 50));

		// recipeImagesPanel && recipeImagesPanelContainer
		recipeImagesPanelContainer.setPreferredSize(new Dimension(500, 100));
		recipeImagesPanelContainer.setMaximumSize(new Dimension(500, 100));
		recipeImagesPanelContainer.setMinimumSize(new Dimension(500, 100));

		recipeImagesPanelContainer.setLayout(new BoxLayout(recipeImagesPanelContainer, BoxLayout.Y_AXIS));
		// This will make sure that the recipeImagesPanel will align correctly w/in recipeImagesPanelContainer
		recipeImagesPanelContainer.add(recipeImagesPanel); // Add recipeImagesPanel

		recipeImagesPanel.setPreferredSize(new Dimension(400, 100));
		recipeImagesPanel.setMaximumSize(new Dimension(400, 100));
		recipeImagesPanel.setMinimumSize(new Dimension(400, 100));

		// Set the image ArrayList for the recipe manager
		recipeImages.clear();
		recipeImagesPanel.removeAll();
		recipeImagesPanel.validate();
		for (int i = 0; i < 10; i++) {
			recipeImages.add(new JLabel(new ImageIcon("imageicons/cutter/cutter001.png")));
			recipeImages.get(i).setBorder(blackBorderSmall);
			recipeImages.get(i).addMouseListener(pTest); // This will allow the JLabel to be clicked
			recipeImagesPanel.add(recipeImages.get(i));
		}
		// Set the layout for this op, specifically
		recipeImagesPanel.setLayout(new GridLayout(2,5));

		// recipeScrollPane && opRecipeLabel
		opRecipeLabel.setPreferredSize(new Dimension(490, 50));
		opRecipeLabel.setMaximumSize(new Dimension(490, 50));
		opRecipeLabel.setMinimumSize(new Dimension(490, 50));

		recipeScrollPane.setPreferredSize(new Dimension(500, 325 + yOffset));
		recipeScrollPane.setMaximumSize(new Dimension(500, 325 + yOffset));
		recipeScrollPane.setMinimumSize(new Dimension(500, 325 + yOffset));
		// Make sure to add the recipeScrollPanel into the scrollPane

		// descriptionPanel -- This will be saved for later if we need it		
		descriptionPanel.setPreferredSize(new Dimension(475, 260 + yOffset));
		descriptionPanel.setMaximumSize(new Dimension(475, 260 + yOffset));
		descriptionPanel.setMinimumSize(new Dimension(475, 260 + yOffset));

		RecipeDescriptionLabel.setPreferredSize(new Dimension(490, 50));
		RecipeDescriptionLabel.setMaximumSize(new Dimension(490, 50));
		RecipeDescriptionLabel.setMinimumSize(new Dimension(490, 50));

		RecipeDescriptionTextField.setPreferredSize(new Dimension(450, 250 + yOffset));
		RecipeDescriptionTextField.setMaximumSize(new Dimension(450, 250 + yOffset));
		RecipeDescriptionTextField.setMinimumSize(new Dimension(450, 250 + yOffset));

		cancelButton.setPreferredSize(new Dimension(100, 30));
		cancelButton.setMaximumSize(new Dimension(100, 30));
		cancelButton.setMinimumSize(new Dimension(100, 30));

		// editButtonsPanel		
		editButtonsPanel.setPreferredSize(new Dimension(400, 75));
		editButtonsPanel.setMaximumSize(new Dimension(400, 75));
		editButtonsPanel.setMinimumSize(new Dimension(400, 75));

		modifyButton.setPreferredSize(new Dimension(100, 30));
		modifyButton.setMaximumSize(new Dimension(100, 30));
		modifyButton.setMinimumSize(new Dimension(100, 30));

		deleteButton.setPreferredSize(new Dimension(100, 30));
		deleteButton.setMaximumSize(new Dimension(100, 30));
		deleteButton.setMinimumSize(new Dimension(100, 30));

		cancelButton.setPreferredSize(new Dimension(100, 30));
		cancelButton.setMaximumSize(new Dimension(100, 30));
		cancelButton.setMinimumSize(new Dimension(100, 30));

		// Set Alignment and borders
		// Panels
		editTitlePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		namePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		opRecipeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		recipeImagesLabelPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		recipeImagesPanelContainer.setAlignmentX(Component.RIGHT_ALIGNMENT);
		recipeScrollPane.setAlignmentX(Component.RIGHT_ALIGNMENT);

		descriptionPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		editButtonsPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		// Components
		RecipeDescriptionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);


		// Add components into panels		
		if (buildVal == false) {	
			// Set up the createRecipePanel
			createRecipePanel.setLayout(new BoxLayout(createRecipePanel, BoxLayout.Y_AXIS));

			createRecipePanel.add(namePanel);
			createRecipePanel.add(recipeImagesLabelPanel);
			createRecipePanel.add(recipeImagesPanelContainer);
			createRecipePanel.add(opRecipeLabel);
			createRecipePanel.add(recipeScrollPane);
			
			
			//background -- JL
			createRecipePanel.setOpaque(false);
			GridBagConstraints l = new GridBagConstraints();
			l.gridx = 0;
			l.gridy = 0;
			second.add(createRecipePanel, l);
			//second.add(background2, l);
			
			cardPanel.add(second, "createRecipePanel");
			//background end -- JL
			
			cl.show(cardPanel, "createRecipePanel");

		}

		else { // buildVal == true	
			editRecipePanel.setLayout(new BoxLayout(editRecipePanel, BoxLayout.Y_AXIS));

			editRecipePanel.add(editTitlePanel);
			editRecipePanel.add(namePanel);
			editRecipePanel.add(recipeImagesLabelPanel);
			editRecipePanel.add(recipeImagesPanelContainer);	
			editRecipePanel.add(opRecipeLabel);
			editRecipePanel.add(recipeScrollPane);
			
			editRecipePanel.add(editButtonsPanel);
			
			//background -- JL
			editRecipePanel.setOpaque(false);
			GridBagConstraints m = new GridBagConstraints();
			m.gridx = 0;
			m.gridy = 0;
			third.add(editRecipePanel, m);
			third.add(background3, m);
			
			cardPanel.add(third, "editRecipePanel");
			//background end -- JL
			
			cl.show(cardPanel, "editRecipePanel");
		}
	}

public void setUpScrollData() {
		// Make a list of all possible image names
		
	for (int i=0;i<10;i++){
			String x = new String();
			
			switch(i){
			case 0: x = "Cutter";
					break;
			case 1: x = "Breakout";
					break;
			case 2: x = "Manual Breakout";
			break;
			case 3: x = "Drill";
			break;
			case 4: x = "Cross Seamer";
			break;
			case 5: x = "Grinder";
			break;
			case 6: x = "Washer";
			break;
			case 7: x = "Painter";
			break;
			case 8: x = "UV Lamp";
			break;
			case 9: x = "Oven";
			break;
			default: x = "wtf";
			}
			
			listnameImagerecipes.add(x);
	}
		


		// Set all of the editRecipe Scrolling Data for the first panel

		// Set Box Layout for editRecipeScrollPanel
		editRecipeScrollPanel.setLayout(new BoxLayout(editRecipeScrollPanel, BoxLayout.Y_AXIS));

		
		// Make 6 sample recipes
		recipes = new ArrayList<Recipe>();
		
		for (Integer i = 0; i < 9; i++){
			Recipe r = new Recipe();
			r.setName("Recipe "+i.toString());
			recipes.add(r);
		}
		


		// Set up editScrollPane variables
		for (int i = 0; i < recipes.size(); i++) {
			// Set the JPanels for the editScrollPane
			editRecipePanels.add(new JPanel());
			editRecipePanels.get(i).setLayout(new BoxLayout(editRecipePanels.get(i), BoxLayout.X_AXIS));
			editRecipePanels.get(i).setBorder(border);
			// Make the sub-components of this JPanel
			editRecipeNames.add(new JLabel("  " + recipes.get(i).getName()));
			editRecipeButtons.add(new JButton("Edit"));
			editRecipeDoButtons.add(new JButton("Process"));
			// Add these sub-components into the JPanel
			editRecipePanels.get(i).add(editRecipeNames.get(i), "");
			editRecipePanels.get(i).add(editRecipeDoButtons.get(i), "");
			editRecipePanels.get(i).add(editRecipeButtons.get(i), "");
			// Add the JPanels into the scrollPane
			editRecipeScrollPanel.add(editRecipePanels.get(i), "");

			// Set any action listeners
			editRecipeButtons.get(i).addActionListener(pTest);			
			editRecipeDoButtons.get(i).addActionListener(pTest);
		}

		// Set Sizes of these components
		for (int i = 0; i < editRecipePanels.size(); i++) {
			editRecipePanels.get(i).setPreferredSize(new Dimension(400, 50));
			editRecipePanels.get(i).setMaximumSize(new Dimension(400, 50));
			editRecipePanels.get(i).setMinimumSize(new Dimension(400, 50));

			editRecipeNames.get(i).setPreferredSize(new Dimension(190, 25));
			editRecipeNames.get(i).setMaximumSize(new Dimension(190, 25));
			editRecipeNames.get(i).setMinimumSize(new Dimension(190, 25));

			editRecipeButtons.get(i).setPreferredSize(new Dimension(100, 25));
			editRecipeButtons.get(i).setMaximumSize(new Dimension(100, 25));
			editRecipeButtons.get(i).setMinimumSize(new Dimension(100, 25));
			
			editRecipeDoButtons.get(i).setPreferredSize(new Dimension(100, 25));
			editRecipeDoButtons.get(i).setMaximumSize(new Dimension(100, 25));
			editRecipeDoButtons.get(i).setMinimumSize(new Dimension(100, 25));
		}
		editRecipeScrollPane.getViewport().add(editRecipeScrollPanel);
		editRecipeScrollPane.getVerticalScrollBar().setUnitIncrement(10);

	
		recipeScrollPanel.setLayout(new BoxLayout(recipeScrollPanel, BoxLayout.Y_AXIS));
		



		// Set Sizes of these components
		for (int i = 0; i < opRecipePanels.size(); i++) {
			opRecipePanels.get(i).setPreferredSize(new Dimension(400, 50));
			opRecipePanels.get(i).setMaximumSize(new Dimension(400, 50));
			opRecipePanels.get(i).setMinimumSize(new Dimension(400, 50));

			opRecipeNames.get(i).setPreferredSize(new Dimension(290, 25));
			opRecipeNames.get(i).setMaximumSize(new Dimension(290, 25));
			opRecipeNames.get(i).setMinimumSize(new Dimension(290, 25));

			opRecipeButtons.get(i).setPreferredSize(new Dimension(100, 25));
			opRecipeButtons.get(i).setMaximumSize(new Dimension(100, 25));
			opRecipeButtons.get(i).setMinimumSize(new Dimension(100, 25));
		}
		recipeScrollPane.getViewport().add(recipeScrollPanel);
		recipeScrollPane.getVerticalScrollBar().setUnitIncrement(10);

	}

	public void updateScrollPanel() {


		// Clear editScrollPane variables
		editRecipeScrollPanel.removeAll();
		editRecipeScrollPanel.validate();

		editRecipePanels.clear();
		editRecipeNames.clear();
		editRecipeButtons.clear();
		editRecipeDoButtons.clear();

		// Set up editScrollPane variables
		for (int i = 0; i < recipes.size(); i++) {
			// Set the JPanels for the editScrollPane
			editRecipePanels.add(new JPanel());
			editRecipePanels.get(i).setLayout(new BoxLayout(editRecipePanels.get(i), BoxLayout.X_AXIS));
			editRecipePanels.get(i).setBorder(border);
			// Make the sub-components of this JPanel
			editRecipeNames.add(new JLabel("  " + recipes.get(i).getName()));
			editRecipeButtons.add(new JButton("Edit"));
			editRecipeDoButtons.add(new JButton("Do"));
			// Add these sub-components into the JPanel
			editRecipePanels.get(i).add(editRecipeNames.get(i), "");
			editRecipePanels.get(i).add(editRecipeDoButtons.get(i), "");
			editRecipePanels.get(i).add(editRecipeButtons.get(i), "");
			// Add the JPanels into the scrollPane
			editRecipeScrollPanel.add(editRecipePanels.get(i), "");

			// Set any action listeners
			editRecipeButtons.get(i).addActionListener(pTest); // Will allow the edit buttons to work			
			editRecipeDoButtons.get(i).addActionListener(pTest); // Will allow the edit buttons to work
		}

		// Set Sizes of these components
		for (int i = 0; i < editRecipePanels.size(); i++) {
			editRecipePanels.get(i).setPreferredSize(new Dimension(400, 50));
			editRecipePanels.get(i).setMaximumSize(new Dimension(400, 50));
			editRecipePanels.get(i).setMinimumSize(new Dimension(400, 50));

			editRecipeNames.get(i).setPreferredSize(new Dimension(100, 25));
			editRecipeNames.get(i).setMaximumSize(new Dimension(190, 25));
			editRecipeNames.get(i).setMinimumSize(new Dimension(190, 25));

			editRecipeButtons.get(i).setPreferredSize(new Dimension(100, 25));
			editRecipeButtons.get(i).setMaximumSize(new Dimension(100, 25));
			editRecipeButtons.get(i).setMinimumSize(new Dimension(100, 25));
			
			editRecipeDoButtons.get(i).setPreferredSize(new Dimension(100, 25));
			editRecipeDoButtons.get(i).setMaximumSize(new Dimension(100, 25));
			editRecipeDoButtons.get(i).setMinimumSize(new Dimension(100, 25));
		}
		editRecipeScrollPane.getViewport().add(editRecipeScrollPanel);
		editRecipeScrollPane.getVerticalScrollBar().setUnitIncrement(10);
		
		opRecipePanels.clear();
		opRecipeNames.clear();
		opRecipeButtons.clear();
		recipeScrollPanel.removeAll();
		
		for (int i = 0; i < operations.size(); i++) {
			// Set the JPanels for the editScrollPane
			opRecipePanels.add(new JPanel());
			opRecipePanels.get(i).setLayout(new BoxLayout(opRecipePanels.get(i), BoxLayout.X_AXIS));
			opRecipePanels.get(i).setBorder(border);
			// Make the sub-components of this JPanel
			opRecipeNames.add(new JLabel("  " + operations.get(i)));
			opRecipeButtons.add(new JButton("Select"));
			// Add these sub-components into the JPanel
			opRecipePanels.get(i).add(opRecipeNames.get(i), "");
			opRecipePanels.get(i).add(opRecipeButtons.get(i), "");
			// Add the JPanels into the scrollPane
			recipeScrollPanel.add(opRecipePanels.get(i), "");

			// Set any action listeners
			opRecipeButtons.get(i).addActionListener(pTest);
		}

		// Set Sizes of these components
		for (int i = 0; i < opRecipePanels.size(); i++) {
			opRecipePanels.get(i).setPreferredSize(new Dimension(400, 50));
			opRecipePanels.get(i).setMaximumSize(new Dimension(400, 50));
			opRecipePanels.get(i).setMinimumSize(new Dimension(400, 50));

			opRecipeNames.get(i).setPreferredSize(new Dimension(290, 25));
			opRecipeNames.get(i).setMaximumSize(new Dimension(290, 25));
			opRecipeNames.get(i).setMinimumSize(new Dimension(290, 25));

			opRecipeButtons.get(i).setPreferredSize(new Dimension(100, 25));
			opRecipeButtons.get(i).setMaximumSize(new Dimension(100, 25));
			opRecipeButtons.get(i).setMinimumSize(new Dimension(100, 25));
		}
		recipeScrollPane.getViewport().add(recipeScrollPanel);
		recipeScrollPane.getVerticalScrollBar().setUnitIncrement(10);
		
		setUpColors();

	}

	public void addOp(int index) { // This will add a op to the visual area showing the recipes on the GUI plus the arrayList of ops to be added to a new or edited recipe
		if (tempOps.size() == 10) {
			System.out.println("recipe is already full of ops!");
			return;
		}
		// Add this op to the arrayList
		tempOps.add(operations.remove(index));
		// Add this op to the GUI image area 
		//recipeImages.get(tempOps.size() - 1).setIcon(new ImageIcon(operations.get(index)));
	
			recipeImages.get(tempOps.size() - 1).setIcon(new ImageIcon("imageicons/gui/"+tempOps.get(tempOps.size()-1)+".png"));
			updateScrollPanel();
		

	}	

	public void deleteOp(int index) { // This will remove a op from the visual area showing the recipes on the GUI plus the arrayList of ops to be added to a new or edited recipe
		if (index > tempOps.size() - 1) {
			System.out.println("Cannot delete an op that does not exist...");
			return;
		}		
		// Remove this op from the arrayList
		operations.add(tempOps.remove(index));
		System.out.println("op at " + index + " removed!");
		// Remove this op from the GUI image area 
		recipeImages.remove(index);
		// Add an extra space to the back of the list, so the arrayList size is always 8, and then add an mouse listener to this JLabel
		recipeImages.add(new JLabel(new ImageIcon("")));
		recipeImages.get(recipeImages.size() - 1).addMouseListener(pTest); // This will allow the JLabel to be clicked
		recipeImages.get(recipeImages.size() - 1).setBorder(border); // This put a border around the JLabel
		// Reset the images panel so that nothing weird happens
		// Set the image ArrayList for the recipe manager
		recipeImagesPanel.removeAll();
		recipeImagesPanel.validate();
		for (int i = 0; i < 10; i++) {
			recipeImagesPanel.add(recipeImages.get(i));
		}
		recipeImagesPanel.validate();
		// Set the layout for this op, specifically
		recipeImagesPanel.setLayout(new GridLayout(2,5)); //was 2, 4 TODO
		//opRecipePanelRefresh();
		updateScrollPanel();
	}

	public void createRecipe(String n, Boolean[] r) {
		// Create the new recipe
		Recipe rec = new Recipe();
		rec.setRecipe(n, r);
		recipes.add(rec);
		System.out.println("Recip added at index: " + (recipes.size() - 1));
		// Add it to the editScrollPane		
		updateScrollPanel();
	} // Send object op to be saved on the server.

	public void editRecipe(String n, Boolean r[], int index) {
		System.out.println("Editing recipe at index: " + index);
		recipes.get(index).setRecipe(n,r);
		recipes.get(index).getOpsList();
		updateScrollPanel();
	}

	public void deleteRecipe(int index) {
		System.out.println("Deleting recipe at index: " + index);
		recipes.remove(index);
		System.out.println("Send this deleted object to server: ");
		updateScrollPanel();
	} 

	public void sendRecipe(int index){
		Object[] args = new Object[1];
		args[0] = recipes.get(index);
		
		transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, args);

		//Send this to factory!!TODO
		//transducer.fireEvent(TChannel.CONTROL_PANEL, TEvent.START, null);
		System.out.println(recipes.get(index).getOpsList()); //won't need to get ops list, but just send recipe
	}
	public boolean verifyInformation(String val) {
		try {
			// This will create a pattern to see if characters are alphanumeric	-- underscores and spaces are allowed					
			Pattern pat = Pattern.compile("^[a-zA-Z0-9_ ]+$"); 

			// This will check to make sure that names are alphanumeric							
			Matcher match = pat.matcher(val);

			if (!match.find()) {
				throw new Exception("String needs to be alphanumeric: " + val);
			}

			// Name is alphanumeric -- return true
			return true;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			// Name is not alphanumeric -- return false
			return false;
		}	
	} // Verify that user has inputted correct data. For instance, is there a name in the JTextfield?

	public void gotoCreateRecipePanel() {
		// Reset the tempOps arrayList
		//tempOps = new 
		operations = listnameImagerecipes;// Kevin TODO
		
		buildSecondandThirdPanels(false);
		// This will make all GUI fields blank
		// Set the TF
		RecipeNameTextField.setText("");

		// Set the description
		RecipeDescriptionTextField.setText("");	
	} // Displays the createRecipePanel.

	public void gotoeditRecipePanel(int index) {
		// Get the ops from the selected recipe
		tempOps.clear(); //Kevin
		operations.clear();

		buildSecondandThirdPanels(true);
		// The index will set the GUI panel components to the appropriate information based upon the op picked
		RecipeNameTextField.setText(recipes.get(index).getName()); // Set the name TF

		// Remember what recipe was selected
		selectedRecipe = index;

		// Set the images for the GUI to that of the selected recipe
		tempOps = recipes.get(selectedRecipe).getOpsList();
		System.out.println("getting truops from index: "+selectedRecipe);
		operations = recipes.get(selectedRecipe).getNotOpsList();

		// Set the images for the GUI to the ops in tempOps
		recipeImages.clear();
		recipeImagesPanel.removeAll();
		recipeImagesPanel.validate();
		for (int i = 0; i < 10; i++) {
			if (i < tempOps.size()) {
				recipeImages.add(new JLabel(new ImageIcon("imageicons/gui/"+tempOps.get(i)+".png"))); //TODO new ImageIcon(tempOps.get(i))));
				System.out.print("image: "+tempOps.get(i));
			}
			else {
				recipeImages.add(new JLabel(new ImageIcon("")));
			}
			recipeImages.get(i).setOpaque(true);
			recipeImages.get(i).setBackground(Color.black);
			recipeImages.get(i).setBorder(blackBorderSmall);
			recipeImages.get(i).addMouseListener(pTest); // This will allow the JLabel to be clicked
			recipeImagesPanel.add(recipeImages.get(i));
		}
		// Set the layout for this op, specifically
		recipeImagesPanel.setLayout(new GridLayout(2,5));//was 2,4 Kevin
		recipeImagesPanel.validate();
		updateScrollPanel(); //Kevin TODO changed
	} // Displays the editRecipePanel.

	public void gotoRecipePanel() {
		setUpColors(); //Kevin ? TODO
		cl.show(cardPanel, "RecipeManagementPanel");
	} // Displays RecipeManagementPanel 
	
	public void gotoBreakPanel() {
		setUpColors(); //Kevin ? TODO
		cl.show(cardPanel, "breakManagementPanel");
	} // Displays RecipeManagementPanel 
	
	public void setUpColors() {
		// Set Panel Background Colors
		//RecipeManagementPanel.setBackground(Color.blue);
		editRecipeScrollPane.setBackground(Color.blue);
		editRecipeScrollPanel.setBackground(Color.blue);
		
		for (int i = 0; i < editRecipePanels.size(); i++) {
			editRecipePanels.get(i).setBackground(Color.blue);
		}

		editRecipePanel.setBackground(Color.blue);

		//editTitlePanel.setBackground(Color.blue);
		editTitlePanel.setOpaque(false);
		//JL
		//namePanel.setBackground(Color.blue);
		namePanel.setOpaque(false);
		//recipeIdPanel.setBackground(Color.blue);
		//recipeImagesLabelPanel.setBackground(Color.blue);
		recipeImagesLabelPanel.setOpaque(false);
		//recipeImagesPanelContainer.setBackground(Color.blue);
		recipeImagesPanelContainer.setOpaque(false);
		recipeImagesPanel.setBackground(Color.black);
		
		recipeScrollPane.setBackground(Color.blue);
		recipeScrollPanel.setBackground(Color.blue);
		
		for (int i = 0; i < opRecipePanels.size(); i++) {
			opRecipePanels.get(i).setBackground(Color.blue);
		}
		
		//JL
		//descriptionPanel.setBackground(Color.blue);
		descriptionPanel.setOpaque(false);
		//createButtonsPanel.setBackground(Color.blue);
		//editButtonsPanel.setBackground(Color.blue);
		editButtonsPanel.setOpaque(false);


		// Set Panel/Label non-black border Colors

		
		// Set Label Text Colors
		titleLabel.setForeground(Color.black);
		editRecipeLabel.setForeground(Color.black);
		
		editRecipePanelSubTitle.setForeground(Color.black);
		
		RecipeNameLabel.setForeground(Color.black);
		RecipeDescriptionLabel.setForeground(Color.black);
		
		recipeImagesLabel.setForeground(Color.black);
		opRecipeLabel.setForeground(Color.black);
		
		for (int i = 0; i < editRecipeNames.size(); i++) {
			editRecipeNames.get(i).setForeground(Color.black);			
		}
		
		for (int i = 0; i < opRecipeNames.size(); i++) {
			opRecipeNames.get(i).setForeground(Color.black);			
		}
		
//		// Set Button/Textfield Colors		
////		addNewRecipeButton.setBackground(Color.yellow);
//		//background -- JL
//		Border redBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.blue);
//		addNewRecipeButton.setBorder(redBorder);
//		addNewRecipeButton.setForeground(Color.blue);
//		//addNewRecipeButton.setBorder(blackBorder);
//		addNewRecipeButton.addMouseListener(cml);

		
		cancelButton.setBackground(Color.yellow);
		//background -- JL
		//cancelButton.setBorder(blackBorder);
		cancelButton.setBorder(redBorder);
		cancelButton.setForeground(Color.blue);
		cancelButton.addMouseListener(cml);
		
		modifyButton.setBackground(Color.yellow);
		//background -- JL
		//modifyButton.setBorder(blackBorder);
		modifyButton.setBorder(redBorder);
		modifyButton.setForeground(Color.blue);
		modifyButton.addMouseListener(cml);
		
		deleteButton.setBackground(Color.yellow);
		//background -- JL
		//deleteButton.setBorder(blackBorder);
		deleteButton.setBorder(redBorder);
		deleteButton.setForeground(Color.blue);
		deleteButton.addMouseListener(cml);
		
		for (int i = 0; i < editRecipeButtons.size(); i++) {
			editRecipeButtons.get(i).setBackground(Color.yellow);
			editRecipeButtons.get(i).setBorder(blackBorder);
			editRecipeButtons.get(i).addMouseListener(cml);
			editRecipeButtons.get(i).setOpaque(true);
		}
		
		for (int i = 0; i < editRecipeDoButtons.size(); i++) {
			editRecipeDoButtons.get(i).setBackground(Color.yellow);
			editRecipeDoButtons.get(i).setBorder(blackBorder);
			editRecipeDoButtons.get(i).addMouseListener(cml);
			editRecipeDoButtons.get(i).setOpaque(true);
		}
		
		for (int i = 0; i < opRecipeButtons.size(); i++) {
			opRecipeButtons.get(i).setBackground(Color.yellow);
			opRecipeButtons.get(i).setBorder(blackBorder);
			opRecipeButtons.get(i).addMouseListener(cml);
			opRecipeButtons.get(i).setOpaque(true);
		}
	}
	private class ColorMouseListener implements MouseListener { // This will change the color of a button when a user mouses over it, and then change it back when the user exits
		public void mouseEntered(MouseEvent me) { // Change the button to the selected color
			JButton button = (JButton) me.getSource();
			button.setBackground(selectedColor);
		}

		public void mouseExited(MouseEvent me) { // Change the button back to regular color
			JButton button = (JButton) me.getSource();
			button.setBackground(regularColor);
		}

		public void mouseClicked(MouseEvent me) {}
		
		public void mousePressed(MouseEvent me) {
			JButton button = (JButton) me.getSource();
			button.setBackground(clickedColor);
		}
		
		public void mouseReleased(MouseEvent me) {
			JButton button = (JButton) me.getSource();
			if ( (me.getX() >= 0 && me.getX() < button.getWidth()) && (me.getY() > 0 && me.getY() < button.getHeight()) ) {
				button.setBackground(selectedColor);
			}
			else {
				button.setBackground(regularColor);
			}
		}		
	}
	
	public void displayClient(int xVal, int yVal) { // Will set the window specifications as attained from the subClient

		cardPanel.setLayout(cl);
		this.add(cardPanel);
		JFrame jf = new JFrame();
		jf.add(this);
		jf.setSize(xVal, yVal);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(false);
	}
	
	private class recipeManagerTestGUI implements ActionListener, MouseListener {

		public void actionPerformed(ActionEvent ae) {
//			if (ae.getSource() == addNewRecipeButton) {
//				System.out.println("addNewRecipeButton press detected!");
//				gotoCreateRecipePanel();
//				return;
//			}

			for (int i = 0; i < editRecipeButtons.size(); i++) { // Will cycle through any of the recipe edit buttons to see if one has been clicked
				if (ae.getSource() == editRecipeButtons.get(i)) {
					gotoeditRecipePanel(i);
					return;
				}
			}
			
			for (int i = 0; i < editRecipeDoButtons.size(); i++) { // Will cycle through any of the recipe edit buttons to see if one has been clicked
				if (ae.getSource() == editRecipeDoButtons.get(i)) {
					sendRecipe(i);
					return;
				}
			}

			for (int i = 0; i < opRecipeButtons.size(); i++) {// Will cycle through any of the op select buttons to see if one has been clicked
				if (ae.getSource() == opRecipeButtons.get(i)) {
					System.out.println("opRecipeButton press detected at index: " + i + "!");
					addOp(i);
					return;
				}
			}
			
			if (ae.getSource() == goBreakStuffButton) {
				System.out.println("goBreakStuffButton press detected!");
				
				gotoBreakPanel();
				
				return;
			}
			
			if (ae.getSource() == returnFromBreakButton) {
				System.out.println("returnFromBreakButton press detected!");
				
				gotoRecipePanel();
				
				return;
			}
			
			if (ae.getSource() == breakButton) {
				System.out.println("breakButton press detected!");
				
				String breakFixText = (String) breakFixSelect.getSelectedItem();
				String breakFix2Text = (String) breakFixSelect2.getSelectedItem();
				String breakerSelText = (String) breakerSelect.getSelectedItem();
				Integer breakerNumber = (Integer) breakerNumberSelect.getSelectedItem();
				
				System.out.println(breakFixText + " "+ breakerSelText+" "+breakerNumber);
				doThisStuff(breakFixText, breakFix2Text, breakerSelText, breakerNumber);
				
				return;
			}

			if (ae.getSource() == cancelButton) {
				System.out.println("cancelButton press detected!");

				gotoRecipePanel();
				return;
			}

			if (ae.getSource() == modifyButton) {
				System.out.println("modifyButton press detected!");
				if (verifyInformation(RecipeNameTextField.getText()) == false) { // Information is not correct, try again
					System.out.println("Recipe Name not alphanumeric, try again!");
				}
				else { // Edit the op
					Boolean[] tempRecipeBool = createTheBool(tempOps);
						editRecipe(RecipeNameTextField.getText(), tempRecipeBool, selectedRecipe); //Kevin TODO
						gotoRecipePanel();
					}

				
				return;
			}

			if (ae.getSource() == deleteButton) {
				System.out.println("deleteButton press detected!");
				// Delete the appropriate op
				deleteRecipe(selectedRecipe);
				gotoRecipePanel();
				return;
			}			
		}

		public void doThisStuff(String bf,String bf2, String thePart,
				Integer num) {
			ConveyorFamily cf = conveyorFams.get(num);
			//ConveyorFamily cf = conveyorFams.get(5); //Dany hack
				if (thePart == "Conveyor"){
					if (bf == "Break")
						if (bf2.equals("Full"))
							cf.msgSetConveyorTooFull();
						else
							cf.msgStopConveyor();
					else if (bf == "Fix")
						cf.msgStartConveyor();
				}
				else if (thePart == "Popup"){
					if (bf == "Break")
						cf.msgStopPopup();
					else if (bf == "Fix")
						cf.msgStartPopup();
				}
				else if (thePart == "Inline Machine"){
					if (bf == "Break"){
						if (bf2 == "Expectations")
							cf.msgOnlineMachineExpectationFailure();
						else if (bf2 == "NA")
							cf.msgStopOnlineMachine();
					}
					else if (bf == "Fix"){
						if (bf2 == "Expectations")
							cf.msgOnlineMachineFixExpectationFailure();
						else if (bf2 == "NA")
							cf.msgStartOnlineMachine();
					}
				}
				else if (thePart == "Top Offline Machine"){
					if (bf == "Break"){
						if (bf2 == "Glass")
							cf.msgTopMachineBreaksGlass();
						else if (bf2 == "Expectations"){
							cf.msgOfflineMachineExpectationFailure();
						}
						else if (bf2 == "NA")
							cf.msgTopOfflineMachineTurnOff();
					}
					else if (bf == "Fix"){
						if (bf2 == "Glass")
							cf.msgTopMachineFixGlass();
						else if (bf2 == "Expectations")
							cf.msgOfflineMachineFixExpectationFailure();
						else if (bf2 == "NA")
							cf.msgTopOfflineMachineTurnOn();
					}
				}
				else if (thePart == "Bottom Offline Machine"){
					if (bf == "Break"){
						if (bf2 == "Glass")
							cf.msgBottomMachineBreaksGlass();
						else if (bf2 == "Expectations")
							cf.msgOfflineMachineExpectationFailure();
						else if (bf2 == "NA")
							cf.msgBottomOfflineMachineTurnOff();
					}
					else if (bf == "Fix"){
						if (bf2 == "Glass")
							cf.msgBottomMachineFixGlass();
						else if (bf2 == "Expectations")
							cf.msgOfflineMachineFixExpectationFailure();
						else if (bf2 == "NA")
							cf.msgBottomOfflineMachineTurnOn();
					}
					
					else if (thePart == "Truck"){
						cf = conveyorFams.get(8);// No clue if this is truck position
					 
						if (bf == "Break")
							cf.msgStopConveyor(); //I think?

						else if (bf == "Fix")
							cf.msgStartConveyor(); //?
					}
				}

			
		}

		public void mouseClicked(MouseEvent e) { // Will detect the clicks of JLabels (very nice)
			for (int i = 0; i < 10; i++) {
				if (e.getSource() == recipeImages.get(i)) {
					System.out.println("JLabel " + i + " clicked!");
					deleteOp(i);
					break;
				}
			}			
		}

		// None of the below events will be used
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}		
	}
	
	public Boolean[] createTheBool(ArrayList<String> ops){
		Boolean [] tempRecipe = new Boolean[10];

		for (int i = 0; i < 10; i++){
			tempRecipe[i] = new Boolean(false);
		}
		System.out.println("ops.size: "+ops.size());
		for (int i=0;i<ops.size();i++){
			String x = new String();
			for (int j = 0; j<10; j++){
				
			
				switch(j){
				case 0: x = "Cutter";
						break;
				case 1: x = "Breakout";
						break;
				case 2: x = "Manual Breakout";
				break;
				case 3: x = "Drill";
				break;
				case 4: x = "Cross Seamer";
				break;
				case 5: x = "Grinder";
				break;
				case 6: x = "Washer";
				break;
				case 7: x = "Painter";
				break;
				case 8: x = "UV Lamp";
				break;
				case 9: x = "Oven";
				break;
				default: x = "wtf";
				}
			
			
				if (ops.get(i) == x){
					tempRecipe[j] = new Boolean(true);
					System.out.println("added "+x+" to tempRecipe");
				}
			}
			
		}
		return tempRecipe;
	}

	
	@Override
	public synchronized void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (channel == TChannel.CONTROL_PANEL){
			
			if (event == TEvent.START){
				
				// JUSTIN changed this. The create part call is now up in the 'sendRecipe' function, to allow me to actually send a recipe
				
				// look at 'current recipe'? and make that many new parts?
				//transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);
				
				//System.err.println("here.. do I want to be? Justin debug");
			}
		}
	}
}