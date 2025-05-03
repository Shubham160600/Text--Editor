package TextEditor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.*;

public class SimpleTextEditor extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private String currentFile = "Untitled";
    private boolean changed = false;
    
    public SimpleTextEditor() {
        // Set up the main frame
        super("Simple Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Add window listener to handle closing with unsaved changes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmSave()) {
                    System.exit(0);
                }
            }
        });
        
        // Initialize the text area with scrolling
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        // Add document listener to track changes
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                changed = true;
                updateTitle();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                changed = true;
                updateTitle();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                changed = true;
                updateTitle();
            }
        });
        
        // Add the text area to a scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Initialize file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        
        // Create menu bar
        createMenuBar();
        
        // Update the title
        updateTitle();
        
        // Display the frame
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMenuItem.addActionListener(e -> newFile());
        
        JMenuItem openMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.addActionListener(e -> openFile());
        
        JMenuItem saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.addActionListener(e -> saveFile(false));
        
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.addActionListener(e -> saveFile(true));
        
        JMenuItem printMenuItem = new JMenuItem("Print", KeyEvent.VK_P);
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        printMenuItem.addActionListener(e -> printFile());
        
        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(e -> {
            if (confirmSave()) {
                System.exit(0);
            }
        });
        
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(printMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem cutMenuItem = new JMenuItem("Cut", KeyEvent.VK_T);
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutMenuItem.addActionListener(e -> textArea.cut());
        
        JMenuItem copyMenuItem = new JMenuItem("Copy", KeyEvent.VK_C);
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyMenuItem.addActionListener(e -> textArea.copy());
        
        JMenuItem pasteMenuItem = new JMenuItem("Paste", KeyEvent.VK_P);
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteMenuItem.addActionListener(e -> textArea.paste());
        
        JMenuItem selectAllMenuItem = new JMenuItem("Select All", KeyEvent.VK_A);
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllMenuItem.addActionListener(e -> textArea.selectAll());
        
        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.addSeparator();
        editMenu.add(selectAllMenuItem);
        
        // Format menu
        JMenu formatMenu = new JMenu("Format");
        formatMenu.setMnemonic(KeyEvent.VK_O);
        
        JMenuItem wordWrapMenuItem = new JMenuItem("Word Wrap");
        wordWrapMenuItem.addActionListener(e -> {
            boolean wrap = textArea.getLineWrap();
            textArea.setLineWrap(!wrap);
            textArea.setWrapStyleWord(!wrap);
        });
        
        JMenuItem fontMenuItem = new JMenuItem("Font");
        fontMenuItem.addActionListener(e -> {
            Font currentFont = textArea.getFont();
            Font selectedFont = JFontChooser.showDialog(this, "Select Font", currentFont);
            if (selectedFont != null) {
                textArea.setFont(selectedFont);
            }
        });
        
        formatMenu.add(wordWrapMenuItem);
        formatMenu.add(fontMenuItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Simple Text Editor\nCreated with Java Swing", 
                "About", JOptionPane.INFORMATION_MESSAGE));
        
        helpMenu.add(aboutMenuItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(helpMenu);
        
        // Set the menu bar to the frame
        setJMenuBar(menuBar);
    }
    
    private void updateTitle() {
        setTitle((changed ? "*" : "") + currentFile + " - Simple Text Editor");
    }
    
    private void newFile() {
        if (confirmSave()) {
            textArea.setText("");
            currentFile = "Untitled";
            changed = false;
            updateTitle();
        }
    }
    
    private void openFile() {
        if (confirmSave()) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    textArea.setText("");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.append(line + "\n");
                    }
                    currentFile = file.getPath();
                    changed = false;
                    updateTitle();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, 
                            "Error reading file: " + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private boolean saveFile(boolean saveAs) {
        if (currentFile.equals("Untitled") || saveAs) {
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".txt")) {
                    file = new File(file.getPath() + ".txt");
                }
                currentFile = file.getPath();
            } else {
                return false;
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
            writer.write(textArea.getText());
            changed = false;
            updateTitle();
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                    "Error saving file: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void printFile() {
        try {
            textArea.print();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, 
                    "Error printing: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean confirmSave() {
        if (!changed) {
            return true;
        }
        
        int response = JOptionPane.showConfirmDialog(this, 
                "The text has been modified. Do you want to save changes?", 
                "Confirm", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            return saveFile(false);
        } else if (response == JOptionPane.NO_OPTION) {
            return true;
        } else {
            return false;
        }
    }
    
    // Custom Font Chooser Dialog
    static class JFontChooser extends JDialog {
        private JList<String> fontList;
        private JList<String> styleList;
        private JList<Integer> sizeList;
        private JTextField previewText;
        private Font selectedFont;
        private boolean approved = false;
        
        public JFontChooser(Frame parent, Font initialFont) {
            super(parent, "Font Chooser", true);
            setSize(500, 400);
            setLayout(new BorderLayout());
            
            // Initialize selected font
            selectedFont = initialFont;
            
            // Font families
            String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            fontList = new JList<>(fonts);
            fontList.setSelectedValue(initialFont.getFamily(), true);
            fontList.addListSelectionListener(e -> updatePreview());
            
            // Font styles
            String[] styles = {"Plain", "Bold", "Italic", "Bold Italic"};
            styleList = new JList<>(styles);
            styleList.setSelectedIndex(initialFont.getStyle());
            styleList.addListSelectionListener(e -> updatePreview());
            
            // Font sizes
            Integer[] sizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
            sizeList = new JList<>(sizes);
            sizeList.setSelectedValue(initialFont.getSize(), true);
            sizeList.addListSelectionListener(e -> updatePreview());
            
            // Preview panel
            previewText = new JTextField("AaBbYyZz");
            previewText.setFont(initialFont);
            previewText.setHorizontalAlignment(JTextField.CENTER);
            previewText.setEditable(false);
            
            // Scroll panes for lists
            JScrollPane fontScroll = new JScrollPane(fontList);
            JScrollPane styleScroll = new JScrollPane(styleList);
            JScrollPane sizeScroll = new JScrollPane(sizeList);
            
            // Panels for each selection
            JPanel fontPanel = new JPanel(new BorderLayout());
            fontPanel.add(new JLabel("Font:"), BorderLayout.NORTH);
            fontPanel.add(fontScroll, BorderLayout.CENTER);
            
            JPanel stylePanel = new JPanel(new BorderLayout());
            stylePanel.add(new JLabel("Style:"), BorderLayout.NORTH);
            stylePanel.add(styleScroll, BorderLayout.CENTER);
            
            JPanel sizePanel = new JPanel(new BorderLayout());
            sizePanel.add(new JLabel("Size:"), BorderLayout.NORTH);
            sizePanel.add(sizeScroll, BorderLayout.CENTER);
            
            // Selection panel containing all three lists
            JPanel selectionPanel = new JPanel(new GridLayout(1, 3));
            selectionPanel.add(fontPanel);
            selectionPanel.add(stylePanel);
            selectionPanel.add(sizePanel);
            
            // Preview panel
            JPanel previewPanel = new JPanel(new BorderLayout());
            previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
            previewPanel.add(previewText, BorderLayout.CENTER);
            
            // Button panel
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> {
                approved = true;
                dispose();
            });
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            
            // Add panels to dialog
            add(selectionPanel, BorderLayout.CENTER);
            add(previewPanel, BorderLayout.NORTH);
            add(buttonPanel, BorderLayout.SOUTH);
            
            setLocationRelativeTo(parent);
        }
        
        private void updatePreview() {
            String fontFamily = fontList.getSelectedValue();
            int fontStyle = styleList.getSelectedIndex();
            int fontSize = sizeList.getSelectedValue();
            
            selectedFont = new Font(fontFamily, fontStyle, fontSize);
            previewText.setFont(selectedFont);
        }
        
        public static Font showDialog(Component parent, String title, Font initialFont) {
            Frame owner;
            if (parent instanceof Frame) {
                owner = (Frame) parent;
            } else {
                owner = (Frame) SwingUtilities.getWindowAncestor(parent);
                if (owner == null) {
                    owner = new Frame(title);
                }
            }
            
            JFontChooser dialog = new JFontChooser(owner, initialFont);
            dialog.setVisible(true);
            
            return dialog.approved ? dialog.selectedFont : null;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SimpleTextEditor();
        });
    }
}
