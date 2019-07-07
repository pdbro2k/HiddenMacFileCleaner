/**
 * HiddenMacFileCleaner.java - is a simple JFileChooser based class that deletes hidden Mac/OSX files
 * @author Patrick D. Brookshire
 * @version 1.0.0
 */
package org.pb.general.fileOps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class HiddenMacFileCleaner {

	public static void main(String[] args) {
		// load localized data
		ResourceBundle rb = ResourceBundle.getBundle("resources.Messages");
				
		// create file chooser
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(rb.getString("SEL_DIR_W_HIDDEN_F"));
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showDialog(null, rb.getString("CLEAN_DIR")) == JFileChooser.APPROVE_OPTION) {
			// remove files and print all removals to the console
			int cnt = 0;
			File[] dirs = chooser.getSelectedFiles();
			for (File dir: dirs) {
				System.out.println("Processing "+dir.getPath());

				// process subdirectories
				try (Stream<Path> walk = Files.walk(Paths.get(dir.getPath()))) {
					List<Path> result = walk.filter(path -> isHiddenMacFile(path))
							.collect(Collectors.toList());	    		  
					for (Path path: result) {
						System.out.println("Deleting "+path);
						Files.delete(path);
						++cnt;
					}
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e);
				}

			}
			// display stats
			System.out.println("---");
			System.out.println("Deleted "+cnt+" files");
			JOptionPane.showMessageDialog(null, cnt + " " + rb.getString("RESULT_MSG"));
		}
	}

	protected static boolean isHiddenMacFile(Path path) {
		String fileName = path.getFileName().toString();
		if (fileName.startsWith("._"))
			return true;
		if (fileName.toUpperCase().contentEquals(".DS_STORE"))
			return true;
		return false;
	}
}
