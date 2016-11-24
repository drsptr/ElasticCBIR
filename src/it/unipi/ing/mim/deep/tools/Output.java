package it.unipi.ing.mim.deep.tools;

import it.unipi.ing.mim.deep.ImgDescriptor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Output {

	public static final int COLUMNS = 5;

	public static void toHTML(List<ImgDescriptor> ids, File outputFile) {
		String html = "<html>\n<body>\n<table align='center'>\n";

		for (int i = 0; i < ids.size(); i++) {
			//System.out.println(i + " - " + (float) ids.get(i).getDist() + "\t" + ids.get(i).getId() );
			
			if (i % COLUMNS == 0) {
				if (i != 0)
					html += "</tr>\n";
				html += "<tr>\n";
			}
			html += "<td><img align='center' border='0' height='160' width='160' title=\"Id: " + ids.get(i).getId() + "\nScore: "
			        + ids.get(i).getDist() + "\" src='" + ids.get(i).getUri() + "'></td>\n";
		}
		if (ids.size() != 0)
			html += "</tr>\n";

		html += "</table>\n</body>\n</html>";
		
		try {
	        string2File(html, outputFile);
			System.out.println("html generated");
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}

	private static void string2File(String text, File file) throws IOException {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(text);
		} finally {
			if (fileWriter != null)
				fileWriter.close();
		}
	}
}
