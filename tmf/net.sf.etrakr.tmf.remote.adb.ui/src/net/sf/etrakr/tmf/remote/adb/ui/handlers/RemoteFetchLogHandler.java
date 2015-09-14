/*******************************************************************************
 * Copyright (c) 2015 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bernd Hufmann - Initial API and implementation
 *******************************************************************************/

package net.sf.etrakr.tmf.remote.adb.ui.handlers;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import net.sf.etrakr.eventbus.EventBus;
import net.sf.etrakr.eventbus.ITkrEvent;
import net.sf.etrakr.eventbus.TkrEvent;
import net.sf.etrakr.eventbus.TkrEventException;
import net.sf.etrakr.tmf.remote.adb.core.TmfAdbService;
import net.sf.etrakr.tmf.remote.adb.core.systrace.SystraceOptions;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Command handler for opening the remote fetch wizard.
 *
 * @author Bernd Hufmann
 *
 */
public class RemoteFetchLogHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if (window == null) {
            return false;
        }

        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        IStructuredSelection sec = StructuredSelection.EMPTY;
        if (currentSelection instanceof IStructuredSelection) {
            sec = (IStructuredSelection) currentSelection;
        }

//        try {
//			
//			TmfAdbService service = new TmfAdbService();
//			
//			String str = service.getSystraceOutput();
//			
//			//System.out.println("RemoteFetchLogHandler.execute "+ str);
//			
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put(ITkrEvent.TOPIC_ETRAKR_COMMAND_OPEN_TRACE_DATA_KEY, str); 		
//			
//			Event tkrEvent = TkrEvent.newEvent().topic(ITkrEvent.TOPIC_ETRAKR_COMMAND_OPEN_TRACE).message(map).build();
//			
//			EventBus.getEventBus().postEvent(tkrEvent);
//			
//		} catch (RemoteConnectionException | URISyntaxException | TkrEventException e) {
//			e.printStackTrace();
//			throw new ExecutionException("RemoteFetchLogHandler.execute failed ", e);
//		}

//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
//        Task task1 = new Task ("Task 1");
//        Task task2 = new Task ("Task 2");
//        Task task3 = new Task ("Task 3");
//         
//        System.out.println("The time is : " + new Date());
//         
//        executor.schedule(task1, 10 , TimeUnit.SECONDS);
//        executor.schedule(task2, 20 , TimeUnit.SECONDS);
//        executor.schedule(task3, 30 , TimeUnit.SECONDS);
//         
//        try {
//              executor.awaitTermination(1, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//              e.printStackTrace();
//        }
//         
//        executor.shutdown();
        
        Timer timer = new Timer();
        
        /* the time must ref AdbSession.executeRequest, default is await 5 sec */
        timer.schedule(new Task("Profile Start"), 0, 1000);
        
        //new Thread(new Task("Profile Start")).run();
        //timer.cancel();
        return null;
    }
    
	class Task extends TimerTask implements Runnable {
		
		private String name;

		public Task(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public void run() {
			
			try {

				SystraceOptions options = SystraceOptions.newSystraceOptions().BufferSize(1024).Duration(1);
				
				String str = TmfAdbService.init().push(options).go();
				
				// System.out.println("RemoteFetchLogHandler.execute "+ str);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put(ITkrEvent.TOPIC_ETRAKR_COMMAND_OPEN_TRACE_DATA_KEY, str);

				Event tkrEvent = TkrEvent.newEvent().topic(ITkrEvent.TOPIC_ETRAKR_COMMAND_OPEN_TRACE).message(map).build();

				EventBus.getEventBus().postEvent(tkrEvent);

			} catch (RemoteConnectionException | URISyntaxException | TkrEventException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
