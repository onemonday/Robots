package gui;

import java.awt.*;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import save_logic.Saveable;
import save_logic.State;

public class LogWindow extends JInternalFrame implements LogChangeListener, Saveable
{
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) 
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
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
        return "LogWindow";
    }
}
