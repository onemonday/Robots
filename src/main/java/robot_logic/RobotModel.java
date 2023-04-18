package robot_logic;

public class RobotModel {
    public static final double MAX_VELOCITY = 0.1;
    //нельзя изменять поведение робота
    public static final double MAX_ANGULAR_VELOCITY = 0.001;
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    public double getRobotPositionX() {
        return m_robotPositionX;
    }

    public double getRobotPositionY() {
        return m_robotPositionY;
    }

    public void setRobotPositionX(double newX) {
        this.m_robotPositionX = newX;
    }

    public void setRobotPositionY(double newY) {
        this.m_robotPositionY = newY;
    }

    public double getRobotDirection() {
        return m_robotDirection;
    }

    public void setRobotDirection(double newDirection) {
        this.m_robotDirection = newDirection;
    }
}
