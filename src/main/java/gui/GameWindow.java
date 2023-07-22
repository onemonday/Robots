package gui;

import robot_logic.RobotController;
import save_logic.Saveable;
import save_logic.State;

import java.awt.*;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame implements Saveable
{
    public GameWindow(RobotController controller)
    {
        super("Игровое поле", true, true, true, true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(controller.getVisualizer(), BorderLayout.CENTER);
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
        return "GameWindow";
    }
}
