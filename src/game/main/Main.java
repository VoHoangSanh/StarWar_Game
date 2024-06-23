package game.main;

import game.component.GamePanel;
import java.awt.BorderLayout;
import javax.swing.JFrame;

public class Main extends JFrame {
    
    public Main(){
        init();
    }
    
    private void init(){
        setTitle("Java StarWars Game");
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        GamePanel panelGame = new GamePanel();
        add(panelGame, BorderLayout.CENTER);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                panelGame.createBufferStrategy(3); // Tạo BufferStrategy sau khi thêm vào JFrame
                panelGame.start();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                panelGame.stop();
            }
        });
    }
    
    public static void main(String[] args) {
       Main main = new Main();
       main.setVisible(true);
    }
}