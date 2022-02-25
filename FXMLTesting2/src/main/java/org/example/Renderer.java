package org.example;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Rotate;

import java.util.List;
import java.util.ArrayList;

public class Renderer {
    private Canvas canvas;
    private GraphicsContext context;

    private List<Entity> entities = new ArrayList<>();
    public Renderer(Canvas canvas){
        this.canvas = canvas;
        this.context = canvas.getGraphicsContext2D();
    }
    public List<Entity> getEntities(){
        return entities;
    }
    public void addEntity(Entity entity){
        entities.add(entity);
    }
    public void removeEntity(Entity entity){
        entities.remove(entity);
    }
    public void clearEntities(){
        entities.clear();
    }
    public GraphicsContext getContext(){
        return context;
    }
    public void render(){
        context.save();
        for (Entity entity : entities){
            transformContext(entity);
            Point2D pos = entity.getDrawPosition();
            context.setLineWidth(1.5);
            context.setStroke(Color.rgb(0, 0, 0, 1));
            context.strokeOval(pos.getX(), pos.getY(), entity.getRadius() * 2, entity.getRadius() * 2);
        }
        context.restore();
    }
    public void prepare(){
        context.setFill(new RadialGradient(0, 0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#81c483")),
                new Stop(1, Color.web("#fcc200"))));
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    private void transformContext(Entity entity){
        Point2D centre = entity.getCenter();
        Rotate r = new Rotate(entity.getRotation(), centre.getX(), centre.getY());
        context.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }
}
