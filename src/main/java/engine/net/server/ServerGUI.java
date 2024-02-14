package engine.net.server;

import javax.swing.*;

public class ServerGUI extends JFrame implements guiService{

    private Server server;
    private boolean matchmakingStarted;

    private JButton jbStart;
    private JTextArea jtaScroll;
    private JScrollPane jSroll;
    private JTextField jtfSide;
    private JTextField jtfPlayers;

    public ServerGUI(){
        matchmakingStarted = false;
        server = new Server(this);
        initComponents();
    }

    private void initComponents() {

        jSroll = new javax.swing.JScrollPane();
        jtaScroll = new javax.swing.JTextArea();
        jbStart = new javax.swing.JButton();
        jtfSide = new javax.swing.JTextField();
        JLabel jLabel1 = new javax.swing.JLabel();
        jtfPlayers = new javax.swing.JTextField();
        JLabel jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jtaScroll.setEditable(false);
        jtaScroll.setColumns(20);
        jtaScroll.setRows(5);
        jSroll.setViewportView(jtaScroll);

        jbStart.setText("Matchmaking");
        jbStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbStartMatchmaking(evt);
            }
        });

        jtfSide.setToolTipText("");

        jLabel1.setText("Tamaño del tablero:");

        jLabel2.setText("Número de jugadores:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSroll, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jbStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jtfSide)
                                        .addComponent(jtfPlayers)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel2))
                                                .addGap(0, 1, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(0, 230, Short.MAX_VALUE)
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jtfPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jtfSide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbStart))
                                        .addComponent(jSroll))
                                .addContainerGap())
        );

        pack();
    }

    private void jbStartMatchmaking(java.awt.event.ActionEvent evt) {
        int side;
        int players = 1;
        if(!matchmakingStarted){
            try {
                side = Integer.valueOf(jtfSide.getText());
                players = Integer.valueOf(jtfPlayers.getText());
                if(side < 4){
                    JOptionPane.showMessageDialog(null, "El tablero es demasiado pequeño!");
                }else if(side > 20){
                    JOptionPane.showMessageDialog(null, "El tablero es demasiado grande!");
                }else{
                    if(players < 1){
                        JOptionPane.showMessageDialog(null, "No puedes empezar un juego sin jugadores!");
                    }else if(players > 8){
                        JOptionPane.showMessageDialog(null, "No puedes jugar con más de 8 jugadores!");
                    }else{
                        server.generateBoard(side, side);
                        matchmakingStarted = true;
                    }

                }
            }catch(IllegalArgumentException e){
                JOptionPane.showMessageDialog(null, "Tienes que introducir un número!");
            }
        }
        jbStart.setEnabled(!matchmakingStarted);
        if(matchmakingStarted){
            server.setPlayers(players);
            server.startGame();
        }
    }

    public void printMessage(String msg){
        SwingUtilities.invokeLater(() -> {
            System.out.println("Received: " + msg);
            jtaScroll.append(msg + "\n");
        });
    }

}
