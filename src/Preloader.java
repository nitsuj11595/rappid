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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedTransferQueue;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/** 
 * Preloader using ForkJoinPool
 * <p>
 * Preforms tasks in seperate threads.
 * Displays a basic loading screen while tasks are being preformed.
 * Subclass to define your own draw method.
 * </p>
 */

public class Preloader{
    private PApplet applet;
    private ForkJoinPool fjPool;
    private LinkedTransferQueue<Task> tasks;

    /**
     * Class constructor
     */
    public Preloader(PApplet applet){
	this.applet = applet;
	tasks = new LinkedTransferQueue<Task>();
	fjPool = new ForkJoinPool();
	//fjPool = ForkJoinPool.commonPool();
	applet.registerMethod("draw", this);
    }

    /**
     * Default draw method
     */
    public void draw(){
	//Draw while tasks are being executed
	if(!fjPool.isQuiescent()){
	    applet.background(0xFF000000);
	    applet.fill(0xFFFFFFFF);
	    applet.pushMatrix();
	    applet.translate(applet.width/2, applet.height/2);
	    applet.scale(4.0f);
	    applet.textAlign(applet.CENTER, applet.CENTER);
	    applet.text("Loading...", 0, 0);
	    applet.popMatrix();
	  
	    applet.textAlign(applet.LEFT, applet.BOTTOM);
	    applet.text(fjPool.toString(), 0, applet.height);
	}
    }

    /**
     * Queues a task for execution
     */
    public void addTask(String name){
	addTask(applet, name);
    }

    /**
     * Queues a task for execution
     */
    public void addTask(Object parent, String name){
	try{
	    Method method = parent.getClass().getMethod(name);
	    parent.getClass().getMethod(name);
	    tasks.add(new Task(parent, method));
	}
	catch(NoSuchMethodException e){
	    e.printStackTrace();
	}
    }

    /**
     * Submits all queued tasks to ForkJoinPool
     */
    public void start(){
	while(!tasks.isEmpty()){
	    try{
		fjPool.submit(tasks.take());
	    }
	    catch(InterruptedException e){
		e.printStackTrace();
	    }
	}
    }
  
    private class Task implements Runnable{
	private Method method;
	private Object object;
    
	Task(Object object, Method method){
	    this.object = object;
	    this.method = method;
	}
    
	public void run(){
	    try{
		method.invoke(object, new Object[0]);
	    }
	    catch(Exception e){
		e.printStackTrace();
	    }
	}
    }

    /**
     * Return true if tasks are still being executed
     */
    public boolean isLoading(){
	return(!fjPool.isQuiescent());
    }
    
    /**
     * Return ForkJoinPool
     */
    public ForkJoinPool getForkJoinPool(){
	return(fjPool);
    }
}
