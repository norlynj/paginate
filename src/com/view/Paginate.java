package view;
import view.component.AudioPlayer;
import view.component.Frame;
import view.component.Panel;

import java.awt.*;

public class Paginate {
    private Frame frame;
    private MenuPanel menuPanel;
    private HowPanel howPanel;
    private InputPanel inputPanel;
    private OutputPanel outputPanel;
    private Panel contentPane;
    private CardLayout cardLayout;
    private AudioPlayer audio;

    public Paginate(){
        audio = new AudioPlayer("bgmusic.wav");
        audio.play();
        audio.loop();
        frame = new Frame("Paginate");

        // create Panels
        menuPanel = new MenuPanel();
        howPanel = new HowPanel();
        inputPanel = new InputPanel();
        outputPanel = new OutputPanel();

        // setup the content pane and card layout
        contentPane = new Panel(true, "bg/menu-panel.png");
        cardLayout = new CardLayout();
        contentPane.setLayout(cardLayout);

        // add the panels to the content pane
        contentPane.add(menuPanel, "menuPanel");
        contentPane.add(howPanel, "howPanel");

        contentPane.add(inputPanel, "inputPanel");
        contentPane.add(outputPanel, "outputPanel");

        listenToMenu();
        listenToInput();
        listenToHow();

        frame.add(contentPane);
        frame.pack();
        frame.setVisible(true);
    }

    public void listenToMenu() {
        menuPanel.getStartButton().addActionListener(e -> cardLayout.show(contentPane, "inputPanel" ));
        menuPanel.getHowItWorksButton().addActionListener(e -> cardLayout.show(contentPane, "howPanel" ));
        menuPanel.getExitButton().addActionListener(e -> System.exit(0));
        menuPanel.getMusicOnButton().addActionListener(e -> soundClick());
        menuPanel.getMusicOffButton().addActionListener(e -> soundClick());
    }

    public void listenToHow(){
        howPanel.getMusicOnButton().addActionListener(e -> soundClick());
        howPanel.getMusicOffButton().addActionListener(e -> soundClick());
        howPanel.getHomeButton().addActionListener(e -> cardLayout.show(contentPane, "menuPanel" ));
    }
    public void listenToInput(){
        inputPanel.getMusicOnButton().addActionListener(e -> soundClick());
        inputPanel.getMusicOffButton().addActionListener(e -> soundClick());
        inputPanel.getHomeButton().addActionListener(e -> {
            cardLayout.show(contentPane, "menuPanel" );
            inputPanel.resetTables();
        });
    }

    public void soundClick() {
        menuPanel.musicClick();
        inputPanel.musicClick();
        outputPanel.musicClick();
        howPanel.musicClick();
        if (audio.isPlaying()) {
            audio.stop();
        } else {
            audio.play();
        }
    }

}
