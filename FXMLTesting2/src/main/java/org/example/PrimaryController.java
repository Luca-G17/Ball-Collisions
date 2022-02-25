package org.example;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimaryController {

    @FXML
    private Canvas canvas;
    @FXML
    private Pane anchorPane;
    private Entity selectedBall;


    public void initialize(){
        Random rand = new Random();
        canvas.widthProperty().bind(anchorPane.widthProperty());
        canvas.heightProperty().bind(anchorPane.heightProperty());
        anchorPane.setBackground(new Background(
                new BackgroundFill(
                new RadialGradient(0, 0, 0, 0, 1, true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#81c483")),
                        new Stop(1, Color.web("#fcc200"))),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // drawLines(gc);
        // anchorPane.getChildren().add(new Circle(40));

        Renderer renderer = new Renderer(this.canvas);
        for (int i = 0; i < 30; i++){
            int rad = rand.nextInt(50) + 10;
            int x = (int) Math.round(anchorPane.getMinHeight());
            int y = (int) Math.round(anchorPane.getMinHeight());
            Entity newCircle = new Entity(rand.nextInt(x), rand.nextInt(y), rad, rad);
            renderer.addEntity(newCircle);
        }
        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    selectedBall = null;
                }
                else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                    if (selectedBall != null){
                        Point2D pos = selectedBall.getCenter();
                        double velX = 5 * ((pos.getX()) - mouseEvent.getX());
                        double velY = 5 * ((pos.getY()) - mouseEvent.getY());
                        selectedBall.setVelocity(new Point2D(velX, velY));
                    }
                    selectedBall = null;
                }
            }
        });
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if (selectedBall != null){
                        double x = mouseEvent.getX();
                        double y = mouseEvent.getY();
                        double r = selectedBall.getHeight();
                        selectedBall.setPosition(new Point2D(x - r, y - r));
                    }
                }
            }
        });
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) || mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                    selectedBall = null;
                    double x = mouseEvent.getX();
                    double y = mouseEvent.getY();
                    for (Entity entity : renderer.getEntities()){
                        if (isPointInBall(entity.getCenter(), entity.getHeight(), x, y)){
                            selectedBall = entity;
                            break;
                        }
                    }
                }
            }
        });
        PhysicsLoop timer = new PhysicsLoop() {
            @Override
            public void tick(float secondsSinceLastFrame) {
                renderer.prepare();
                List<Pair<Entity, Entity>> collidingBalls = new ArrayList<>();
                updateBalls(renderer, secondsSinceLastFrame);
                for (Entity entity : renderer.getEntities()){
                    entity.update(secondsSinceLastFrame, anchorPane.getWidth(), anchorPane.getHeight());
                }
                renderer.render();
            }
        };
        timer.start();
    }
    private void updateBalls(Renderer renderer, float elapsedTime){
        for (Entity entity1 : renderer.getEntities()){
            for (Entity entity2 : renderer.getEntities()){
                if (!entity1.equals(entity2)){
                    Point2D b1Centre = entity1.getCenter();
                    Point2D b2Centre = entity2.getCenter();
                    if (doBallsOverlap(b1Centre, entity1.getHeight(), b2Centre, entity2.getHeight())){
                        double distance = Math.sqrt((b1Centre.getX() - b2Centre.getX()) * (b1Centre.getX() - b2Centre.getX()) +
                                (b1Centre.getY() - b2Centre.getY()) * (b1Centre.getY() - b2Centre.getY()));
                        double overlap = Math.floor(0.5 * (distance - entity1.getHeight() - entity2.getHeight()));
                        Point2D b1Pos = entity1.getPosition();
                        Point2D b2Pos = entity2.getPosition();
                        double xOffset = overlap * (b1Pos.getX() - b2Pos.getX()) / distance;
                        double yOffset = overlap * (b1Pos.getY() - b2Pos.getY()) / distance;
                        entity1.setPosition(new Point2D(b1Pos.getX() - xOffset, b1Pos.getY() - yOffset));
                        entity2.setPosition(new Point2D(b2Pos.getX() + xOffset, b2Pos.getY()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              + yOffset));

                        Point2D b1 = entity1.getCenter();
                        Point2D b2 = entity2.getCenter();
                        double d = Math.sqrt(squareDistance(b1, entity1.getHeight(), b2, entity2.getHeight()));
                        //Normal
                        Point2D normal = new Point2D (((b2.getX() - b1.getX()) / d),
                                ((b2.getY() - b1.getY())) / d);
                        // Tangent
                        Point2D tangent = new Point2D(-normal.getY(), normal.getX());
                        // Dot Product Tangent
                        double dpTan1 = tangent.dotProduct(entity1.getVelocity());
                        double dpTan2 = tangent.dotProduct(entity2.getVelocity());
                        // Dot Product Normal
                        double dpNorm1 = normal.dotProduct(entity1.getVelocity());
                        double dpNorm2 = normal.dotProduct(entity2.getVelocity());
                        // Conservation of momentum in 1D
                        double m1 = (dpNorm1 * (entity1.getMass() - entity2.getMass()) + (2 * entity2.getMass() * dpNorm2)) / (entity1.getMass() + entity2.getMass());
                        double m2 = ((dpNorm1 * entity1.getMass()) + ((entity2.getMass() - entity1.getMass()) * dpNorm2)) / (entity1.getMass() + entity2.getMass());

                        entity1.setVelocity(tangent.multiply(dpTan1).add(normal.multiply(m1)));
                        entity2.setVelocity(tangent.multiply(dpTan2).add(normal.multiply(m2)));
                    }
                    double distance = Math.sqrt(squareDistance(b1Centre, entity1.getHeight(), b2Centre, entity2.getHeight()));
                    addConnectingLine(renderer.getContext(), b1Centre, b2Centre, distance);
                }
            }
        }
    }
    private void addConnectingLine(GraphicsContext context, Point2D ball, Point2D target, double distance){
        double alpha = (-distance / (200)) + 1;
        alpha = alpha >= 0 ? alpha : 0;
        context.setStroke(Color.rgb(2, 80, 207, alpha));
        context.strokeLine(ball.getX(), ball.getY(), target.getX(), target.getY());
    }
    private boolean isPointInBall(Point2D ball, double r1, double x, double y){
        return (ball.getX() - x) * (ball.getX() - x) + (ball.getY() - y) * (ball.getY() - y) < r1 * r1;
    }
    private double squareDistance(Point2D b1, double r1, Point2D b2, double r2){
        return (b1.getX() - b2.getX()) * (b1.getX() - b2.getX()) + (b1.getY() - b2.getY()) * (b1.getY() - b2.getY());
    }
    private boolean doBallsOverlap(Point2D b1, double r1, Point2D b2, double r2){
        double distance = (b1.getX() - b2.getX()) * (b1.getX() - b2.getX()) + (b1.getY() - b2.getY()) * (b1.getY() - b2.getY());
        return distance <= (r1 + r2) * (r1 + r2);
    }
    private void drawLines(GraphicsContext gc){
        gc.beginPath();
        gc.moveTo(30.5, 30.5);
        gc.lineTo(150.5, 30.5);
        gc.lineTo(150.5, 150.5);
        gc.lineTo(30.5, 30.5);
        gc.stroke();
    }
}
