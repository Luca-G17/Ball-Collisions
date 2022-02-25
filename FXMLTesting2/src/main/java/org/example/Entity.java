package org.example;

import javafx.geometry.Point2D;


public class Entity {
    private Point2D position;
    private float rotation;
    private double radius;
    private double mass;
    private Point2D currentVelocityVector = new Point2D(0, 0);
    private Point2D currentAccelerationVector = new Point2D(0, 0);

    public Entity(double x, double y, double radius){
        position = new Point2D(x, y);
        this.radius = radius;
        this.mass = radius * 10;
    }

    public double getMass() {
        return mass;
    }
    public Point2D getDrawPosition() {
        return position;
    }
    public void setDrawPosition(float x, float y){
        this.position = new Point2D(x, y);
    }
    private void rotate(float rotation){
        this.rotation += rotation;
    }
    public float getRotation(){
        return  rotation;
    }
    public Point2D getVelocity(){
        return currentVelocityVector;
    }
    public Point2D getCenter(){
        Point2D pos = getDrawPosition();
        return new Point2D(pos.getX() + radius, pos.getY() + radius);
    }
    public void setPosition(Point2D pos){
        this.position = pos;
    }
    public Point2D getPosition(){
        return position;
    }
    public void setVelocity(Point2D velocity){
        this.currentVelocityVector = velocity;
    }
    public void addVelocity(Point2D vel){
        currentVelocityVector = currentVelocityVector.add(vel);
    }
    public Point2D getAcceleration(){
        return currentAccelerationVector;
    }
    public void addAcceleration(Point2D acc){
        currentAccelerationVector = currentAccelerationVector.add(acc);
    }
    public void addVelocity(double scalar, double angle){
        Point2D velVector = calculateNewVelocityVector(scalar, Math.toRadians(angle));
        currentVelocityVector = currentVelocityVector.add(velVector);
    }
    private Point2D calculateNewVelocityVector(double scalar, double angle){
        return new Point2D(
                (float) (Math.sin(angle) * scalar),
                (float) (Math.cos(angle) * scalar)
        );
    }
    public void update(float elapsedTime, double width, double height){
        currentAccelerationVector = currentVelocityVector.multiply(PrimaryController.DRAG_SCALAR);
        currentVelocityVector = currentVelocityVector.add(currentAccelerationVector.multiply(elapsedTime));
        position = position.add(currentVelocityVector.multiply(elapsedTime));
        Point2D centre = getCenter();
        if (centre.getX() < 0) position = new Point2D(position.getX() + width, position.getY());
        if (centre.getX() >= width) position = new Point2D(position.getX() - width, position.getY());
        if (centre.getY() < 0) position = new Point2D(position.getX(), position.getY() + height);
        if (centre.getY() >= height) position = new Point2D(position.getX(), position.getY() - height);
        if (Point2D.ZERO.distance(currentVelocityVector) < 0.01) currentVelocityVector = Point2D.ZERO;
    }
    public int getEnergy(){
        return (int) Math.round(0.5 * mass * currentVelocityVector.magnitude() * currentVelocityVector.magnitude() * PrimaryController.ENERGY_OUTPUT_SCALAR);
    }
    public double getRadius(){
        return radius;
    }
}
