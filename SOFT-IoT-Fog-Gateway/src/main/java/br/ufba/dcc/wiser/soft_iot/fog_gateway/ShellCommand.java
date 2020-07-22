package br.ufba.dcc.wiser.soft_iot.fog_gateway;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

public class ShellCommand{
	public void restartBundle(String bundleJarFilePath) throws BundleException {
		Bundle bundle = null;
		BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		bundle = bundleContext.getBundle(bundleJarFilePath);
		bundle.stop();
		System.out.println("Bundle '"+"' stopped");
		bundle.update();
		System.out.println("Bundle '"+"' updated");
		bundle.start();
		System.out.println("Bundle '"+"' started");
		System.out.println(bundle.getSymbolicName());
	}
}
