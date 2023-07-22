package gui;

import robot_logic.RobotController;
import save_logic.Saveable;
import save_logic.State;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.Observable;
import java.util.Observer;

public class CoordsWindow extends JInternalFrame implements Saveable, Observer {
    private TextArea textArea;
    private final RobotController controller;
    public CoordsWindow(RobotController controller) {
        super("Координаты", true, true, true, true);
        this.controller = controller;

        textArea = new TextArea();
        textArea.setText("initial text");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textArea, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public State getState() {
        State state = new State();
        state.putElement("prefix", this.getPrefix());

        state.putElement("height", this.getSize().height);
        state.putElement("width", this.getSize().width);

        state.putElement("location_x", this.getLocation().getX());
        state.putElement("location_y", this.getLocation().getY());

        state.putElement("is_hidden", this.isIcon);

        return state;
    }

    @Override
    public void restoreState(State state) {
        var height = state.getElement("height");
        var width = state.getElement("width");

        var locationX = Math.round((Double) state.getElement("location_x"));
        var locationY = Math.round((Double) state.getElement("location_y"));

        var isHidden = state.getElement("is_hidden");

        this.setSize(Math.toIntExact((Long) width), Math.toIntExact((Long) height));
        this.setLocation(Math.toIntExact(locationX), Math.toIntExact(locationY));

        try {
            this.setIcon((Boolean) isHidden);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPrefix() {
        return "CoordsWindow";
    }

    private void onRobotMoved() {
        String text = "X: " + controller.getRobotPositionX() + "\n" +
                "Y: " + controller.getRobotPositionY();
        textArea.setText(text);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (RobotController.ROBOT_MOVED.equals(arg)) {
            onRobotMoved();
        }
    }
}
