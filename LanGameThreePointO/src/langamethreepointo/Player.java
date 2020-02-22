/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package langamethreepointo;

import javax.swing.JPanel;

/**
 *
 * @author ah91099
 */
public class Player extends MovableObject{
   
    private int horizntalDir;
    private int verticalDir;
    private int screenHeight;
    private int screenWidth;


    
    public Player(String owner, int id, String whatAmI, JPanel mainPanel, String filename, int x, int y, int width, int height, int screenHeight, int screenWidth){
        super(owner, id, whatAmI, mainPanel,filename,x,y,width,height);
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

    }
    public int getHorizntalDir() {
        return horizntalDir;
    }

    public int getVerticalDir() {
        return verticalDir;
    }    


    public void setHorizntalDir(int horizntalDir) {
        this.horizntalDir = horizntalDir;
    }

    public void setVerticalDir(int verticalDir) {
        this.verticalDir = verticalDir;
    }



    
    public void move(){
        
        
        if(getVerticalDir()== 1 && getY() > 0){
            moveUp();
            //setHeight(getHeight()-1);
        }
        if(getVerticalDir()== -1 && getY() < screenHeight-getHeight()){
            moveDown();
            //setHeight(getHeight()+1);            
        }
        if(getHorizntalDir()== -1 && getX() > 0){
            moveLeft();
            //setWidth(getWidth()-1);
        }
        if(getHorizntalDir()== 1 && getX() < screenWidth-getWidth()){
            moveRight();
            //setWidth(getWidth()+1);
        }   
        //System.out.println("X : " + getX());
        //System.out.println("Y : " + getY());
    }
        
    
    
    
}
