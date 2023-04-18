package robot_logic;

import gui.GameVisualizer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class RobotController extends Observable {
    public static final Integer ROBOT_MOVED = 1;
    private final RobotModel robot;
    private final GameVisualizer visualizer;
    private final Timer m_timer = initTimer();

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;
    private final double duration = 10;

    public RobotController() {
        this.robot = new RobotModel();
        this.visualizer = new GameVisualizer(this);

        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();
            }
        }, 0, 10);
        visualizer.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setTargetPositionX(e.getPoint().x);
                setTargetPositionY(e.getPoint().y);
                visualizer.repaint();
            }
        });
        visualizer.setDoubleBuffered(true);
    }

    private static Timer initTimer()
    {
        return new Timer("events generator", true);
    }

    public GameVisualizer getVisualizer() {
        return this.visualizer;
    }

    public void addObservers(List<Observer> observers) {
        observers.forEach(observer -> this.addObserver(observer));
    }

    public double getRobotPositionX() {
        return robot.getRobotPositionX();
    }

    public double getRobotPositionY() {
        return robot.getRobotPositionY();
    }

    public double getRobotDirection() {
        return robot.getRobotDirection();
    }

    public int getTargetPositionX() {
        return m_targetPositionX;
    }

    public int getTargetPositionY() {
        return m_targetPositionY;
    }

    public void setTargetPositionX(int newTargetX) {
        this.m_targetPositionX = newTargetX;
    }

    public void setTargetPositionY(int newTargetY) {
        this.m_targetPositionY = newTargetY;
    }

    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(visualizer::repaint);
    }

    protected void onModelUpdateEvent() {
        moveRobot(m_targetPositionX, m_targetPositionY, duration);
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double asNormalizedRadians(double angle)
    {

        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    public void moveRobot(int targetPositionX, int targetPositionY, double duration)
    {
        double distance = distance(targetPositionX, targetPositionY,
                robot.getRobotPositionX(), robot.getRobotPositionY());
        if (distance < 0.5)
        {
            return;
        }
        double velocity = RobotModel.MAX_VELOCITY;
        double angleToTarget = angleTo(robot.getRobotPositionX(), robot.getRobotPositionY(), targetPositionX, targetPositionY);
        double angularVelocity = 0;

        double angle = asNormalizedRadians(angleToTarget - robot.getRobotDirection());

        if (angle < Math.PI / 2) {
            angularVelocity = RobotModel.MAX_ANGULAR_VELOCITY;
        } else if (angle > Math.PI / 2) {
            angularVelocity = -RobotModel.MAX_ANGULAR_VELOCITY;
        }

        velocity = applyLimits(velocity, 0, RobotModel.MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -RobotModel.MAX_ANGULAR_VELOCITY, RobotModel.MAX_ANGULAR_VELOCITY);
        double newX = robot.getRobotPositionX() + velocity / angularVelocity *
                (Math.sin(robot.getRobotDirection()  + angularVelocity * duration) -
                        Math.sin(robot.getRobotDirection()));
        if (!Double.isFinite(newX))
        {
            newX = robot.getRobotPositionX() + velocity * duration * Math.cos(robot.getRobotDirection());
        }
        double newY = robot.getRobotPositionY() - velocity / angularVelocity *
                (Math.cos(robot.getRobotDirection()  + angularVelocity * duration) -
                        Math.cos(robot.getRobotDirection()));
        if (!Double.isFinite(newY))
        {
            newY = robot.getRobotPositionY() + velocity * duration * Math.sin(robot.getRobotDirection());
        }
        robot.setRobotPositionX(newX);
        robot.setRobotPositionY(newY);
        double newDirection = asNormalizedRadians(robot.getRobotDirection() + angularVelocity * duration);
        robot.setRobotDirection(newDirection);

        setChanged();
        notifyObservers(ROBOT_MOVED);
        clearChanged();
    }
}
