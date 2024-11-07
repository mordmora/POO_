package core;

import core.gates.ANDGate;
import core.gates.Cable;
import core.gates.LED;
import core.gates.LogicGate;
import core.gates.NANDGate;
import core.gates.NORGate;
import core.gates.NOTGate;
import core.gates.ORGate;
import core.gates.SWITCH;
import core.gates.XNORGate;
import core.gates.XORGate;
import expression_parser.ast.LogicGateParser;
import expression_parser.ast.Printer;
import expression_parser.ast.nodes.ASTNode;
import expression_parser.lexer.LogicGateLexer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.util.Stack;

/**
 *
 * @author glsx0
 */
public class DrawnFrame extends javax.swing.JFrame {

    private final ArrayList<LogicGate> gates;
    private ArrayList<Cable> cables;
    private LogicGate draggedGate = null;  // Variable para almacenar la compuerta arrastrada
    private int offsetX, offsetY;
    private ArrayList<SWITCH> switches;

    private static final int MAX_GATES_PER_ROW = 6;
    private static final int GAP = 150;  // Espacio entre las compuertas
    private static final int START_X = 100;
    private static final int START_Y = 100;
    private final ArrayList<Point> pComponents = new ArrayList<>();
    private static final int gridSize = 10;  // Tamaño de la cuadrícula

    private ASTNode root;

    private LogicGateLexer lexer;
    private LogicGateParser parser;
    private Printer prt;

    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem item1 = new JMenuItem("Eliminar compuerta");
    JMenuItem item2 = new JMenuItem("2 entradas");
    JMenuItem item3 = new JMenuItem("3 entradas");
    JMenuItem item4 = new JMenuItem("4 entradas");

    // Añadir una variable para almacenar la compuerta seleccionada para eliminación
    private LogicGate selectedGate = null;

    public DrawnFrame() {

        popupMenu.setPreferredSize(new java.awt.Dimension(100, 50));
        popupMenu.add(item1);

        popupMenu.setPreferredSize(new java.awt.Dimension(100, 50));
        popupMenu.add(item2);

        popupMenu.setPreferredSize(new java.awt.Dimension(100, 50));
        popupMenu.add(item3);

        popupMenu.setPreferredSize(new java.awt.Dimension(100, 50));
        popupMenu.add(item4);

        gates = new ArrayList<>();
        cables = new ArrayList<>();
        switches = new ArrayList<>();

        // Agregar ActionListener al item1 para manejar la eliminación
        item1.addActionListener(e -> {
            if (selectedGate != null) {
                gates.remove(selectedGate);  // Eliminar la compuerta del ArrayList
                selectedGate = null;  // Limpiar la referencia
                repaint();  // Redibujar el panel para reflejar los cambios
            }
        });
        item2.addActionListener(e -> {
            if (selectedGate != null) {
                selectedGate.setNumInputs(2);
                repaint();
            }
        });
        item3.addActionListener(e -> {
            if (selectedGate != null) {
                selectedGate.setNumInputs(3);
                repaint();
            }
        });
        item4.addActionListener(e -> {
            if (selectedGate != null) {
                selectedGate.setNumInputs(4);
                repaint();
            }
        });

        initComponents();

        // Añadir listeners al panel de dibujo
        DrawingPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            private LogicGate selectedSourceGate = null;
            private SWITCH selectedSwitch = null;

            public void mouseClicked(MouseEvent evt) {
                Point p = evt.getPoint();
                Point clickPoint = evt.getPoint();
                // Comprobar si se hizo clic en un interruptor
                for (LogicGate gate : gates) {
                    if (gate instanceof SWITCH && gate.contains(p.x, p.y)) {
                        ((SWITCH) gate).toggle();
                        boolean state;
                        state = ((SWITCH) gate).isOn();
                        for(Cable c : cables){
                            if(c.getSource() == ((SWITCH) gate)){
                                c.setPower(state);
                            }
                        }

                        repaint(); // Redibujar el panel
                        return; // Salir después de cambiar el estado
                    }
                }

                for (SWITCH sw : switches) { // Asumiendo que tienes una lista de interruptores
                    Point outputPoint = sw.getOutputPoint();
                    if (isNear(clickPoint, outputPoint)) {
                        selectedSwitch = sw; // Guardar el interruptor de origen
                        return;
                    }
                }
                for (LogicGate gate : gates) {
                    Point outputPoint = gate.getOutputPoint();
                    if (isNear(clickPoint, outputPoint)) {
                        selectedSourceGate = gate; // Guardar la compuerta de origen
                        return;
                    }
                }
                if (selectedSourceGate != null) {
                    for (LogicGate gate : gates) {
                        for (int i = 0; i < gate.getNumInputs(); i++) {
                            Point inputPoint = gate.getInputPoint(i);
                            if (isNear(clickPoint, inputPoint)) {
                                // Conectar el cable desde la compuerta de origen a la de destino
                                Cable cable = new Cable(selectedSourceGate, gate, i);
                                gate.inputCables.add(cable);
                                selectedSourceGate.outputCables.add(cable);
                                cables.add(cable); // Asumiendo que tienes una lista de cables
                                selectedSourceGate = null; // Reiniciar para la siguiente conexión
                                repaint(); // Redibujar la interfaz gráfica
                                return;
                            }
                        }
                    }
                }
                if (selectedSwitch != null) {
                    for (LogicGate gate : gates) {
                        for (int i = 0; i < gate.getNumInputs(); i++) {
                            Point inputPoint = gate.getInputPoint(i);
                            if (isNear(clickPoint, inputPoint)) {
                                // Conectar el cable desde el SWITCH a la compuerta de destino
                                Cable cable = new Cable(selectedSwitch, gate, i);
                                cables.add(cable); // Añadir el cable a la lista de cables
                                selectedSwitch.inputCables.add(cable);
                                selectedSwitch = null; // Reiniciar para la siguiente conexión
                                repaint(); // Redibujar la interfaz gráfica
                                return;
                            }
                        }
                    }
                }

                // Ajustar el punto a la cuadrícula
                int gridX = (p.x / gridSize) * gridSize;
                int gridY = (p.y / gridSize) * gridSize;

                // Almacenar la posición ajustada del componente
                if (!pComponents.contains(new Point(gridX, gridY))) {
                    pComponents.add(new Point(gridX, gridY));
                }

                // Actualizar el panel para redibujar los componentes
                repaint();
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                popupMenu.setVisible(false);

                if (evt.getButton() == MouseEvent.BUTTON3) {
                    Point mousePoint = evt.getPoint();
                    selectedGate = null;  // Reiniciar la compuerta seleccionada

                    for (LogicGate gate : gates) {
                        if (gate.contains(mousePoint.x, mousePoint.y)) {
                            selectedGate = gate;  // Guardar la compuerta seleccionada
                            popupMenu.show(DrawingPanel, 30, -90);
                            break;
                        }
                    }
                } else if (evt.getButton() == MouseEvent.BUTTON1) {
                    onMousePressed(evt);
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onMouseReleased(evt);
            }

            private boolean isNear(Point clickPoint, Point gatePoint) {
                int tolerance = 10;  // Tolerancia para considerar el clic dentro del área
                return clickPoint.distance(gatePoint) <= tolerance;
            }

        });

        DrawingPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                onMouseDragged(evt);
            }
        });

    }

    void parseExpression(String inputStr) {
        lexer = new LogicGateLexer(inputStr);
        parser = new LogicGateParser(lexer);
        prt = new Printer(this);
        root = parser.parse();
        root.accept(prt);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmenu = new javax.swing.JPopupMenu();
        BtAND = new javax.swing.JButton();
        BtOR = new javax.swing.JButton();
        BtNOT = new javax.swing.JButton();
        BtNAND = new javax.swing.JButton();
        BtNOR = new javax.swing.JButton();
        BtXOR = new javax.swing.JButton();
        BtXNOR = new javax.swing.JButton();
        DrawingPanel = new javax.swing.JPanel();
        BtLED = new javax.swing.JButton();
        BtSWITCH = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        InputStr = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BtAND.setIcon(new javax.swing.ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESAND.png"))); // NOI18N
        BtAND.setText("AND");
        BtAND.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtANDMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtANDMouseExited(evt);
            }
        });
        BtAND.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtANDActionPerformed(evt);
            }
        });

        BtOR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESOR.png"))); // NOI18N
        BtOR.setText("OR");
        BtOR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtORMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtORMouseExited(evt);
            }
        });
        BtOR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtORActionPerformed(evt);
            }
        });

        BtNOT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESNOT.png"))); // NOI18N
        BtNOT.setText("NOT");
        BtNOT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtNOTMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtNOTMouseExited(evt);
            }
        });
        BtNOT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtNOTActionPerformed(evt);
            }
        });

        BtNAND.setIcon(new javax.swing.ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESNAND.png"))); // NOI18N
        BtNAND.setText("NAND");
        BtNAND.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtNANDMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtNANDMouseExited(evt);
            }
        });
        BtNAND.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtNANDActionPerformed(evt);
            }
        });

        BtNOR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESNOR.png"))); // NOI18N
        BtNOR.setText("NOR");
        BtNOR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtNORMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtNORMouseExited(evt);
            }
        });
        BtNOR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtNORActionPerformed(evt);
            }
        });

        BtXOR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESXOR.png"))); // NOI18N
        BtXOR.setText("XOR");
        BtXOR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtXORMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtXORMouseExited(evt);
            }
        });
        BtXOR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtXORActionPerformed(evt);
            }
        });

        BtXNOR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESXNOR.png"))); // NOI18N
        BtXNOR.setText("XNOR");
        BtXNOR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtXNORMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtXNORMouseExited(evt);
            }
        });
        BtXNOR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtXNORActionPerformed(evt);
            }
        });

        DrawingPanel.setBackground(new java.awt.Color(255, 255, 255));
        DrawingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Design Frame"));

        javax.swing.GroupLayout DrawingPanelLayout = new javax.swing.GroupLayout(DrawingPanel);
        DrawingPanel.setLayout(DrawingPanelLayout);
        DrawingPanelLayout.setHorizontalGroup(
            DrawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        DrawingPanelLayout.setVerticalGroup(
            DrawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 429, Short.MAX_VALUE)
        );

        BtLED.setIcon(new javax.swing.ImageIcon(getClass().getResource("/core/recursos graficos/LED.png"))); // NOI18N
        BtLED.setText("LED");
        BtLED.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtLEDMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtLEDMouseExited(evt);
            }
        });
        BtLED.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtLEDActionPerformed(evt);
            }
        });

        BtSWITCH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/core/recursos graficos/SWITCHMesa de trabajo 10.png"))); // NOI18N
        BtSWITCH.setText("SWITCH");
        BtSWITCH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtSWITCHMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtSWITCHMouseExited(evt);
            }
        });
        BtSWITCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtSWITCHActionPerformed(evt);
            }
        });

        jLabel1.setText("Ingresa una expresion");

        InputStr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InputStrActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DrawingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BtAND)
                        .addGap(18, 18, 18)
                        .addComponent(BtOR)
                        .addGap(18, 18, 18)
                        .addComponent(BtNOT)
                        .addGap(18, 18, 18)
                        .addComponent(BtNAND)
                        .addGap(18, 18, 18)
                        .addComponent(BtNOR, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtXOR)
                        .addGap(18, 18, 18)
                        .addComponent(BtXNOR)
                        .addGap(18, 18, 18)
                        .addComponent(BtLED)
                        .addGap(18, 18, 18)
                        .addComponent(BtSWITCH))
                    .addComponent(InputStr))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(InputStr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(BtOR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtNOT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtNOR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtNAND, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtXOR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtXNOR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtAND, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtLED, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtSWITCH, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(DrawingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void addGate(LogicGate gate) {
        int index = gates.size();
        int x = START_X + (index % MAX_GATES_PER_ROW) * GAP;
        int y = START_Y + (index / MAX_GATES_PER_ROW) * GAP;

        x = (x / gridSize) * gridSize;
        y = (y / gridSize) * gridSize;

        gate.setPosition(x, y);
        gates.add(gate);
        repaint();
    }

    private void BtANDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtANDActionPerformed
        addGate(new ANDGate(0, 0, 2));

    }//GEN-LAST:event_BtANDActionPerformed

    private void BtXORActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtXORActionPerformed
        addGate(new XORGate(0, 0, 2));

    }//GEN-LAST:event_BtXORActionPerformed

    private void BtNORActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtNORActionPerformed
        addGate(new NORGate(0, 0, 2));

    }//GEN-LAST:event_BtNORActionPerformed

    private void BtORActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtORActionPerformed
        addGate(new ORGate(0, 0, 2));

    }//GEN-LAST:event_BtORActionPerformed

    private void BtNOTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtNOTActionPerformed
        addGate(new NOTGate(0, 0, 4));

    }//GEN-LAST:event_BtNOTActionPerformed

    private void BtNANDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtNANDActionPerformed
        addGate(new NANDGate(0, 0, 2));
    }//GEN-LAST:event_BtNANDActionPerformed

    private void BtXNORActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtXNORActionPerformed
        addGate(new XNORGate(0, 0, 2));
    }//GEN-LAST:event_BtXNORActionPerformed

    private void BtANDMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtANDMouseEntered

        ImageIcon icono = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESAND.png"));
        BtAND.setPreferredSize(BtAND.getSize());
        BtAND.setMaximumSize(BtAND.getSize());
        BtAND.setMinimumSize(BtAND.getSize());
        BtAND.setText("");
        BtAND.setIcon(icono);
    }//GEN-LAST:event_BtANDMouseEntered

    private void BtANDMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtANDMouseExited

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESAND.png"));
        BtAND.setPreferredSize(BtAND.getSize());
        BtAND.setMaximumSize(BtAND.getSize());
        BtAND.setMinimumSize(BtAND.getSize());
        BtAND.setIcon(originalIcon);
        BtAND.setText("AND");
    }//GEN-LAST:event_BtANDMouseExited

    private void BtORMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtORMouseEntered

        ImageIcon icono = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESOR.png"));
        BtOR.setPreferredSize(BtOR.getSize());
        BtOR.setMaximumSize(BtOR.getSize());
        BtOR.setMinimumSize(BtOR.getSize());
        BtOR.setText("");
        BtOR.setIcon(icono);
    }//GEN-LAST:event_BtORMouseEntered

    private void BtORMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtORMouseExited

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESOR.png"));
        BtOR.setPreferredSize(BtOR.getSize());
        BtOR.setMaximumSize(BtOR.getSize());
        BtOR.setMinimumSize(BtOR.getSize());
        BtOR.setIcon(originalIcon);
        BtOR.setText("OR");
    }//GEN-LAST:event_BtORMouseExited

    private void BtNOTMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtNOTMouseEntered

        ImageIcon icono = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESNOT.png"));
        BtNOT.setPreferredSize(BtNOT.getSize());
        BtNOT.setMaximumSize(BtNOT.getSize());
        BtNOT.setMinimumSize(BtNOT.getSize());
        BtNOT.setText("");
        BtNOT.setIcon(icono);
    }//GEN-LAST:event_BtNOTMouseEntered

    private void BtNOTMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtNOTMouseExited

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESNOT.png"));
        BtNOT.setPreferredSize(BtNOT.getSize());
        BtNOT.setMaximumSize(BtNOT.getSize());
        BtNOT.setMinimumSize(BtNOT.getSize());
        BtNOT.setIcon(originalIcon);
        BtNOT.setText("NOT");
    }//GEN-LAST:event_BtNOTMouseExited

    private void BtNANDMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtNANDMouseExited

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESNAND.png"));
        BtNAND.setPreferredSize(BtNAND.getSize());
        BtNAND.setMaximumSize(BtNAND.getSize());
        BtNAND.setMinimumSize(BtNAND.getSize());
        BtNAND.setIcon(originalIcon);
        BtNAND.setText("NAND");
    }//GEN-LAST:event_BtNANDMouseExited

    private void BtNANDMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtNANDMouseEntered

        ImageIcon icono = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESNAND.png"));
        BtNAND.setPreferredSize(BtNAND.getSize());
        BtNAND.setMaximumSize(BtNAND.getSize());
        BtNAND.setMinimumSize(BtNAND.getSize());
        BtNAND.setText("");
        BtNAND.setIcon(icono);
    }//GEN-LAST:event_BtNANDMouseEntered

    private void BtNORMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtNORMouseEntered

        ImageIcon icono = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESNOR.png"));
        BtNOR.setPreferredSize(BtNOR.getSize());
        BtNOR.setMaximumSize(BtNOR.getSize());
        BtNOR.setMinimumSize(BtNOR.getSize());
        BtNOR.setText("");
        BtNOR.setIcon(icono);
    }//GEN-LAST:event_BtNORMouseEntered

    private void BtNORMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtNORMouseExited

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESNOR.png"));
        BtNOR.setPreferredSize(BtNOR.getSize());
        BtNOR.setMaximumSize(BtNOR.getSize());
        BtNOR.setMinimumSize(BtNOR.getSize());
        BtNOR.setIcon(originalIcon);
        BtNOR.setText("NOR");
    }//GEN-LAST:event_BtNORMouseExited

    private void BtXORMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtXORMouseEntered
        ImageIcon icono = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESXOR.png"));
        BtXOR.setPreferredSize(BtXOR.getSize());
        BtXOR.setMaximumSize(BtXOR.getSize());
        BtXOR.setMinimumSize(BtXOR.getSize());
        BtXOR.setText("");
        BtXOR.setIcon(icono);
    }//GEN-LAST:event_BtXORMouseEntered

    private void BtXORMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtXORMouseExited

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESXOR.png"));
        BtXOR.setPreferredSize(BtXOR.getSize());
        BtXOR.setMaximumSize(BtXOR.getSize());
        BtXOR.setMinimumSize(BtXOR.getSize());
        BtXOR.setIcon(originalIcon);
        BtXOR.setText("XOR");
    }//GEN-LAST:event_BtXORMouseExited

    private void BtXNORMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtXNORMouseEntered
        ImageIcon icono = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESXNOR.png"));
        BtXNOR.setPreferredSize(BtXNOR.getSize());
        BtXNOR.setMaximumSize(BtXNOR.getSize());
        BtXNOR.setMinimumSize(BtXNOR.getSize());
        BtXNOR.setText("");
        BtXNOR.setIcon(icono);
    }//GEN-LAST:event_BtXNORMouseEntered

    private void BtXNORMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtXNORMouseExited

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/core/recursos graficos/LOGICGATESXNOR.png"));
        BtXNOR.setPreferredSize(BtXNOR.getSize());
        BtXNOR.setMaximumSize(BtXNOR.getSize());
        BtXNOR.setMinimumSize(BtXNOR.getSize());
        BtXNOR.setIcon(originalIcon);
        BtXNOR.setText("XNOR");
    }//GEN-LAST:event_BtXNORMouseExited

    private void BtLEDMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtLEDMouseEntered

        ImageIcon icono = new ImageIcon(getClass().getResource("/core/recursos graficos/LED.png"));
        BtLED.setPreferredSize(BtLED.getSize());
        BtLED.setMaximumSize(BtLED.getSize());
        BtLED.setMinimumSize(BtLED.getSize());
        BtLED.setText("");
        BtLED.setIcon(icono);
    }//GEN-LAST:event_BtLEDMouseEntered

    private void BtLEDMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtLEDMouseExited

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/core/recursos graficos/LED.png"));
        BtLED.setPreferredSize(BtLED.getSize());
        BtLED.setMaximumSize(BtLED.getSize());
        BtLED.setMinimumSize(BtLED.getSize());
        BtLED.setIcon(originalIcon);
        BtLED.setText("LED");
    }//GEN-LAST:event_BtLEDMouseExited

    private void BtSWITCHMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtSWITCHMouseEntered

        ImageIcon icono = new ImageIcon(getClass().getResource("/core/recursos graficos/SWITCHMesa de trabajo 10.png"));
        BtSWITCH.setPreferredSize(BtSWITCH.getSize());
        BtSWITCH.setMaximumSize(BtSWITCH.getSize());
        BtSWITCH.setMinimumSize(BtSWITCH.getSize());
        BtSWITCH.setText("");
        BtSWITCH.setIcon(icono);
    }//GEN-LAST:event_BtSWITCHMouseEntered

    private void BtSWITCHMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtSWITCHMouseExited

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/core/recursos graficos/SWITCHMesa de trabajo 10.png"));
        BtSWITCH.setPreferredSize(BtSWITCH.getSize());
        BtSWITCH.setMaximumSize(BtSWITCH.getSize());
        BtSWITCH.setMinimumSize(BtSWITCH.getSize());
        BtSWITCH.setIcon(originalIcon);
        BtSWITCH.setText("SWITCH");
    }//GEN-LAST:event_BtSWITCHMouseExited

    private void BtSWITCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtSWITCHActionPerformed
        String label = "A"; // Aquí puedes definir la letra que deseas usar, puedes reemplazar "A" por lo que necesites.

        // Crear un nuevo interruptor con la etiqueta
        SWITCH switchGate = new SWITCH(label, 0, 0);
        addGate(switchGate); // Agregarlo al panel
        switchGate.toggle(); // Cambiar el estado al agregarlo
        repaint(); // Volver a dibujar el panel
    }//GEN-LAST:event_BtSWITCHActionPerformed

    private void InputStrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputStrActionPerformed
        parseExpression(evt.getActionCommand());
    }//GEN-LAST:event_InputStrActionPerformed

    private void BtLEDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtLEDActionPerformed
        LED ledGate = new LED(0, 0); // Crear un nuevo interruptor
        addGate(ledGate); // Agregarlo al panel
        ledGate.toggle(); // Cambiar el estado al agregarlo
        repaint();
    }//GEN-LAST:event_BtLEDActionPerformed

    private void conectarSalidas() {
        Map<LogicGate, Integer> contadorEntradas = new HashMap<>(); // Mapa para llevar el control de entradas por compuerta

        for (LogicGate gate : gates) {
            if (gate instanceof SWITCH) {
                Point outputPoint = ((SWITCH) gate).getOutputPoint(); // Obtener el punto de salida del SWITCH

                for (LogicGate targetGate : gates) {
                    if (targetGate instanceof SWITCH) {
                        continue; // Evitar conectar SWITCH con otro SWITCH
                    }
                    // Obtener el índice de entrada disponible para la compuerta destino
                    int entradaIndex = contadorEntradas.getOrDefault(targetGate, 0);

                    // Verificar que el índice de entrada no exceda el número de entradas de la compuerta destino
                    if (entradaIndex < targetGate.getNumInputs()) {
                        Point inputPoint = targetGate.getInputPoint(entradaIndex);

                        if (isNear(outputPoint, inputPoint)) {
                            // Conectar el cable desde el SWITCH a la compuerta de destino
                            Cable cable = new Cable(gate, targetGate, entradaIndex);
                            if (!cables.contains(cable)) { // Verificar si ya existe el cable
                                cables.add(cable); // Añadir el cable a la lista de cables
                            }

                            // Actualizar el contador para que el próximo SWITCH se conecte al siguiente puerto
                            contadorEntradas.put(targetGate, entradaIndex + 1);
                            break; // Salir del bucle después de conectar
                        }
                    }
                }
            }
        }
        // Crear y dibujar el LED
        LED ledGate = new LED(0, 0); // Crear un nuevo LED en la posición actual
        addGate(ledGate); // Agregarlo al panel
        repaint(); // Refrescar la interfaz gráfica para mostrar los cambios
    }

    // Método auxiliar para verificar si dos puntos están cerca
    private boolean isNear(Point p1, Point p2) {
        int threshold = 1000; // Umbral de distancia
        return Math.abs(p1.x - p2.x) < threshold && Math.abs(p1.y - p2.y) < threshold;
    }

    public void paint(Graphics g) {
        super.paint(g);
        // Dibujar la cuadrícula
        Graphics2D g2d = (Graphics2D) DrawingPanel.getGraphics();
        g2d.setColor(Color.LIGHT_GRAY);

        // Líneas de la cuadrícula
        for (int i = 0; i < DrawingPanel.getWidth(); i += gridSize) {
            g2d.drawLine(i, 0, i, DrawingPanel.getHeight());
        }
        for (int i = 0; i < DrawingPanel.getHeight(); i += gridSize) {
            g2d.drawLine(0, i, DrawingPanel.getWidth(), i);
        }

        if (cables != null) {
            g2d.setColor(Color.BLACK);  // Asegurar que los cables se dibujan en negro
            for (Cable cable : cables) {
                cable.draw(g2d);  // Dibujar cada cable
            }
        }

        // Dibujar las compuertas lógicas
        Graphics2D gateG2D = (Graphics2D) DrawingPanel.getGraphics();
        gateG2D.setColor(Color.BLACK);
        for (LogicGate gate : gates) {
            gate.draw(gateG2D);
        }

        // Dibuja los cables
        for (Cable cable : cables) {
            cable.draw(g2d); // Asegúrate de tener un método draw en la clase Cable
        }

        // Dibuja cada SWITCH
        for (SWITCH sw : switches) {
            sw.draw(g2d); // Asumiendo que cada interruptor tiene un método draw(Graphics g)
        }

    }

    // Event listeners for mouse drag and drop
    private void onMousePressed(MouseEvent evt) {

        Point mousePoint = evt.getPoint();
        for (LogicGate gate : gates) {
            if (gate.contains(mousePoint.x, mousePoint.y)) {
                draggedGate = gate;
                offsetX = mousePoint.x - gate.getX();
                offsetY = mousePoint.y - gate.getY();
                break;
            }
        }
    }

    private void onMouseReleased(MouseEvent evt) {
        if (draggedGate != null) {
            // Ajustar posición a la cuadrícula
            int newX = (draggedGate.getX() / gridSize) * gridSize;
            int newY = (draggedGate.getY() / gridSize) * gridSize;
            draggedGate.setPosition(newX, newY);
            draggedGate = null;
            repaint();
        }
    }

    private void onMouseDragged(MouseEvent evt) {
        if (draggedGate != null) {
            int newX = evt.getX() - offsetX;
            int newY = evt.getY() - offsetY;

            // Ajustar las posiciones a la cuadrícula
            newX = (newX / gridSize) * gridSize;
            newY = (newY / gridSize) * gridSize;

            draggedGate.setPosition(newX, newY);
            repaint();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtAND;
    private javax.swing.JButton BtLED;
    private javax.swing.JButton BtNAND;
    private javax.swing.JButton BtNOR;
    private javax.swing.JButton BtNOT;
    private javax.swing.JButton BtOR;
    private javax.swing.JButton BtSWITCH;
    private javax.swing.JButton BtXNOR;
    private javax.swing.JButton BtXOR;
    private javax.swing.JPanel DrawingPanel;
    private javax.swing.JTextField InputStr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPopupMenu ppmenu;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the x
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DrawnFrame().setVisible(true);
            }
        });
    }
}
