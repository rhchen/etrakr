/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.etrakr.remote.adb.core.test.adb;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystraceVersionDetector extends Job {
	
    public static final int SYSTRACE_V1 = 1;
    public static final int SYSTRACE_V2 = 2;

    private final IDevice mDevice;
    private List<SystraceTag> mTags;

    public SystraceVersionDetector(String name, IDevice mDevice, List<SystraceTag> mTags) {
		super(name);
		this.mDevice = mDevice;
		this.mTags = mTags;
	}

	@Override
    public IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Checking systrace version on device..",
                IProgressMonitor.UNKNOWN);

        CountDownLatch setTagLatch = new CountDownLatch(1);
        CollectingOutputReceiver receiver = new CollectingOutputReceiver(setTagLatch);
        String cmd = "atrace --list_categories";
        
        try {
		
        	mDevice.executeShellCommand(cmd, receiver);
        	
        	setTagLatch.await(5, TimeUnit.SECONDS);
		
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException | InterruptedException e) {
			e.printStackTrace();
		}

        String shellOutput = receiver.getOutput();
        mTags = parseSupportedTags(shellOutput);

        for(SystraceTag tag : mTags){
        	
        	String p = tag.info + " " + tag.tag;
        	
        	System.out.println("SystraceVersionDetector.run " + p);
        }
        monitor.done();
        
        return Status.OK_STATUS;
    }

    public int getVersion() {
        if (mTags == null) {
            return SYSTRACE_V1;
        } else {
            return SYSTRACE_V2;
        }
    }

    public List<SystraceTag> getTags() {
        return mTags;
    }

    private List<SystraceTag> parseSupportedTags(String listCategoriesOutput) {
        if (listCategoriesOutput == null) {
            return null;
        }

        if (listCategoriesOutput.contains("unknown option")) {
            return null;
        }

        String[] categories = listCategoriesOutput.split("\n");
        List<SystraceTag> tags = new ArrayList<SystraceTag>(categories.length);

        Pattern p = Pattern.compile("([^-]+) - (.*)"); //$NON-NLS-1$
        for (String category : categories) {
            Matcher m = p.matcher(category);
            if (m.find()) {
                tags.add(new SystraceTag(m.group(1).trim(), m.group(2).trim()));
            }
        }

        return tags;
    }
}
