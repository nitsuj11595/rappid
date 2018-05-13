//  This file is part of Rappid library for Processing
//  Copyright (C) 2018 Justin Wong
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
// 
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package rappid;

import processing.core.*;
import java.io.*;
import java.lang.NullPointerException;
import processing.awt.PGraphicsJava2D;

/**
 * PGraphics wrapper to preload images from disk.
 * Instead of drawing every frame, drawing is only prefomed the first time the application is run.
 * The PGraphics is then saved to disk so that it may be loaded as an image on subsequent runs.
 * This can save processing power when complex PShape objects are used.
 * <p>
 * Currently, only 2D graphics are supported.
 * <p>
 * All drawing must be preformed in a {@code draw()} method.
 * Everything is called between {@code beginDraw()} and {@code endDraw()},
 * so you do not need to call them yourself.
 * <p>
 * Call {@code load()} to attempt to load a pregenerated image from disk.
 * If file does not exist or fails to load, {@code redraw()}
 * is automatically called to draw and save the image.
 * <p>
 * <h1> Use with Anonymous Class</h1>
 * Images may be loaded using anonymous classes.
 * This can be used to easily define the {@code draw} method
 * <pre>
 * PImage theImage = new RasterFile(this, width, height){
 *     void draw(){
 *         /&#42 Your code here &#42/
 *     }
 * }.load("image.png"); //File to load from
 * </pre>
 * <p>
 * <h1>Subclassing RasterFile</h1>
 * RasterFile may be subclassed: 
 * <pre>
 * class RasterFileExtension extends RasterFile{
 *     RasterFileExtension(PApplet applet, width, height){
 *         super(applet, width, height);
 *         this.load("image.png"); //File path to load from
 *     }
 *
 *     void draw(){
 *         /&#42 Your code here &#42/
 *     }
 * };
 * </pre>
 * <p>
 */

public abstract class RasterFile extends PGraphicsJava2D{
    static PApplet applet = null;
    File file = null;
    
    /**
     * Class constructor: width and height must be positive values
     */

    public RasterFile(PApplet parent, int width, int height){
	this.applet = parent;
	setParent(applet);
	setPrimary(false);
	setSize(width, height);
	//System.out.println("Raster file " + this.width + ", " + this.height);
    }
    
    /**
     * 
     */
    
    abstract public void draw();

    /**
     * Call draw method and save image.  
     */
    
    public RasterFile redraw(){
	this.beginDraw();
	this.draw();
	this.endDraw();
	if(file != null){
	    save(file.getAbsolutePath());
	}
	else{
	    System.err.println("Warning: Output file was not defined");
	    System.err.println("Call setFile() to specify file path");
	}
	return this;
    }

    /**
     * Set file path.
     * Image is saved to this path when redraw() is called
     * Image is loaded from this path when load() is called
     */
    
    public RasterFile setFile(String name){
	if(name.charAt(0) == '/'){
	    file = new File(name);
	}
	else{
	    file = new File(applet.dataPath(""), name);
	}
	return this;
    }


    
    /**
     * Attempt to load image from disk.
     * redraw() is called if loading fails.
     */
    public RasterFile load(String name){
	//Set file
	this.setFile(name);
	System.out.println("Loading \"" + file.getAbsolutePath() + "\"");
	return this.load();
    }

    /**
     * Attempt to load image from disk.
     * redraw() is called if loading fails.
     */
    public RasterFile load(){
	//Attempt to load file if it exists.  Redraw if there is error
	try{
	    if(!file.exists()){
		throw new IOException("File not found: \"" + file.getName() + "\"");
	    }
	    PImage image = applet.loadImage(file.getAbsolutePath());
	    if(image == null){
		throw new IOException("Failure to load: \"" + file.getName() + "\"");
	    }
	    //Verify size
	    if(image.width != this.width || image.height != this.height){
		throw new IOException("Inconsistent size: \"" + file.getName() + "\"");
	    }
	    //Draw image if no errors
	    beginDraw();
	    background(image);
	    endDraw();
	}
	catch(IOException e){
	    //Redraw on error
	    System.out.print("Calling redraw due to ");
	    System.out.println(e.getMessage());
	    this.redraw();
	}
	return this;
    }
}
