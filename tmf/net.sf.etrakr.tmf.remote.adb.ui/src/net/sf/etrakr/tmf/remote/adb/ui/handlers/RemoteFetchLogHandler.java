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

import net.sf.etrakr.tmf.remote.adb.core.TmfAdbService;


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

        TmfAdbService service;
		try {
			
			service = new TmfAdbService();
			
			String str = service.getSystraceOutput();
			
			System.out.println("RemoteFetchLogHandler.execute "+ str);
			
			
		} catch (RemoteConnectionException | URISyntaxException e) {
			e.printStackTrace();
			throw new ExecutionException("RemoteFetchLogHandler.execute failed ", e);
		}

        return null;
    }
}
