import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Creates a table with columns and rows.
 * When a user enters data into the table,
 * the spreadsheet will recalculate the values and formula.
 * If there is a cycle, it will create a popup warning the user and
 * it will revert the value back to what it was.
 *
 *  @version 03/07/2022
 *  @author Aman Vahora, Trevor Tomlin, Phuoc Le, and Bohdan Ivanovich Ivchenko.
 */
public class GUI extends JPanel {

    String[] columnNames;
    String data[][];

    private static int numColumns = 8; // Size of spread sheet.

    private String cellLastValue = "";

    public GUI(Spreadsheet s) throws Overflow, Underflow {

        super(new GridLayout(1,0));


        FlowLayout flow = new FlowLayout();

        setLayout(flow);

        columnNames = new String[numColumns+1];

        columnNames[0] = "";

        // Changes the column names to be excel names
        // e.g A, B, C ... AA, AB
        for (int i = 1; i <= numColumns; i++) {
            columnNames[i] = "";

            int columnNumber = i;

            while (columnNumber > 0)
            {
                int modulo = (columnNumber - 1) % 26;
                columnNames[i] = (char) (65 + modulo) + columnNames[i];
                columnNumber = (columnNumber - modulo) / 26;
            }

        }

        data = new String[numColumns][numColumns+1];

        // Changes rows to numebers 0 - numColumns
        for (int j = 0; j <= numColumns-1; j++) {

            data[j][0] = Integer.toString(j);

        }

        final JTable table = new JTable(data, columnNames);

        TableColumn t = table.getColumnModel().getColumn(0);
        t.setPreferredWidth(50);

        table.setRowSelectionAllowed(false);

        table.getModel().addTableModelListener(
                new TableModelListener()
                {
                    // When the table is changed, recalculate the formulas and update table values.
                    public void tableChanged(TableModelEvent evt)
                    {

                        int row = evt.getFirstRow();
                        int col = evt.getColumn() - 1;

                        // Make sure event isn't called when this method changes the table
                        if (((String) table.getValueAt(row, col + 1)).equals(cellLastValue)) return;

                        cellLastValue = (String) table.getValueAt(row, col + 1);

                        String inputFormula = (String) table.getValueAt(row, col + 1);

                        Stack expTreeTokenStack = new Stack();
                        CellToken cellToken = new CellToken();

                        cellToken.setRow(row);
                        cellToken.setColumn(col);

                        try {
                            expTreeTokenStack = Token.getFormula(inputFormula);
                        } catch (Overflow e) {
                            e.printStackTrace();
                        } catch (Underflow e) {
                            e.printStackTrace();
                        }

                        boolean success = s.changeCellFormulaAndRecalculate(cellToken, expTreeTokenStack, inputFormula);

                        // If there is a cycle, make a popup telling the user.
                        if (!success) {

                            JFrame jFrame = new JFrame();
                            JOptionPane.showMessageDialog(jFrame,"Cycle Detected. Value reverted.");

                        }

                        table.setValueAt(Integer.toString(s.sheet[row][col].value), row, col+1);

                    }
                }
        );

        table.setPreferredScrollableViewportSize(new Dimension(800, 20 * numColumns));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane);

    }

    /*
    Creates frame and menu bar.
    Also creates Spreadsheet object and GUI for the table.
     */
    private static void createAndShowGUI() throws Overflow, Underflow {

        JFrame frame = new JFrame("TCSS342 Spreadsheet");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create menu with 3 options:
        // Close, Save, Open
        JMenu menu, submenu;
        JMenuItem i1, i2, i3, i4, i5;

        JMenuBar mb=new JMenuBar();
        menu=new JMenu("Menu");
        i1=new JMenuItem("Close");
        i2=new JMenuItem("Save");
        i3=new JMenuItem("Open");

        Spreadsheet s = new Spreadsheet(numColumns);
        // Closes window
        i1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        i2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File selectedFile = null;

                JFileChooser FileCheooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "txt");
                FileCheooser.setFileFilter(filter);

                int returnValue = FileCheooser.showSaveDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    selectedFile = FileCheooser.getSelectedFile();

                    try {
                        selectedFile.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        FileOutputStream fos = new FileOutputStream(selectedFile);

                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

                        Cell[][] sheet = s.getSheet();

                        for (int row = 0; row < sheet.length; row++) {
                            for (int col = 0; col < sheet[row].length; col++) {

                                if (sheet[row][col].getFormula().isEmpty()) {
                                    bw.write(sheet[row][col].getValue());
                                } else {
                                    bw.write(sheet[row][col].getFormula());

                                }
                                bw.write(",");
                            }
                            bw.newLine();
                        }
                        bw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        });
        menu.add(i1); menu.add(i2); menu.add(i3);
        mb.add(menu);
        frame.setJMenuBar(mb);
        frame.setSize(400,400);
        frame.setLayout(null);
        frame.setVisible(true);


        GUI newContentPane = new GUI(s);
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Overflow e) {
                    e.printStackTrace();
                } catch (Underflow e) {
                    e.printStackTrace();
                }
            }
        });
    }
}